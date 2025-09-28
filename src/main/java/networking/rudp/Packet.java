package networking.rudp;

import java.io.Serializable;

public class Packet implements Serializable {
    static final long serialVersionUID = 1L;

    public enum PacketType {
        DATA, ACK, HEARTBEAT, HEARTBEAT_ACK, CONNECT, CONNECT_ACK, CONNECT_ACKACK, DISCONNECT, NONE
    }

    private final PacketType type;
    // DATA일 경우 발신 패킷 순서 번호
    // ACK일 경우 발신 되었던 패킷의 순서 번호
    // HEARTBEAT, HEARTBEAT_ACK에는 의미 없음
    private final int seqNumber;
    private final byte[] data;

    //Kryo 직렬화를 위한 매개변수 없는 생성자
    public Packet(){
        this.type = PacketType.NONE;
        this.seqNumber = -1;
        this.data = null;
    }

    // 데이터 패킷 생성자
    public Packet(int seqNumber, byte[] data) {
        this.type = PacketType.DATA;
        this.seqNumber = seqNumber;
        this.data = data;
    }

    // ACK 패킷 생성자
    public Packet(int seqNumber) {
        this.type = PacketType.ACK;
        this.seqNumber = seqNumber;
        this.data = new byte[0];
    }

    // ACK 패킷 생성자
    public Packet(PacketType type) {
        this.type = type;
        this.seqNumber = -1;
        this.data = new byte[0];
    }

    public PacketType getType() {
        return type;
    }

    public int getSeqNumber() {
        return seqNumber;
    }

    public byte[] getData() {
        return data;
    }

    @Override
    public String toString() {
        if (type == PacketType.DATA) {
            return "Packet{type=" + type + ", seq=" + seqNumber + ", dataSize=" + data.length + "}";
        } else {
            return "Packet{type=" + type + ", seq=" + seqNumber + "}";
        }
    }
}