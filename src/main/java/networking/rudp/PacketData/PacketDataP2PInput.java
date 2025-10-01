package networking.rudp.PacketData;

import java.util.ArrayList;

public class PacketDataP2PInput extends PacketData{
    public long inputFrame;
    public ArrayList<String> inputs;
    // Kryo 역직렬화를 위한 기본 생성자
    public PacketDataP2PInput(){
    }
}
