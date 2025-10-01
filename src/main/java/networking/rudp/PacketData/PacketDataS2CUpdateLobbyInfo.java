package networking.rudp.PacketData;

import java.util.ArrayList;

public class PacketDataS2CUpdateLobbyInfo extends PacketData{
    public int lobbyID;  // 이거 음수면 퇴장임
    public String lobbyName;
    public int maxPlayers;
    public ArrayList<String> playersUID;
    public ArrayList<Boolean> playerReadied;
    public int mapID;
    // Kryo 역직렬화를 위한 기본 생성자
    public PacketDataS2CUpdateLobbyInfo(){
    }
}
