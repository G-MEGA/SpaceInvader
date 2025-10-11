package networking.rudp.PacketData;

public class PacketDataS2CAuthOK extends PacketData{
    public boolean ok;

    public PacketDataS2CAuthOK(){
    }
    public PacketDataS2CAuthOK(boolean ok){
        this.ok = ok;
    }
}
