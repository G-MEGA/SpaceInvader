package networking.rudp.PacketData;

public class PacketDataC2SEnterLobby extends PacketData{
    int lobbyID;
    // Kryo 역직렬화를 위한 기본 생성자
    public PacketDataC2SEnterLobby(){
    }
    public PacketDataC2SEnterLobby(int lobbyID){
        this.lobbyID = lobbyID;
    }
}
