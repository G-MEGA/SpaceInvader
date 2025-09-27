package org.newdawn.spaceinvaders.game_object;

import org.newdawn.spaceinvaders.loop.Loop;

import java.awt.*;

import java.util.ArrayList;
import java.util.Set;

public abstract class GameObject  {
    private Loop loop;
    protected Loop getLoop() {
        return loop;
    }

    boolean inLoop = false;
    public final boolean isInLoop() {
        return inLoop;
    }
    public final void setInLoop(boolean value) {
        this.inLoop = value;

        for (GameObject gameObject: children) {
            gameObject.setInLoop(this.inLoop);
        }

        onInLoopUpdated(this.inLoop);
    }
    protected void onInLoopUpdated(boolean value) {
    }
    boolean destroyed = false;

    GameObject parent;
    ArrayList<GameObject> children = new ArrayList<GameObject>();

    int sortingLayer = 0;
    public void setSortingLayer(int value) {
        this.sortingLayer = value;
    }

    // Kryo 역직렬화를 위한 매개변수 없는 생성자
    public GameObject(){
        super();
    }
    public GameObject(Loop loop){
        this.loop = loop;
    }
    // 소멸자 비스무리한 역할
    protected void onDestroy(){
        // child에서 destroy()를 호출하면 parent.removeChild(this)가 호출되어 children의 길이가 줄어드므로
        // 순회중 배열에 변동이 생기기에 foreach문 쓰면 안됨
//        for(GameObject child : children){
//            child.destroy();
//        }
        for(int i = children.size()-1; i >= 0; i--){
            GameObject child = children.get(i);
            child.destroy();
        }

        if(parent != null) parent.removeChild(this);
        children.clear();
    }

    //region 상속해서 구현할 것들
    protected void process(long deltaTime){}
    protected void postProcess(long deltaTime){}
    protected void draw(Graphics2D g){}
    //endregion

    public final void destroy(){
        if(!destroyed){
            onDestroy();
        }

        destroyed = true;
    }

    //region Loop가 호출하는거
    public final void propagateProcess(long deltaTime){
        if(destroyed) return;

        process(deltaTime);

        for(GameObject child : children){
            child.propagateProcess(deltaTime);
        }
    }
    public final void propagatePostProcess(long deltaTime){
        if(destroyed) return;

        postProcess(deltaTime);

        for(GameObject child : children){
            child.propagatePostProcess(deltaTime);
        }
    }
    public final void propagateDraw(Graphics2D g, int sortingLayer){
        if(this.sortingLayer == sortingLayer){
            draw(g);
        }

        for(GameObject child : children){
            child.propagateDraw(g, sortingLayer);
        }
    }
    public final void propagateGetLayerSet(Set<Integer> layerSet){
        layerSet.add(sortingLayer);

        for(GameObject child : children){
            child.propagateGetLayerSet(layerSet);
        }
    }
    //endregion

    public final GameObject getParent(){
        return parent;
    }
    public final void addChild(GameObject child){
        if(children.contains(child) || child.parent != null) return;
        children.add(child);
        child.parent = this;

        child.setInLoop(inLoop);
        child.onAddedToParent();
    }
    protected void onAddedToParent(){}
    public final void removeChild(GameObject child){
        if(!children.contains(child) || child.parent != this) return;
        child.parent = null;
        children.remove(child);

        child.setInLoop(false);
        child.onRemovedFromParent();
    }
    protected void onRemovedFromParent(){}
    public final ArrayList<GameObject> getChildren(){
        return children;
    }

    public final boolean isDestroyed(){
        return destroyed;
    }
}