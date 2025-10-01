package networking.rudp.PacketData;

import java.net.InetSocketAddress;
import java.util.ArrayList;

public class PacketDataS2CPreprocessForGame extends PacketData{
    public long gameLoopSeed;
    public ArrayList<String> playersUID;
    public ArrayList<String> addresses;
    public ArrayList<String> ports;

    // Kryo 역직렬화를 위한 기본 생성자
    public PacketDataS2CPreprocessForGame(){
    }
}
