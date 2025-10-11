package org.newdawn.spaceinvaders.loop.game_loop;

import org.newdawn.spaceinvaders.enums.GameLoopResultType;

public interface IGameLoopGameResultListener {
    void onGameResultChanged(GameLoopResultType gameResult);
}
