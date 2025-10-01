package networking.rudp.PacketData;

import java.util.ArrayList;

public class PacketDataS2CPreprocessForGame extends PacketData{
    public long gameLoopSeed;
    public int mapID;
    public ArrayList<String> playersUID = new ArrayList<>();
    public ArrayList<String> addresses = new ArrayList<>();
    public ArrayList<Integer> ports = new ArrayList<>();

    // Kryo 역직렬화를 위한 기본 생성자
    public PacketDataS2CPreprocessForGame(){
    }
}
