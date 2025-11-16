package org.newdawn.spaceinvaders.game_object.visual;

import org.newdawn.spaceinvaders.game_object.GameObject2D;
import org.newdawn.spaceinvaders.loop.Loop;
import org.newdawn.spaceinvaders.sprite.Sprite;
import org.newdawn.spaceinvaders.sprite.SpriteStore;

import java.awt.*;

public class SpriteRenderer extends GameObject2D {
    public String spriteRef;
    private transient Sprite sprite;

    // Kryo 역직렬화를 위한 매개변수 없는 생성자
    public SpriteRenderer(){
        super();
    }
    public SpriteRenderer(Loop loop) {
        super(loop);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        sprite = null;
    }

    public void setSpriteRef(String spriteRef) {
        this.spriteRef = spriteRef;
        this.sprite = SpriteStore.get().getSprite(this.spriteRef);
    }

    public long getSpritePivotX() {
        if (sprite == null) return 0L;
        return sprite.getPivotX();
    }
    public long getSpritePivotY() {
        if (sprite == null) return 0L;
        return sprite.getPivotY();
    }
    public int getSpriteWidth() {
        if (sprite == null) return 0;
        return sprite.getWidth();
    }
    public int getSpriteHeight() {
        if (sprite == null) return 0;
        return sprite.getHeight();
    }

    @Override
    protected void draw(Graphics2D g) {
        if(spriteRef==null) return;
        if(sprite==null){
            sprite = SpriteStore.get().getSprite(spriteRef);
        }

        sprite.draw(g, getGlobalTransform().getAffineTransform());
    }
}
