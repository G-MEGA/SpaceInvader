package networking.rudp.PacketData;

public class PacketDataC2SAuth extends PacketData{
    public String authToken;

    // Kryo 역직렬화를 위한 기본 생성자
    public PacketDataC2SAuth(){
    }
    public  PacketDataC2SAuth(String authToken) {
        this.authToken = authToken;
    }
}
