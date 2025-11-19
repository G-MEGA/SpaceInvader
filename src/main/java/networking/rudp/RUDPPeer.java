package networking.rudp;

import networking.rudp.PacketData.PacketData;
import networking.rudp.PacketData.PacketDataConnect;
import networking.rudp.PacketData.PacketDataDisconnect;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class RUDPPeer implements AutoCloseable{

    // 1. 원하는 IP로 보낼 수 있어야함
    // 2. 옵저버 패턴으로 패킷 리시브 가능해야하며..... 그럼 리시브가 메인 스레드에서 돌아야 하나 ㄴㄴ 대신 대기열을 만들어서
    //    메인이 원하는 타이밍에 받을 수 있도록 해라.
    // 3. 데이터 패킷 받으면 무조건 ACK 보냄. ACK가 유실되어서 계속 보내는 것일 지도
    //
    // 스레드 종류는 3가지(이 클래스 입장에서)
    // - 발신 스레드(메인 스레드일 확률이 높음)
    // - 재전송 스레드(여기서 하트비트도 날림)
    // - 수신 스레드(여기서 ACK 발신함)
    //
     // - 다만 셧다운 시에는 셧다운 훅 스레드가 동작해야하므로 이를 위해서 serializerForShutdown까지 추가

    public static final int BUFFER_SIZE = 1400;
    private static final long RETRANSMISSION_TIMEOUT = 250;  // 서버랑 통신할 때는 핑이 기본 0.2초니까 조금 더 긴 0.25초로
    private static final long HEARTBEAT_INTERVAL = 3_000;
    private static final long HEARTBEAT_TIMEOUT = 10_000;
    private static final long DISCONNECTING_DURATION = 3_000;  // 더 늘려야 하나 싶은데 일단은 이렇게 하자

    private Set<IRUDPPeerListener> listeners = new HashSet<>();

    private final PacketSerializer serializerForSend = new PacketSerializer();
    private final PacketSerializer serializerForReceive = new PacketSerializer();
    private final PacketSerializer serializerForRetransmit = new PacketSerializer();

    private final PacketSerializer serializerForShutdown = new PacketSerializer();

    private final DatagramChannel channel;
    private final Selector selector;

    private final Map<InetSocketAddress, Connection> connections = new ConcurrentHashMap<>();

    private volatile boolean running = true;
    private int localPort;

    public boolean printLog = false;

    public RUDPPeer(int localPort) throws Exception {
        this.selector = Selector.open();
        this.channel = DatagramChannel.open();
        this.channel.configureBlocking(false);
        this.channel.socket().bind(new InetSocketAddress(localPort));
        this.channel.register(selector, SelectionKey.OP_READ);

        this.localPort = localPort;
        System.out.printf("피어 시작. 로컬 포트: %d\n", this.localPort);

        //셧다운 대비
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("셧다운으로 인한 모든 피어와의 disconnect 작동");
            for(Connection c : connections.values()){
                try {
                    disconnect(c, serializerForShutdown);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }));
    }

    public void start() {
        // 패킷 수신 스레드
        Thread receiverThread = new Thread(this::receiveLoop);
        receiverThread.setDaemon(true);
        receiverThread.start();

        // 재전송 스레드
        Thread retransmissionThread = new Thread(this::retransmitLoop);
        retransmissionThread.setDaemon(true);
        retransmissionThread.start();
    }

    private void sendPacket(Connection connection, Packet packet, PacketSerializer packetSerializer) throws Exception {
        sendPacket(connection.getAddress(), packet, packetSerializer);
    }
    private void sendPacket(InetSocketAddress address, Packet packet, PacketSerializer packetSerializer) throws Exception {
        ByteBuffer buffer = ByteBuffer.wrap(packetSerializer.serialize(packet));
        channel.send(buffer, address);
        if(printLog){
            System.out.printf("%s 발신 %s\n", localPort, packet.getType());
        }
    }

    public boolean isConnected(InetSocketAddress address) {
        return connections.containsKey(address);
    }

    //region 발신 스레드
    public void processReceivedData(){
        if(listeners.isEmpty()){
            System.out.println("RUDPPeer : There is no listener");
            return;
        }

        for (Connection connection : connections.values()){
            for(int i = 0; i < connection.getReceivedData().size(); i++){
                PacketData packetData = connection.getReceivedData().poll();

                boolean processed = processPacket(packetData, connection);

                // 모든 Listener에서 처리 수락하면 큐에서 제거된 채로 진행
                // 하나라도 처리 거부시 큐에 다시 넣음
                if(!processed){
                    connection.getReceivedData().offer(packetData);
                }
            }
        }
    }
    private boolean processPacket(PacketData packetData, Connection connection) {
        for (IRUDPPeerListener listener : listeners) {
            // 반복문 내부의 복잡한 로직을 단일 메서드 호출로 대체
            // "처리에 실패했거나 거부했다면(!success) 즉시 중단(return false)"
            if (!notifyListener(listener, connection, packetData)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 단일 리스너에게 이벤트를 전파하고, 계속 진행할지 여부를 반환합니다.
     * 반환값: true(계속 진행/성공), false(중단/거부)
     */
    private boolean notifyListener(IRUDPPeerListener listener, Connection connection, PacketData packetData) {
        // 1. Null 체크 (continue 로직 제거)
        if (listener == null) {
            System.err.println("RUDPPeer : listener is null");
            return true; // null이면 무시하고 다음 리스너로 진행(continue와 동일 효과)
        }

        // 2. 타입별 분기 (메서드 추출로 복잡도 격리)
        return dispatchEvent(listener, connection, packetData);
    }

    /**
     * 패킷 타입에 따라 적절한 리스너 메서드를 호출합니다.
     */
    private boolean dispatchEvent(IRUDPPeerListener listener, Connection connection, PacketData packetData) {
        if (packetData instanceof PacketDataDisconnect) {
            return listener.onDisconnected(this, connection);
        }

        if (packetData instanceof PacketDataConnect) {
            return listener.onConnected(this, connection);
        }

        // 기본값 (일반 데이터)
        return listener.onReceived(this, connection, packetData);
    }

    public void connect(InetSocketAddress address) throws Exception {
        if (isConnected(address)) {
            return;
        }
        sendPacket(address, new Packet(Packet.PacketType.CONNECT), serializerForSend);
    }
    public void disconnect(Connection connection) throws Exception {
        disconnect(connection, serializerForSend);
    }
    public void disconnectAll(String exceptionTag) throws Exception {
        for (Connection connection : connections.values()){
            if(exceptionTag != null && connection.tag.contains(exceptionTag)) continue;

            disconnect(connection);
        }
    }
    private void disconnect(Connection connection, PacketSerializer packetSerializer) throws Exception {
        if(connection.isDisconnecting()) return;

        connection.disconnect();
        connection.getReceivedData().offer(new PacketDataDisconnect());  // 내 스스로에게 디스커넥 알림
        sendPacket(connection, new Packet(Packet.PacketType.DISCONNECT), packetSerializer);  // 상대에게 디스커넥 알림
    }

    public void send(Connection connection, PacketData data) throws Exception {
        int sequenceNumber = connection.getNextSequenceNumber().getAndIncrement();
        Packet packet = new Packet(sequenceNumber, serializerForSend.serialize(data));
        connection.getSentPackets().put(sequenceNumber, new SentPacketInfo(packet));

        sendPacket(connection, packet, serializerForSend);
    }
    public void broadcast(PacketData data, String exceptionTag) throws Exception {
        for (Connection connection : connections.values()){
            if(connection.isDisconnecting() || exceptionTag != null && connection.tag.contains(exceptionTag)) continue;

            send(connection, data);
        }
    }
    public void broadcastAboutTag(String targetTag, PacketData data) throws Exception {
        for (Connection connection : connections.values()){
            if(connection.isDisconnecting() || !connection.tag.contains(targetTag)) continue;

            send(connection, data);
        }
    }

    public void addListener(IRUDPPeerListener listener) {
        listeners.add(listener);
    }
    public void removeListener(IRUDPPeerListener listener) {
        listeners.remove(listener);
    }
    //endregion

    //region 수신 스레드
    private void receiveLoop() {
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
        try {
            while (running) {
                if (selector.select(100) > 0) {
                    Set<SelectionKey> selectedKeys = selector.selectedKeys();
                    Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

                    while (keyIterator.hasNext()) {
                        SelectionKey key = keyIterator.next();
                        if (key.isReadable()) {
                            // 패킷 하나 읽기
                            buffer.clear();
                            InetSocketAddress senderAddress = (InetSocketAddress) channel.receive(buffer);
                            buffer.flip();
                            Packet receivedPacket = serializerForReceive.deserializePacket(buffer.array());
                            // 패킷 처리
                            handlePacket(receivedPacket, senderAddress);
                        }
                        keyIterator.remove();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.printf("수신 루프 오류 %s\n", e.getMessage());
        }
    }

    private void handlePacket(Packet packet, InetSocketAddress senderAddress) throws Exception {
        if (printLog) {
            System.out.printf("%s 수신 %s\n", localPort, packet.getType());
        }

        Connection connection = connections.get(senderAddress);

        // 1. 커넥션이 없는 경우 (핸드셰이크 처리)
        if (connection == null) {
            handleNewConnection(packet, senderAddress);
            return; // 핸드셰이크 패킷은 여기서 처리 끝
        }

        // 2. 커넥션이 있지만 연결 종료 중인 경우
        if (connection.isDisconnecting()) {
            return;
        }

        // 3. 정상 패킷 처리
        processPacket(packet, connection);
    }

// --- 추출된 메서드들 ---

    /**
     * 연결이 없는 상태에서 들어온 패킷(핸드셰이크)을 처리합니다.
     */
    private void handleNewConnection(Packet packet, InetSocketAddress senderAddress) throws Exception {
        Packet.PacketType type = packet.getType();

        if (type == Packet.PacketType.CONNECT) {
            sendPacket(senderAddress, new Packet(Packet.PacketType.CONNECT_ACK), serializerForReceive);
            return;
        }

        if (type == Packet.PacketType.CONNECT_ACK) {
            sendPacket(senderAddress, new Packet(Packet.PacketType.CONNECT_ACKACK), serializerForReceive);
            createAndRegisterConnection(senderAddress);
            return;
        }

        if (type == Packet.PacketType.CONNECT_ACKACK) {
            createAndRegisterConnection(senderAddress);
        }

        // 그 외의 패킷은 연결이 없는 상태에서는 무시
    }

    /**
     * 새로운 커넥션을 생성하고 맵에 등록합니다. (중복 코드 제거)
     */
    private void createAndRegisterConnection(InetSocketAddress senderAddress) {
        Connection newConnection = new Connection(senderAddress);
        connections.put(newConnection.getAddress(), newConnection);

        // 연결 성립 이벤트 발생
        newConnection.getReceivedData().offer(new PacketDataConnect());
    }


    private void processPacket(Packet packet, Connection connection) throws Exception{
        if (packet.getType() == Packet.PacketType.DATA) {
            processDataPacket(packet, connection);
        } else if (packet.getType() == Packet.PacketType.ACK) {
            // ACK 받은 패킷은 미확인 목록에서 제거
            connection.getSentPackets().remove(packet.getSeqNumber());
            System.out.printf("ACK %d 수신 완료. 미확인 패킷 %d개 남음.\n", packet.getSeqNumber(), connection.getSentPackets().size());
        } else if (packet.getType() == Packet.PacketType.HEARTBEAT) {
            //Heartbeat에 대한 ACK 날리기
            sendPacket(connection, new Packet(Packet.PacketType.HEARTBEAT_ACK), serializerForReceive);
        } else if (packet.getType() == Packet.PacketType.HEARTBEAT_ACK) {
            // 딱히 할 것 없음
        } else if (packet.getType() == Packet.PacketType.DISCONNECT) {
            disconnect(connection);
        }

        //뭐든지 수신 받으면 커넥션 하트비트 갱신
        connection.updateLastHeartbeat();
    }
    private void processDataPacket(Packet packet, Connection connection) throws Exception {
        // 1. ACK 전송 (단순 추출)
        sendAck(packet, connection);

        // 2. 패킷 버퍼링 (받아야 할 순서보다 작으면 이미 처리된 것이므로 무시)
        bufferPacketIfNew(packet, connection);

        // 3. 순서가 맞는 패킷들 처리 (핵심 로직 개선)
        processOrderedPackets(connection);
    }

// --- 추출된 헬퍼 메서드들 ---

    private void sendAck(Packet packet, Connection connection) throws Exception {
        // ACK는 중복 패킷이어도 보내야 하므로 항상 실행
        sendPacket(connection, new Packet(packet.getSeqNumber()), serializerForReceive);
    }

    private void bufferPacketIfNew(Packet packet, Connection connection) {
        int seqNumber = packet.getSeqNumber();
        // 기대값보다 작다면(이미 처리된 패킷) 버퍼링하지 않음
        if (seqNumber >= connection.getExpectedReceivedSequenceNumber()) {
            connection.getReceivedPacketsWaitingForSequence().put(seqNumber, packet);
        }
    }

    private void processOrderedPackets(Connection connection) throws Exception {
        Map<Integer, Packet> waitingPackets = connection.getReceivedPacketsWaitingForSequence();

        // while 루프: '기대하는 순번'의 패킷이 Map에 존재하는 동안 계속 실행
        // 기존의 [List변환 -> 정렬 -> Loop -> break] 과정을 이 while문 하나로 대체
        while (waitingPackets.containsKey(connection.getExpectedReceivedSequenceNumber())) {

            // 1. Map에서 바로 꺼내고 제거 (복잡도와 메모리 사용량 감소)
            Packet nextPacket = waitingPackets.remove(connection.getExpectedReceivedSequenceNumber());

            // 2. 데이터 처리
            PacketData data = serializerForReceive.deserializePacketData(nextPacket.getData());
            connection.getReceivedData().offer(data);

            // 3. 기대 순번 증가
            connection.incrementExpectedReceivedSequenceNumber();
        }
    }
    //endregion

    //region 재전송 스레드
    private void retransmitLoop() {
        while (running) {
            // 메인 루프는 오직 '주기적인 실행'과 '예외 방어'만 담당합니다.
            if (!executeMaintenanceCycle()) {
                break; // 인터럽트 발생 시 루프 종료
            }
        }
    }

    /**
     * 한 번의 유지보수 주기를 실행합니다.
     * @return 계속 실행 여부 (false면 스레드 종료)
     */
    private boolean executeMaintenanceCycle() {
        try {
            Thread.sleep(RETRANSMISSION_TIMEOUT);

            // 로직 수행 (메서드 추출로 복잡도 격리)
            performMaintenanceTasks();

            return true;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            running = false;
            return false;
        } catch (Exception e) {
            System.err.printf("재전송 오류: %s\n", e.getMessage());
            return true; // 일반 오류는 무시하고 계속 실행
        }
    }

    /**
     * 실제 로직을 수행하는 오케스트레이터
     */
    private void performMaintenanceTasks() {
        long currentTime = System.currentTimeMillis();

        // 모든 커넥션에 대해 작업 수행
        for (Connection c : connections.values()) {
            maintainSingleConnection(c, currentTime);
        }

        processRemovalDisconnected(currentTime);
    }

    /**
     * 단일 커넥션에 대한 유지보수 작업
     * (continue를 return으로 대체하여 구조 단순화)
     */
    private void maintainSingleConnection(Connection connection, long currentTime) {
        processRetransmission(connection, currentTime);

        // Guard Clause: 연결 종료 중이면 하트비트 생략
        if (connection.isDisconnecting()) {
            return; // continue 대신 return 사용 -> 흐름이 명확해짐
        }

        processHeartbeat(connection, currentTime);
    }
    private void processRetransmission(Connection c, long currentTime) {
        try {
            //재전송. disconnect 패킷도 재전송이 되어야하니 disconnecting이어도 동작
            for(SentPacketInfo info : c.getSentPackets().values()){
                if(currentTime - info.getTimestamp() > RETRANSMISSION_TIMEOUT){
                    System.err.printf("타임아웃! 재전송: %s\n", info.packet);
                    sendPacket(c, info.getPacket(), serializerForRetransmit);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private void processHeartbeat(Connection c, long currentTime) {
        try {
            long fromLastHeartbeat = currentTime - c.getLastHeartbeat().get();
            if(fromLastHeartbeat >= HEARTBEAT_TIMEOUT){
                // 너무 오래 끊겼으므로 disconnect
                disconnect(c);
            } else if (fromLastHeartbeat >= HEARTBEAT_INTERVAL) {
                // 하트비트 전송
                sendPacket(c, new Packet(Packet.PacketType.HEARTBEAT), serializerForRetransmit);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private void processRemovalDisconnected(long currentTime) {
        // disconnect 후 몇초 지났는지 확인 후 제거 로직
        for(InetSocketAddress address : connections.keySet()){
            if(!connections.get(address).isDisconnecting()) continue;

            long fromDisconnecting = currentTime - connections.get(address).getLastHeartbeat().get();
            if(fromDisconnecting >= DISCONNECTING_DURATION){
                connections.remove(address);
            }
        }
    }
    //endregion

    @Override
    public void close() throws Exception {
        running = false;
        selector.close();
        channel.close();
    }

//    // --- 데모 실행을 위한 main 메서드 ---
//    public static void main(String[] args) throws Exception {
//        try (RUDPPeer peer1 = new RUDPPeer("Peer1", 9001, "127.0.0.1", 9002);
//             RUDPPeer peer2 = new RUDPPeer("Peer2", 9002, "127.0.0.1", 9001)) {
//
//            peer1.start();
//            peer2.start();
//
//            System.out.println("--- 양방향 통신 시작 ---");
//
//            peer1.send("안녕하세요, Peer2!");
//            Thread.sleep(300);
//
//            peer2.send("네, 안녕하세요 Peer1님.");
//            Thread.sleep(300);
//
//            peer1.send("RUDP 통신 테스트입니다.");
//            Thread.sleep(1000);
//
//            System.out.println("--- 통신 테스트 종료 ---");
//        }
//    }
}