package org.newdawn.spaceinvaders.singleton;

import java.awt.event.KeyEvent;
import java.util.HashMap;

public class PlayerSetting {
    private static PlayerSetting instance = new PlayerSetting();
    public static PlayerSetting getInstance()
    {
        return instance;
    }

    HashMap<String, Integer> keySetting;
    HashMap<Integer, String> keyToInputName = new HashMap<Integer, String>();


    public PlayerSetting(){
        HashMap<String, Integer> newKeySetting = new HashMap<>();

        newKeySetting.put("left", KeyEvent.VK_LEFT);
        newKeySetting.put("right", KeyEvent.VK_RIGHT);
        newKeySetting.put("fire", KeyEvent.VK_SPACE);
        newKeySetting.put("escape", KeyEvent.VK_ESCAPE);
        newKeySetting.put("accept", KeyEvent.VK_ENTER);
        newKeySetting.put("record", KeyEvent.VK_R);

        setKeySetting(newKeySetting);
    }

    private void setKeySetting(HashMap<String, Integer>  keySetting){
        this.keySetting = keySetting;

        keyToInputName.clear();
        for(String key : keySetting.keySet()){
            keyToInputName.put(keySetting.get(key), key);
        }
    }

    public String KeyToInputName(int key){
        return keyToInputName.get(key);
    }
}
