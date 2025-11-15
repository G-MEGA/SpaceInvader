package org.newdawn.spaceinvaders.network;

import networking.Network;
import networking.rudp.IRUDPPeerListener;
import networking.rudp.RUDPPeer;

public abstract class LoopRUDPPeerListener implements IRUDPPeerListener {
    public boolean onDisconnected(RUDPPeer peer, networking.rudp.Connection connection) {
        if (connection.getAddress().getAddress().getHostAddress().equals(Network.SERVER_IP)) {
            System.out.println(connection.getAddress().getAddress().getHostAddress() + " disconnected");
            System.exit(0);
        }
        return true;
    }
}
