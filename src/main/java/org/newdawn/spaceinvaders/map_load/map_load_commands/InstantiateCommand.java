package org.newdawn.spaceinvaders.map_load.map_load_commands;

import java.util.ArrayList;

import org.newdawn.spaceinvaders.enums.GameObjectType;

public class InstantiateCommand extends MapLoadCommand {
    private long _instantiateTime;
    private long _instantiateX;
    private long _instantiateY;
    private GameObjectType _gameObjectType;
    private int _gameObjectId;
    private ArrayList<String> _extra;

    public long getInstantiateTime() { return _instantiateTime; }
    public long getInstantiateX() { return _instantiateX; }
    public long getInstantiateY() { return _instantiateY; }
    public GameObjectType getGameObjectType() { return _gameObjectType; }
    public int getGameObjectId() { return _gameObjectId; }
    public ArrayList<String> getExtra() { return _extra; }

    public InstantiateCommand(
        long instantiateTime, long instantiateX, long instantiateY, 
        GameObjectType gameObjectType, int gameObjectId,
        ArrayList<String> extra)
    {
        super();

        _instantiateTime = instantiateTime;
        _instantiateX = instantiateX;
        _instantiateY = instantiateY;
        _gameObjectType = gameObjectType;
        _gameObjectId = gameObjectId;
        _extra = extra;
    }

}
