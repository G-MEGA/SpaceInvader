package org.newdawn.spaceinvaders.loop.game_loop.game_loop_components;

import java.util.ArrayList;

import org.newdawn.spaceinvaders.game_object.ingame.player.PlayerShip;
import org.newdawn.spaceinvaders.loop.GameLoop;

public class GameLoopPlayerShipSystem {
    private GameLoop gameLoop;

    // Kryo 역직렬화를 위한 매개변수 없는 생성자
    public GameLoopPlayerShipSystem() {
    }

    public GameLoopPlayerShipSystem(GameLoop gameLoop, int myPlayerID) {
        this.gameLoop = gameLoop;
        this.myPlayerID = myPlayerID;
    }

    private int myPlayerID = -1;
    public int getMyPlayerID() { return myPlayerID; }

    private ArrayList<PlayerShip> ships = new ArrayList<>();
    public ArrayList<PlayerShip> getPlayerShips() { return ships; }
    public PlayerShip getPlayerShip(int id) { return ships.get(id); }
    public PlayerShip getMyPlayerShip() { return ships.get(myPlayerID); }

    private ArrayList<PlayerShip> aliveShips = new ArrayList<>();
    public ArrayList<PlayerShip> getAliveShips() { return aliveShips;}
    public PlayerShip getAliveShip(int index) { return aliveShips.get(index); }
    public int getAliveShipCount() { return aliveShips.size(); }
    public PlayerShip getRandomAlivePlayerShip(){
        if(aliveShips.isEmpty()){
            return null;
        } else if (aliveShips.size() == 1) {
            return aliveShips.get(0);
        } else{
            return aliveShips.get(gameLoop.getRandom().nextInt(aliveShips.size()));
        }
    }

    public boolean HasAllDied() { return aliveShips.size()==0; }

    /**
     * Initialise the starting state of the entities (ship and aliens). Each
     * entitiy will be added to the overall list of entities in the game.
     */
    public void initPlayerShips() {
        // create the player ship and place it roughly in the center of the screen
        ships.clear();

        //region 플레이어십 생성
        long shipSpawnDistance = 100L << 16;//플레이어십 간의 거리
        long shipSpawnAreaWidth = (gameLoop.getPlayerCount()-1) * shipSpawnDistance;
        long shipSpawnAreaPosX = (400L << 16) - shipSpawnAreaWidth/2;

        for (int playerID = 0; gameLoop.getPlayerCount() > playerID; playerID++) {
            PlayerShip ship = new PlayerShip(gameLoop, playerID);
            ships.add(ship);
            aliveShips.add(ship);
            gameLoop.addGameObject(ship);

            ship.setPos(shipSpawnAreaPosX + shipSpawnDistance * playerID, 550 << 16);
        }
        //endregion
    }

    public void notifyPlayerShipDeath(){
        aliveShips.clear();

        for(PlayerShip ship : ships){
            if(!ship.isDead()){aliveShips.add(ship);}
        }
    }
}
