package networking.rudp;

import networking.rudp.PacketData.PacketData;

public interface IRUDPPeerListener {
    // 현재 처리되면 안되는 Data라면 false를 반환하여 처리 거부 전달
    // 그러면 receivedData에 다시 삽입하여 다음 processReceivedData()에서 다시 처리 시도됨
    boolean onConnected(RUDPPeer peer, Connection connection);
    boolean onDisconnected(RUDPPeer peer, Connection connection);
    boolean onReceived(RUDPPeer peer, Connection connection, PacketData data);
}
