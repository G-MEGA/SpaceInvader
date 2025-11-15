package networking.rudp.PacketData;

import java.util.ArrayList;

public class PacketDataS2CPreprocessForGame extends PacketData{
    public String gameSessionID;
    public long gameLoopSeed;
    public int mapID;
    public int playerIDInLobby;
    public ArrayList<String> playersUID = new ArrayList<>();
    public ArrayList<String> addresses = new ArrayList<>();
    public ArrayList<Integer> ports = new ArrayList<>();

    public PacketDataS2CPreprocessForGame(){
        // Kryo 역직렬화를 위한 기본 생성자
    }
}
