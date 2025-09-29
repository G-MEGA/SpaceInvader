package networking;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

import java.util.ArrayList;

public class Network {
    // 포트 번호
    static public final int SERVER_UDP_PORT = 7654;
    static public final int PEER_UDP_PORT = 4567;


//    // KryoNet에 통신할 클래스들을 등록하는 헬퍼 메서드
//    static public void register(EndPoint endPoint) {
//        Kryo kryo = endPoint.getKryo();
//
//        // 여기 등록하는 클래스들 반드시 매개변수 없는 생성자가 있어야함
//        kryo.register(C2SAuth.class);
//        kryo.register(S2CAuthOK.class);
//
//        // 테스트
//        kryo.register(ArrayList.class);
//        kryo.register(Hello.class);
//    }
//
//    public static class C2SAuth{
//        public String authToken;
//
//        public C2SAuth(){
//        }
//        public  C2SAuth(String authToken) {
//            this.authToken = authToken;
//        }
//    }
//    public static class S2CAuthOK{
//        public boolean ok;
//
//        public S2CAuthOK(){
//        }
//        public S2CAuthOK(boolean ok){
//            this.ok = ok;
//        }
//    }
//
//    public static class Hello {
//        public ArrayList<String> content = new ArrayList();
//    }
}