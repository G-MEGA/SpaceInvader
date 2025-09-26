package org.newdawn.spaceinvaders.loop;

import org.newdawn.spaceinvaders.Game;
import org.newdawn.spaceinvaders.game_loop_input.GameLoopInput;
import org.newdawn.spaceinvaders.game_loop_input.GameLoopInputKey;
import org.newdawn.spaceinvaders.game_loop_input.GameLoopInputMouseMove;
import org.newdawn.spaceinvaders.game_object.GameObject;
import org.newdawn.spaceinvaders.game_object.collision.Collider2D;
import org.newdawn.spaceinvaders.game_object.collision.ICollider2DOwner;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class Loop implements Serializable {
    private transient Game game;

    private final ArrayList<GameObject> gameObjects = new ArrayList<>();
    private final ArrayList<GameObject> gameObjectsInProcessing = new ArrayList<>();
    private final ArrayList<Collider2D> colliders = new ArrayList<>();

    private final HashMap<String, Boolean> isKeyInputPressed = new HashMap<String, Boolean>();
    private final HashMap<String, Boolean> isKeyInputJustPressed = new HashMap<String, Boolean>();
    private final HashMap<String, Boolean> isKeyInputJustReleased = new HashMap<String, Boolean>();

    private int mousePosX = 400;
    private int mousePosY = 300;

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
        else if(input instanceof GameLoopInputMouseMove){
            inputMouseMove((GameLoopInputMouseMove)input);
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

    private void inputMouseMove(GameLoopInputMouseMove input) {
        mousePosX = input.posX;
        mousePosY = input.posY;
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
    public int getMousePosX() {
        return mousePosX;
    }
    public int getMousePosY() {
        return mousePosY;
    }

    public Game getGame() {
        return game;
    }
    public void setGame(Game game) {
        this.game = game;
    }

    //* LootItem 제작하면서, LootItem가 생성 될 때, 해당 LootItem를 Loop로 삽입하기 위해 추가한 메소드
    public final void addGameObject(GameObject gameObject){
        if(gameObject.getParent() != null){
            throw new IllegalArgumentException("부모가 있는 게임오브젝트는 Loop에 직접 연결할 수 없음");
        }
        if (gameObjects.contains(gameObject))return;

        gameObjects.add(gameObject);
        gameObject.setInLoop(true);
    }
    public final void removeGameObject(GameObject gameObject){
        if(!gameObjects.contains(gameObject))return;

        gameObjects.remove(gameObject);
        gameObject.setInLoop(false);
    }
    public final void clearGameObjects() {
        for (GameObject gameObject : gameObjects) {
            gameObject.setInLoop(false);
        }
        gameObjects.clear();
    }
    public final ArrayList<GameObject> getGameObjects(){
        return gameObjects;
    }

    public final void addCollider(Collider2D collider){
        if(colliders.contains(collider))return;

        colliders.add(collider);
    }
    public final void removeCollider(Collider2D collider){
        colliders.remove(collider);
    }

    public final void processCollision2D(){
//        int count = 0;
//        long nanos = System.nanoTime();
        for (int p=0;p<colliders.size();p++) {
            for (int s=p+1;s<colliders.size();s++) {
//                count++;
                Collider2D me = colliders.get(p);
                Collider2D him = colliders.get(s);

                if(me.isDestroyed()) continue;
                if(him.isDestroyed()) continue;

                if (me.collidesWith(him)) {
                    ICollider2DOwner meOwner = me.getOwner();
                    ICollider2DOwner himOwner = him.getOwner();
                    meOwner.collidedWith(himOwner);
                    himOwner.collidedWith(meOwner);
                }
            }
        }
//        System.out.println((System.nanoTime() - nanos)/1000_000.0 + "밀리초");
    }

    protected final void processGameObjects(){
        gameObjectsInProcessing.clear();
        gameObjectsInProcessing.addAll(gameObjects);

        //propagate 프로세스
        for(GameObject gameObject : gameObjectsInProcessing){
            if(!gameObject.isDestroyed()) gameObject.propagateProcess(game.fixedDeltaTime);
        }

        // 충돌 처리
        processCollision2D();

        //propagate 포스트 프로세스
        for(GameObject gameObject : gameObjectsInProcessing){
            if(!gameObject.isDestroyed()) gameObject.propagatePostProcess(game.fixedDeltaTime);
        }

        //destroy 된 것 있으면 GameObject 목록에서 제거
        for(int i=gameObjects.size() - 1 ; i > -1;i--){
            if(gameObjects.get(i).isDestroyed()){
                gameObjects.remove(i);
            }
        }
    }
}
