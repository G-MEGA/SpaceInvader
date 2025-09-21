package org.newdawn.spaceinvaders.singleton;

import java.util.HashMap;

import org.newdawn.spaceinvaders.game_object.ingame.loot_item.BatteryItem;
import org.newdawn.spaceinvaders.game_object.ingame.loot_item.CoinItem;
import org.newdawn.spaceinvaders.game_object.ingame.loot_item.FrozenItem;
import org.newdawn.spaceinvaders.game_object.ingame.loot_item.LootItem;
import org.newdawn.spaceinvaders.game_object.ingame.loot_item.ShieldItem;
import org.newdawn.spaceinvaders.loop.Loop;

public class LootItemFactory {
    private static LootItemFactory instance = new LootItemFactory();
    public static LootItemFactory getInstance()
    {
        return instance;
    }

    //TODO Object Pulling을 만들기 -> Loop의 GameObject에 넣어놓기
    private final HashMap<String, Long> _itemWeights = new HashMap<>(); //* <아이템 이름, 가중치
    public LootItemFactory() {
        _itemWeights.put("none", 50L);
        _itemWeights.put("shield", 10L);
        _itemWeights.put("battery", 10L);
        _itemWeights.put("coin", 20L);
        _itemWeights.put("frozen", 10L);
    }

    /**
     * 랜덤 가중치 알고리즘 기반으로 LootItem을 소환함
     * @param loop
     */
    public LootItem instantiateRandomItem(Loop loop){
        long totalWeight = 0L;
        LootItem lootItem = null;

        for (String key : _itemWeights.keySet()){
            totalWeight += _itemWeights.get(key);
        }
        
        //TODO 시드 기반으로 만들어 놓기
        long random = (long)Math.ceil(Math.random() * totalWeight);

        for (String key : _itemWeights.keySet()){
            random -= _itemWeights.get(key);
            
            //TODO 생성된 아이템을 Loop의 GameObject 배열에 넣기
            if (random <= 0){
                switch (key) {
                    case "shield":
                        lootItem = new ShieldItem(loop);
                        break;
                    case "battery":
                        lootItem = new BatteryItem(loop);
                        break;
                    case "coin":
                        lootItem = new CoinItem(loop);
                        break;
                    case "frozen":
                        lootItem = new FrozenItem(loop);
                        break;
                    default: // 키 값이 none인 경우이다.
                        System.out.println("LootItem 소환 안됨");
                        break;
                }

                return lootItem;
            }
        }

        throw new RuntimeException("가중치 랜덤 알고리즘에 문제 존재");
    }
}
