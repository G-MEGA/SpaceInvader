package org.newdawn.spaceinvaders.loop;

import org.newdawn.spaceinvaders.Game;
import org.newdawn.spaceinvaders.game_loop_input.GameLoopInput;
import org.newdawn.spaceinvaders.game_loop_input.GameLoopInputKey;
import org.newdawn.spaceinvaders.game_loop_input.GameLoopInputLog;
import org.newdawn.spaceinvaders.game_object.GameObject;
import org.newdawn.spaceinvaders.game_object.collision.Collider2D;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class Loop {
    protected final Game game;

    protected final ArrayList<GameObject> gameObjects = new ArrayList<>();
    private final ArrayList<Collider2D> colliders = new ArrayList<>();

    private final HashMap<String, Boolean> isKeyInputPressed = new HashMap<String, Boolean>();
    private final HashMap<String, Boolean> isKeyInputJustPressed = new HashMap<String, Boolean>();
    private final HashMap<String, Boolean> isKeyInputJustReleased = new HashMap<String, Boolean>();

    public Loop(Game game) {
        this.game = game;
    }

    public void draw(Graphics2D g){
        // cycle round drawing all the entities we have in the game
        for (int i=0;i<gameObjects.size();i++) {
            GameObject gameObject = gameObjects.get(i);

            gameObject.propagateDraw(g);
        }
    }
    public void process(ArrayList<GameLoopInput> inputs){
        for(String keyInputName:isKeyInputJustPressed.keySet()){
            isKeyInputJustPressed.put(keyInputName, false);
        }
        for(String keyInputName:isKeyInputJustReleased.keySet()){
            isKeyInputJustReleased.put(keyInputName, false);
        }


        if(inputs != null && !inputs.isEmpty()) {
            for (GameLoopInput input : inputs) {
                input(input);
            }
        }
    }
    public void process(){
        process(null);
    }

    private void input(GameLoopInput input){
        if(input instanceof GameLoopInputKey){
            inputKey((GameLoopInputKey)input);
        }
    }
    private void inputKey(GameLoopInputKey input){
        if(input.pressed){
            if(isKeyInputPressed.get(input.name) == null || !isKeyInputPressed.get(input.name)){
                isKeyInputJustPressed.put(input.name, true);
            }
        }
        else{
            if(isKeyInputPressed.get(input.name) != null && isKeyInputPressed.get(input.name)){
                isKeyInputJustReleased.put(input.name, true);
            }
        }

        isKeyInputPressed.put(input.name, input.pressed);
    }

    public boolean isKeyInputPressed(String inputName){
        if(isKeyInputPressed.containsKey(inputName)){
            return isKeyInputPressed.get(inputName);
        }
        return false;

    }
    public boolean isKeyInputJustPressed(String inputName){
        if(isKeyInputJustPressed.containsKey(inputName)){
            return isKeyInputJustPressed.get(inputName);
        }
        return false;
    }
    public boolean isKeyInputJustReleased(String inputName){
        if(isKeyInputJustReleased.containsKey(inputName)){
            return isKeyInputJustReleased.get(inputName);
        }
        return false;
    }

    public void addCollider(Collider2D collider){
        if(colliders.contains(collider))return;

        colliders.add(collider);
    }
    public void removeCollider(Collider2D collider){
        colliders.remove(collider);
    }

    public void processCollision2D(){
        for (int p=0;p<colliders.size();p++) {
            for (int s=p+1;s<colliders.size();s++) {
                Collider2D me = colliders.get(p);
                Collider2D him = colliders.get(s);

                if(me.isDestroyed()) continue;
                if(him.isDestroyed()) continue;

                if (me.collidesWith(him)) {
                    me.collidedWith(him.getOwner());
                    him.collidedWith(me.getOwner());
                }
            }
        }
    }
}
