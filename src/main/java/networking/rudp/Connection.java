package networking.rudp;

import networking.rudp.PacketData.PacketData;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class Connection {
    private final InetSocketAddress address;
    public InetSocketAddress getAddress() {return address;}

    // 이거 true면 일정 시간동안 해당 주소에서 들어오는 입력은 처리 안하겠다는 거임.
    //      게임 종료 등의 의도적인 disconnect의 경우 그렇게 함
    private boolean disconnecting = false;
    public boolean isDisconnecting(){return disconnecting;}
    public void disconnect(){
        disconnecting = true;
        updateLastHeartbeat();
    }

    // 아무거나 수신받을 때마다 lastHeartBeat 갱신
    // 재전송 스레드에서 지속적으로 체크하여
    // - 마지막으로 갱신된 지 3초 지났으면 하트비트 발신. UDP 홀펀칭 유지 및 연결 확인
    // - 마지막으로 갱신된 지 10초 지났으면 disconnected 판정
    // 재전송 스레드랑 수신 스레드 두 곳에서 접근할 것이니 Concurrent임
    private final AtomicLong lastHeartbeat = new AtomicLong(System.currentTimeMillis());
    public AtomicLong getLastHeartbeat() {
        return lastHeartbeat;
    }
    public void updateLastHeartbeat() {
        lastHeartbeat.set(System.currentTimeMillis());
    }

    // 패킷 보낼 때 붙이는 패킷 순서 번호
    private final AtomicInteger nextSequenceNumber = new AtomicInteger(0);
    public AtomicInteger getNextSequenceNumber() {
        return nextSequenceNumber;
    }

    // 보낸 것 중 ACK 안 온 것들
    // 패킷 발신 스레드와 패킷 재전송 스레드 두 곳에서 접근해야 하므로 Concurrent 사용
    private final Map<Integer, SentPacketInfo> sentPackets = new ConcurrentHashMap<>();
    public Map<Integer, SentPacketInfo> getSentPackets() {
        return sentPackets;
    }

    // 수신 스레드에서만 쓸꺼임
    private int expectedReceivedSequenceNumber = 0;
    public int getExpectedReceivedSequenceNumber() {
        return expectedReceivedSequenceNumber;
    }
    public void incrementExpectedReceivedSequenceNumber() {
        expectedReceivedSequenceNumber++;
    }
    // 받은 것 중 순서가 안맞는 패킷들, 즉 expected가 아닌 것들 순서 맞는게 올 때까지 대기
    // 수신 스레드에서만 쓸 거니까 Concurrent 필요 없음
    //TODO - receivedPacketsWaitingForSequence 중에 10초 동안 못받은거 있으면... disconnected로 들어가는게 좋지 않을까
    private final Map<Integer, Packet> receivedPacketsWaitingForSequence = new HashMap<>();
    public Map<Integer, Packet> getReceivedPacketsWaitingForSequence() {
        return receivedPacketsWaitingForSequence;
    }

    // 받은 것 중에서 순서 맞는 패킷을 처리한 데이터들, 메인이든 어디든 processReceivedData 처리할 때까지 대기
    // 수신 스레드와 메인 스레드 두 곳에서 접근해야 하므로 Concurrent 사용
    private final Queue<PacketData> receivedData = new ConcurrentLinkedQueue<PacketData>();
    public Queue<PacketData> getReceivedData() {
        return receivedData;
    }

    public String tag = "";

    public Connection(InetSocketAddress address) {
        this.address = address;
    }
}
