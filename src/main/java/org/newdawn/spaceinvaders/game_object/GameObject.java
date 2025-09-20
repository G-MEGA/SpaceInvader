package org.newdawn.spaceinvaders.game_object;

import org.newdawn.spaceinvaders.loop.Loop;

import java.awt.*;
import java.util.ArrayList;

public abstract class GameObject{
    protected final Loop loop;

    boolean destroyed = false;

    GameObject parent;
    ArrayList<GameObject> children = new ArrayList<GameObject>();

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

    final public void destroy(){
        if(!destroyed){
            onDestroy();
        }

        destroyed = true;
    }

    //region Loop가 호출하는거
    final public void propagateProcess(long deltaTime){
        if(destroyed) return;

        process(deltaTime);

        for(GameObject child : children){
            child.propagateProcess(deltaTime);
        }
    }
    final public void propagatePostProcess(long deltaTime){
        if(destroyed) return;

        postProcess(deltaTime);

        for(GameObject child : children){
            child.propagatePostProcess(deltaTime);
        }
    }
    final public void propagateDraw(Graphics2D g){
        draw(g);

        for(GameObject child : children){
            child.propagateDraw(g);
        }
    }
    //endregion

    public GameObject getParent(){
        return parent;
    }
    public void addChild(GameObject child){
        assert children.contains(child) || child.parent != null;
        if(children.contains(child) || child.parent != null) return;
        children.add(child);
        child.parent = this;
    }
    public void removeChild(GameObject child){
        assert !children.contains(child) || child.parent != this;
        if(!children.contains(child) || child.parent != this) return;
        child.parent = null;
        children.remove(child);
    }

    public boolean isDestroyed(){
        return destroyed;
    }
}