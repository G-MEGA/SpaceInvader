package org.newdawn.spaceinvaders.loop;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import networking.rudp.IRUDPPeerListener;
import org.newdawn.spaceinvaders.Game;
import org.newdawn.spaceinvaders.loop_input.LoopInput;
import org.newdawn.spaceinvaders.loop_input.LoopInputKey;
import org.newdawn.spaceinvaders.loop_input.LoopInputMouseMove;
import org.newdawn.spaceinvaders.game_object.GameObject;
import org.newdawn.spaceinvaders.game_object.collision.Collider2D;
import org.newdawn.spaceinvaders.game_object.collision.ICollider2DOwner;

public abstract class Loop  {
    private transient Game game;

    private transient IRUDPPeerListener irudpPeerListener;
    public IRUDPPeerListener getIrudpPeerListener() {
        return irudpPeerListener;
    }
    protected abstract IRUDPPeerListener generateIRUDPPeerListener();

    private int playerCount = -1;
    public int getPlayerCount() {
        return playerCount;
    }

    private final ArrayList<GameObject> gameObjects = new ArrayList<>();
    private final ArrayList<GameObject> gameObjectsInProcessing = new ArrayList<>();
    private final ArrayList<Collider2D> colliders = new ArrayList<>();
    public int getGameObjcetsCount() { return gameObjects.size(); }
    public int getGameObjectsInProcessingCount() { return gameObjectsInProcessing.size(); }
    public int getCollidersCount() { return colliders.size(); }

    private final ArrayList<HashMap<String, Boolean>> isKeyInputPressed = new ArrayList<>();
    private final ArrayList<HashMap<String, Boolean>> isKeyInputJustPressed = new ArrayList<>();
    private final ArrayList<HashMap<String, Boolean>> isKeyInputJustReleased = new ArrayList<>();

    private ArrayList<Integer> mousePosX = new ArrayList<>();
    private ArrayList<Integer> mousePosY = new ArrayList<>();

    // Kryo 역직렬화를 위한 매개변수 없는 생성자
    public Loop(){
        super();
    }
    public Loop(Game game) {
        this(game, 1);
    }
    public Loop(Game game, int playerCount) {
        this.game = game;
        this.playerCount = playerCount;

        irudpPeerListener = generateIRUDPPeerListener();

        for (int i = 0; i < playerCount; i++) {
            isKeyInputPressed.add(new HashMap<>());
            isKeyInputJustPressed.add(new HashMap<>());
            isKeyInputJustReleased.add(new HashMap<>());
            mousePosX.add(400);
            mousePosY.add(300);
        }
    }

    Set<Integer> layerSet = new HashSet<Integer>();
    public void draw(Graphics2D g){
        layerSet.clear();

        for (int i=0;i<gameObjects.size();i++) {
            GameObject gameObject = gameObjects.get(i);

            gameObject.propagateGetLayerSet(layerSet);
        }
        
        for (Integer layer : layerSet) {
            // cycle round drawing all the entities we have in the game
            for (int i=0;i<gameObjects.size();i++) {
                GameObject gameObject = gameObjects.get(i);

                gameObject.propagateDraw(g, layer);
            }
        }
    }
    public void process(ArrayList<LoopInput> inputs){
        for (int playerID=0; playerID<playerCount; playerID++) {
            for(String keyInputName:isKeyInputJustPressed.get(playerID).keySet()){
                isKeyInputJustPressed.get(playerID).put(keyInputName, false);
            }
            for(String keyInputName:isKeyInputJustReleased.get(playerID).keySet()){
                isKeyInputJustReleased.get(playerID).put(keyInputName, false);
            }
        }


        if(inputs != null && !inputs.isEmpty()) {
            for (LoopInput input : inputs) {
                input(input);
            }
        }
    }
    public void process(){
        process(null);
    }

    private void input(LoopInput input){
        if(input instanceof LoopInputKey){
            inputKey((LoopInputKey)input);
        }
        else if(input instanceof LoopInputMouseMove){
            inputMouseMove((LoopInputMouseMove)input);
        }
    }

    private void inputKey(LoopInputKey input){
        if(input.pressed){
            if(isKeyInputPressed.get(input.playerID).get(input.name) == null || !isKeyInputPressed.get(input.playerID).get(input.name)){
                isKeyInputJustPressed.get(input.playerID).put(input.name, true);
            }
        }
        else{
            if(isKeyInputPressed.get(input.playerID).get(input.name) != null && isKeyInputPressed.get(input.playerID).get(input.name)){
                isKeyInputJustReleased.get(input.playerID).put(input.name, true);
            }
        }

        isKeyInputPressed.get(input.playerID).put(input.name, input.pressed);
    }

    private void inputMouseMove(LoopInputMouseMove input) {
        mousePosX.set(input.playerID, input.posX);
        mousePosY.set(input.playerID, input.posY);
    }

    public boolean isKeyInputPressed(int playerID, String inputName){
        if(isKeyInputPressed.get(playerID).containsKey(inputName)){
            return isKeyInputPressed.get(playerID).get(inputName);
        }
        return false;

    }
    public boolean isKeyInputJustPressed(int playerID, String inputName){
        if(isKeyInputJustPressed.get(playerID).containsKey(inputName)){
            return isKeyInputJustPressed.get(playerID).get(inputName);
        }
        return false;
    }
    public boolean isKeyInputJustReleased(int playerID, String inputName){
        if(isKeyInputJustReleased.get(playerID).containsKey(inputName)){
            return isKeyInputJustReleased.get(playerID).get(inputName);
        }
        return false;
    }
    public int getMousePosX(int playerID) {
        return mousePosX.get(playerID);
    }
    public int getMousePosY(int playerID) {
        return mousePosY.get(playerID);
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
