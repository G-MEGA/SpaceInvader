package networking.rudp;

import networking.rudp.PacketData.PacketData;

public interface IRUDPPeerListener {
    void onConnected(RUDPPeer peer, Connection connection);
    void onDisconnected(RUDPPeer peer, Connection connection);
    void onReceived(RUDPPeer peer, Connection connection, PacketData data);
}
