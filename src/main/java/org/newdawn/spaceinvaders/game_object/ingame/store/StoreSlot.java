package org.newdawn.spaceinvaders.game_object.ingame.store;

import java.awt.Color;
import java.awt.Graphics2D;

import org.newdawn.spaceinvaders.fixed_point.FixedPointUtil;
import org.newdawn.spaceinvaders.game_object.GameObject2D;
import org.newdawn.spaceinvaders.game_object.collision.Collider2D;
import org.newdawn.spaceinvaders.game_object.collision.ICollider2DOwner;
import org.newdawn.spaceinvaders.game_object.ingame.PlayerShip;
import org.newdawn.spaceinvaders.game_object.visual.SpriteRenderer;
import org.newdawn.spaceinvaders.loop.GameLoop;
import org.newdawn.spaceinvaders.loop.Loop;

public class StoreSlot extends GameObject2D implements ICollider2DOwner {
    private IStoreItem item;
    private long price;
    private GameLoop gameLoop;
    private SpriteRenderer spriteRenderer;

    public StoreSlot(GameLoop loop, long price, IStoreItem item, long spawnX, long spawnY) {
        super(loop);
        this.item = item;
        this.price = price;
        this.gameLoop = loop;

        spriteRenderer = new SpriteRenderer(loop);
        spriteRenderer.setSpriteRef(item.getSpriteRef());
        addChild(spriteRenderer);

        Collider2D collider2D = new Collider2D(loop, this);
        collider2D.boundsPosX = -spriteRenderer.getSpritePivotX();
        collider2D.boundsPosY = -spriteRenderer.getSpritePivotY();
        collider2D.boundsWidth = ((long)spriteRenderer.getSpriteWidth()) << 16;
        collider2D.boundsHeight = ((long)spriteRenderer.getSpriteHeight()) << 16;
        addChild(collider2D);

        setPosX(spawnX);
        setPosY(spawnY);
    }

    @Override
    protected void draw(Graphics2D g) {
        super.draw(g);

        //TODO 좀 더 보기 좋게 만들기
        long startX = getPosX() >> 16;
        long startY = (getPosY() >> 16) + 50;
        g.setColor(Color.white);
        g.drawString(item.getName(), startX - 32, startY);
        g.setColor(Color.yellow);
        g.drawString(Long.toString(price), startX, startY + 15);
    }

    @Override
    public void collidedWith(ICollider2DOwner collider) {
        if (collider instanceof PlayerShip){
            if(gameLoop.decreaseCoin(price)){
                if(item.onAcquire(gameLoop)){
                    destroy();
                }
            }
            else{
                //TODO GUI 표시로 바꾸기
                System.out.println("코인 갯수 부족 합니다!");
            }
        }
    }

}
