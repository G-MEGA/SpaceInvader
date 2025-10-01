package networking.rudp;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import networking.rudp.PacketData.PacketData;
import org.reflections.Reflections;
import serializer.KryoRecursiveRegistrar;

import java.net.InetSocketAddress;

public class PacketSerializer {
    Kryo kryo;
    KryoRecursiveRegistrar registrar;
    Output output;
    Input input;
    public PacketSerializer(){
        kryo = new Kryo();
        registrar= new KryoRecursiveRegistrar(kryo, new Reflections("networking.rudp"), 10);
        output = new Output(RUDPPeer.BUFFER_SIZE, RUDPPeer.BUFFER_SIZE);
        input = new Input(RUDPPeer.BUFFER_SIZE);

        registrar.register(Packet.class);
        registrar.register(PacketData.class);
    }

    public byte[] serialize(Packet packet){
        return serializeObject(packet);
    }
    public byte[] serialize(PacketData packetData){
        return serializeObject(packetData);
    }
    private byte[] serializeObject(Object object){
        output.setPosition(0);
        kryo.writeClassAndObject(output, object);
        byte[] bytes = output.toBytes();// TODO 이거 GC에 부담 줄 수 있으니 나중에 수정하자 System.arraycopy()랑 output.getBuffer()로
        output.close();
        return bytes;
    }
    public Packet deserializePacket(byte[] bytes){
        input.setBuffer(bytes);

        Packet packet = (Packet)kryo.readClassAndObject(input);
        input.close();
        return packet;
    }
    public PacketData deserializePacketData(byte[] bytes){
        input.setBuffer(bytes);
        PacketData packetData = (PacketData)kryo.readClassAndObject(input);
        input.close();
        return packetData;
    }
}
