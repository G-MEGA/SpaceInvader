package networking.rudp.PacketData;

public class PacketDataC2SCreateLobby extends PacketData{
    public String lobbyName;
    public int maxPlayers;
    // Kryo 역직렬화를 위한 기본 생성자
    public PacketDataC2SCreateLobby(){
    }
    public PacketDataC2SCreateLobby(String lobbyName, int maxPlayers){
        this.lobbyName = lobbyName;
        this.maxPlayers = maxPlayers;
    }
}
