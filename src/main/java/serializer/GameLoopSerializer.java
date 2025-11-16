package serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.newdawn.spaceinvaders.loop.GameLoop;
import org.reflections.Reflections;

public class GameLoopSerializer {
    static GameLoopSerializer instance;
    public static GameLoopSerializer getInstance(){
        if(instance == null){
            instance = new GameLoopSerializer();
        }
        return instance;
    }

    Kryo kryo;
    KryoRecursiveRegistrar registrar;
    Output output;
    Input input;
    private GameLoopSerializer(){
        kryo = new Kryo();
        registrar= new KryoRecursiveRegistrar(kryo, new Reflections("org.newdawn.spaceinvaders"), 10);
//        output = new Output(4096, 4096);  // 혹시 모르니 최대 버퍼 사이즈 지정
        output = new Output(4096, -1);
        input = new Input(4096);

        registrar.register(GameLoop.class);
    }

    public byte[] serialize(GameLoop gameLoop){
        output.setPosition(0);
        kryo.writeObject(output, gameLoop);
        byte[] bytes = output.toBytes();
        output.close();
        return bytes;
    }
    public GameLoop deserialize(byte[] bytes){
        input.setBuffer(bytes);
        GameLoop gameLoop = kryo.readObject(input, GameLoop.class);
        input.close();
        return gameLoop;
    }
}
