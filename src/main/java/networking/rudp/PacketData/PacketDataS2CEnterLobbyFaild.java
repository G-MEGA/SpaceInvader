package networking.rudp.PacketData;

public class PacketDataS2CEnterLobbyFaild extends PacketData{
    // 꽉 참, 존재하지 않음, 게임 중임
    public String reason;
    public PacketDataS2CEnterLobbyFaild(String reason){
        this.reason = reason;
    }
    // Kryo 역직렬화를 위한 기본 생성자
    public PacketDataS2CEnterLobbyFaild(){
    }
}
