package networking.rudp.PacketData;

public class PacketDataC2SAuth extends PacketData{
    public String authToken;

    public PacketDataC2SAuth(){
    }
    public  PacketDataC2SAuth(String authToken) {
        this.authToken = authToken;
    }
}
