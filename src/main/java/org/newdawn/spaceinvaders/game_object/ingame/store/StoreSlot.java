package org.newdawn.spaceinvaders.game_object.ingame.store;

import java.awt.Color;
import java.awt.Graphics2D;

import org.newdawn.spaceinvaders.enums.IndicatorTextType;
import org.newdawn.spaceinvaders.fixed_point.FixedPointUtil;
import org.newdawn.spaceinvaders.game_object.GameObject2D;
import org.newdawn.spaceinvaders.game_object.collision.Collider2D;
import org.newdawn.spaceinvaders.game_object.collision.ICollider2DOwner;
import org.newdawn.spaceinvaders.game_object.gui.TextRenderer;
import org.newdawn.spaceinvaders.game_object.ingame.PlayerShip;
import org.newdawn.spaceinvaders.game_object.visual.SpriteRenderer;
import org.newdawn.spaceinvaders.loop.GameLoop;
import org.newdawn.spaceinvaders.loop.Loop;

public class StoreSlot extends GameObject2D implements ICollider2DOwner {
    private IStoreItem item;
    private boolean isPriceUnkown = false;
    private long price;
    private GameLoop gameLoop;
    private SpriteRenderer spriteRenderer;
    private TextRenderer itemNameText;
    private TextRenderer priceText;
    
    public IStoreItem getItem() { return item; }
    public void setPrice(long price) { this.price = price; }
    public void setPriceUnkown(boolean isPriceUnkown) { this.isPriceUnkown = isPriceUnkown; }
    
    // Kryo 역직렬화를 위한 매개변수 없는 생성자
    public StoreSlot(){
        super();
    }
    public StoreSlot(GameLoop loop, long price, IStoreItem item, long spawnX, long spawnY) {
        super(loop);
        this.item = item;
        this.price = price;
        this.gameLoop = loop;

        spriteRenderer = new SpriteRenderer(loop);
        spriteRenderer.setSpriteRef(item.getSpriteRef());

        long spriteHalfHeight = FixedPointUtil.div(spriteRenderer.getSpriteHeight() << 16, 2 << 16);
        itemNameText = new TextRenderer(loop, item.getName(), 10);
        itemNameText.alignment = 1;
        itemNameText.setPos(getPosX(), getPosY() + spriteHalfHeight);
        itemNameText.setFontStyle(1);
        
        priceText = new TextRenderer(loop, Long.toString(price), 10, Color.yellow);
        itemNameText.alignment = 1;
        priceText.setPos(getPosX(), getPosY() + spriteHalfHeight + (13 << 16));

        addChild(spriteRenderer);
        addChild(itemNameText);
        addChild(priceText);

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
        String priceTextContent = "";
        if (isPriceUnkown){
            priceTextContent = "?";
        }
        else{
            priceTextContent = Long.toString(price);
        }

        priceText.setText(priceTextContent);
        super.draw(g);
    }

    @Override
    public void collidedWith(ICollider2DOwner collider) {
        if (collider instanceof PlayerShip){
            if(gameLoop.decreaseCoin(price)){
                if(item.onAcquire(gameLoop, (PlayerShip)collider)){
                    destroy();
                }
                else{
                    gameLoop.increaseCoin(price); //* IStoreItem의 내부 구매 조건이 충족 되지 않았다면, 환불해줌.
                }
            }
            else{
                gameLoop.showIndicatorText("코인 갯수가 부족 합니다!", IndicatorTextType.Warning);
            }
        }
    }

}
