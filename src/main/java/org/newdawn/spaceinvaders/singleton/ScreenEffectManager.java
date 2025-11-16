package org.newdawn.spaceinvaders.singleton;

import org.newdawn.spaceinvaders.fixed_point.FixedPointUtil;
import org.newdawn.spaceinvaders.game_object.effect.BlankScreenEffect;
import org.newdawn.spaceinvaders.loop.GameLoop;
import org.newdawn.spaceinvaders.loop.game_loop.EventBombUsed;

import event_bus.EventBus;
import event_bus.IEventBusSubscriber;

public class ScreenEffectManager implements IEventBusSubscriber{
    public static final long BOMB_EFFECT_DURATION = FixedPointUtil.ZERO_1;

    private GameLoop gameLoop;
    
    public ScreenEffectManager(){
        // Kryo 역직렬화를 위한 매개변수 없는 생성자
    }

    public ScreenEffectManager(GameLoop gameLoop, EventBus eventBus){
        this.gameLoop = gameLoop;

        eventBus.register(EventBombUsed.class, this);
    }

    public void blankScreen(long duration) { 
        // BlankScreenEffect는 내부에서 SpriteRenderer를 생성하기 위해 gameLoop를 인자로 받음
        // 그런 김에 생성자에서 addGameObject(this)를 호출해 스스로 gameLoop에 등록함
        // 따라서, ScreenEffectManager에서는 BlankScreenEffect를 별도로 gameLoop에 등록할 필요가 없음
        new BlankScreenEffect(gameLoop, duration);
    } 

    @Override
    public void notify(Object event) {
        if (event instanceof EventBombUsed){
            blankScreen(BOMB_EFFECT_DURATION);
        }
    }
}
