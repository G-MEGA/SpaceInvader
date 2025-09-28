package networking.rudp;

public class SentPacketInfo {
    Packet packet;
    public Packet getPacket() {
        return packet;
    }

    long timestamp;
    public long getTimestamp() {
        return timestamp;
    }

    public SentPacketInfo(Packet packet) {
        this.packet = packet;
        this.timestamp = System.currentTimeMillis();
    }
}
