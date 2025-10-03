package networking.rudp.PacketData;

public class PacketDataC2SGameResult extends PacketData{
    public int score;
    public boolean win;
    public PacketDataC2SGameResult(int score, boolean win){
        this.score = score;
        this.win = win;
    }
    // Kryo 역직렬화를 위한 기본 생성자
    public PacketDataC2SGameResult(){
    }
}
