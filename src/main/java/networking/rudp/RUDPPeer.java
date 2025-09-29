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

    public static final int BUFFER_SIZE = 1400;
    private static final long RETRANSMISSION_TIMEOUT = 250;  // 서버랑 통신할 때는 핑이 기본 0.2초니까 조금 더 긴 0.25초로
    private static final long HEARTBEAT_INTERVAL = 3_000;
    private static final long HEARTBEAT_TIMEOUT = 10_000;
    private static final long DISCONNECTING_DURATION = 3_000;  // 더 늘려야 하나 싶은데 일단은 이렇게 하자

    private Set<IRUDPPeerListener> listeners = new HashSet<>();

    private final PacketSerializer serializerForSend = new PacketSerializer();
    private final PacketSerializer serializerForReceive = new PacketSerializer();
    private final PacketSerializer serializerForRetransmit = new PacketSerializer();

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

    //region 발신 스레드

    public void processReceivedData(){
        for (Connection connection : connections.values()){
            while(!connection.getReceivedData().isEmpty()){
                PacketData packetData = connection.getReceivedData().poll();

                for(IRUDPPeerListener listener : listeners){
                    if(listener == null) continue;

                    if(packetData instanceof PacketDataDisconnect){
                        listener.onDisconnected(this, connection);
                    }
                    else if(packetData instanceof PacketDataConnect){
                        listener.onConnected(this, connection);
                    }
                    else{
                        listener.onReceived(this, connection, packetData);
                    }
                }
            }
        }
    }

    public void connect(InetSocketAddress address) throws Exception {
        if (connections.containsKey(address)) {
            return;
        }
        sendPacket(address, new Packet(Packet.PacketType.CONNECT), serializerForSend);
    }
    public void disconnect(Connection connection) throws Exception {
        connection.disconnect();
        connection.getReceivedData().offer(new PacketDataDisconnect());  // 내 스스로에게 디스커넥 알림
        sendPacket(connection, new Packet(Packet.PacketType.DISCONNECT), serializerForSend);  // 상대에게 디스커넥 알림
    }
    public void disconnectAll(String exceptionTag) throws Exception {
        for (Connection connection : connections.values()){
            if(connection.isDisconnecting()) continue;
            if(exceptionTag != null && connection.tag.contains(exceptionTag)) continue;

            disconnect(connection);
        }
    }

    public void send(Connection connection, PacketData data) throws Exception {
        int sequenceNumber = connection.getNextSequenceNumber().getAndIncrement();
        Packet packet = new Packet(sequenceNumber, serializerForSend.serialize(data));
        connection.getSentPackets().put(sequenceNumber, new SentPacketInfo(packet));

        sendPacket(connection, packet, serializerForSend);
    }
    public void broadcast(PacketData data, String exceptionTag) throws Exception {
        for (Connection connection : connections.values()){
            if(connection.isDisconnecting()) continue;
            if(exceptionTag != null && connection.tag.contains(exceptionTag)) continue;

            send(connection, data);
        }
    }
    public void broadcastAboutTag(String targetTag, PacketData data) throws Exception {
        for (Connection connection : connections.values()){
            if(connection.isDisconnecting()) continue;
            if(!connection.tag.contains(targetTag)) continue;

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
                            if(printLog){
                                System.out.printf("%s 수신 %s\n", localPort, receivedPacket.getType());
                            }
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
        Connection connection = connections.get(senderAddress);
        //커넥션이 없을 경우
        if (connection == null) {
            if(packet.getType() == Packet.PacketType.CONNECT){
                sendPacket(senderAddress, new Packet(Packet.PacketType.CONNECT_ACK), serializerForReceive);

                return;
            } else if(packet.getType() == Packet.PacketType.CONNECT_ACK){
                sendPacket(senderAddress, new Packet(Packet.PacketType.CONNECT_ACKACK), serializerForReceive);

                connection = new Connection(senderAddress);
                connections.put(connection.getAddress(), connection);
                connection.getReceivedData().offer(new PacketDataConnect());
            } else if(packet.getType() == Packet.PacketType.CONNECT_ACKACK){
                connection = new Connection(senderAddress);
                connections.put(connection.getAddress(), connection);
                connection.getReceivedData().offer(new PacketDataConnect());
            }
            else{
                return;
            }
        }
        // 커넥션이 있는데 Disconnect 중일 경우 아무것도 할 필요 없음
        if(connection.isDisconnecting()) return;

        // 패킷 처리
        if (packet.getType() == Packet.PacketType.DATA) {
            // ACK 전송
            sendPacket(connection, new Packet(packet.getSeqNumber()), serializerForReceive);

            // receivedPacketsWaitingForSequence에 패킷 넣기
            // 기대했던 순번보다 작다면 이미 받아서 처리까지 완료 했다는 뜻이므로 또 넣을 필요 없음
            if(packet.getSeqNumber() >= connection.getExpectedReceivedSequenceNumber()){
                connection.getReceivedPacketsWaitingForSequence().put(packet.getSeqNumber(), packet);
            }

            // 기대했던 순번이라면...
            if (packet.getSeqNumber() == connection.getExpectedReceivedSequenceNumber()) {
                //1. receivedPacketsWaitingForSequence 정렬
                List<Packet> packetList = new ArrayList<>(connection.getReceivedPacketsWaitingForSequence().values().stream().toList());
                packetList.sort(Comparator.comparing(Packet::getSeqNumber));
                //2. for... expectedSeqNumber과 receivedPacketsWaitingForSequence의 요소가 맞으면
                for(Packet p : packetList) {
                    if(p.getSeqNumber() != connection.getExpectedReceivedSequenceNumber()) break;

                    //3.    receivedPacketsWaitingForSequence에서 제거하고 receivedData에 넣기
                    connection.getReceivedPacketsWaitingForSequence().remove(p.getSeqNumber());
                    connection.getReceivedData().offer(serializerForReceive.deserializePacketData(p.getData()));

                    //4. expectedSeqNumber++
                    connection.incrementExpectedReceivedSequenceNumber();
                }
            }
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
    //endregion

    //region 재전송 스레드
    private void retransmitLoop() {
        while (running) {
            try {
                Thread.sleep(RETRANSMISSION_TIMEOUT);
                long currentTime = System.currentTimeMillis();

                for(Connection c : connections.values()){
                    //재전송. disconnect 패킷도 재전송이 되어야하니 disconnecting이어도 동작
                    for(SentPacketInfo info : c.getSentPackets().values()){
                        if(currentTime - info.getTimestamp() > RETRANSMISSION_TIMEOUT){
                            System.err.printf("타임아웃! 재전송: %s\n", info.packet);
                            sendPacket(c, info.getPacket(), serializerForRetransmit);
                        }
                    }

                    // isDisconnecting 이면 이 밑으로는 동작 안하고 스킵
                    if(c.isDisconnecting()) continue;

                    //하트비트
                    long fromLastHeartbeat = currentTime - c.getLastHeartbeat().get();
                    if(fromLastHeartbeat >= HEARTBEAT_TIMEOUT){
                        // 너무 오래 끊겼으므로 disconnect
                        disconnect(c);
                    } else if (fromLastHeartbeat >= HEARTBEAT_INTERVAL) {
                        // 하트비트 전송
                        sendPacket(c, new Packet(Packet.PacketType.HEARTBEAT), serializerForRetransmit);
                    }
                }
                // disconnect 후 몇초 지났는지 확인 후 제거 로직
                for(InetSocketAddress address : connections.keySet()){
                    if(!connections.get(address).isDisconnecting()) continue;

                    long fromDisconnecting = currentTime - connections.get(address).getLastHeartbeat().get();
                    if(fromDisconnecting >= DISCONNECTING_DURATION){
                        connections.remove(address);
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                running = false;
            } catch (Exception e) {
                System.err.printf("재전송 오류: %s\n", e.getMessage());
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