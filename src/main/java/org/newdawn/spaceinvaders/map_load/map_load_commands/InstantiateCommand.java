package org.newdawn.spaceinvaders.map_load.map_load_commands;

import org.newdawn.spaceinvaders.enums.GameObjectType;

public class InstantiateCommand extends MapLoadCommand {
    private long _instantiateTime;
    private int _instantiateX;
    private int _instantiateY;
    private GameObjectType _gameObjectType;
    private String _gameObjectName;

    public long getInstantiateTime() { return _instantiateTime; }
    public int getInstantiateX() { return _instantiateX; }
    public int getInstantiateY() { return _instantiateY; }
    public GameObjectType getGameObjectType() { return _gameObjectType; }
    public String getGameObjectName() { return _gameObjectName; }

    public InstantiateCommand(
        long instantiateTime, int instantiateX, int instantiateY, 
        GameObjectType gameObjectType, String gmaeObjectName)
    {
        super();

        _instantiateTime = instantiateTime;
        _instantiateX = instantiateX;
        _instantiateY = instantiateY;
        _gameObjectType = gameObjectType;
        _gameObjectName = gmaeObjectName;
    }
}
