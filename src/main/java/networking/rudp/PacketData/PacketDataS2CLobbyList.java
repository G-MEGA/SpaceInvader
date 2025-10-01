package networking.rudp.PacketData;

import java.util.ArrayList;

public class PacketDataS2CLobbyList extends PacketData{
    public ArrayList<Integer> lobbyIDs = new ArrayList<>();
    public ArrayList<String> lobbyNames = new ArrayList<>();
    public ArrayList<Integer> maxPlayers = new ArrayList<>();
    public ArrayList<Integer> mapIDs = new ArrayList<>();
    public ArrayList<Boolean> isPlaying = new ArrayList<>();
    // Kryo 역직렬화를 위한 기본 생성자
    public PacketDataS2CLobbyList(){
    }
}
