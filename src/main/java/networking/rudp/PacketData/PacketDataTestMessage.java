package networking.rudp.PacketData;

public class PacketDataTestMessage extends PacketData{
    public String message;

    //Kryo 직렬화를 위한 매개변수 없는 생성자
    public PacketDataTestMessage() {}

    public PacketDataTestMessage(String message) {
        this.message = message;
    }
}
