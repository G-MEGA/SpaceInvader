package networking.rudp.PacketData;

import java.util.ArrayList;

public class PacketDataS2CLobbyList extends PacketData{
    public ArrayList<Integer> lobbyIDs;
    public ArrayList<String> lobbyNames;
    public ArrayList<Integer> maxPlayers;
    public ArrayList<Boolean> isPlaying;
    // Kryo 역직렬화를 위한 기본 생성자
    public PacketDataS2CLobbyList(){
    }
}
