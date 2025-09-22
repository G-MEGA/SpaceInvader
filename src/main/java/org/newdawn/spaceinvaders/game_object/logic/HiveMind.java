package org.newdawn.spaceinvaders.game_object.logic;

import java.io.Serializable;
import java.util.ArrayList;

public class HiveMind implements Serializable {
    boolean broadcastRequested = false;
    ArrayList<IHiveMindListener> listeners = new  ArrayList<>();

    public void requestBroadcast(){
        broadcastRequested =  true;
    }
    public void cancelBroadcast(){
        broadcastRequested = false;
    }

    public boolean isBroadcastRequested(){
        return broadcastRequested;
    }

    public void broadcastIfRequested(){
        if(isBroadcastRequested()){
            broadcastRequested = false;

            broadcast();
        }
    }
    public void broadcast(){
        // destroy된 것들 정리하고
        for(int i=listeners.size()-1;i>=0;i--){
            IHiveMindListener listener = listeners.get(i);
            if(listener.isDestroyed()){
                listeners.remove(i);
            }
        }
        // 브로드캐스팅
        for(IHiveMindListener listener : listeners){
            listener.onBroadcast();
        }
    }

    public void addListener(IHiveMindListener listener){
        if(!listeners.contains(listener)){
            listeners.add(listener);
        }
    }
    public void removeListener(IHiveMindListener listener){
        listeners.remove(listener);
    }
}
