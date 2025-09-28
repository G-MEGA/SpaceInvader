package networking.rudp.PacketData;

// Packet의 data에 담을 데이터는 전부 이걸 상속받아야 함.
// 얘 상속받는 애들을 전부 kryo로 직렬화 할 것임
public class PacketData {
    //Kryo 직렬화를 위한 매개변수 없는 생성자
    public PacketData() {}
}
