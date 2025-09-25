package org.newdawn.spaceinvaders.game_object.gui;

import org.newdawn.spaceinvaders.fixed_point.FixedPointUtil;
import org.newdawn.spaceinvaders.game_object.GameObject2D;
import org.newdawn.spaceinvaders.loop.Loop;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class Button extends GameObject2D {
    IButtonListener buttonListener;
    int width;
    public int getWidth() {
        return width;
    }
    public long getWidthInFixedPoint() {
        return FixedPointUtil.fromLong(width);
    }
    int height;
    public int getHeight() {
        return height;
    }
    public long getHeightInFixedPoint() {
        return FixedPointUtil.fromLong(height);
    }

    public int alignment = 0;// 0 오른쪽 1 중앙 2 왼쪽 표시

    boolean hovered = false;

    public Button(Loop loop, IButtonListener buttonListener, int width, int height) {
        super(loop);
        this.buttonListener = buttonListener;
        this.width = width;
        this.height = height;
    }

    @Override
    protected void process(long deltaTime) {
        super.process(deltaTime);

        //region 마우스 호버링 판정
        long[] gt = getGlobalTransform();

        long x = gt[0];
        long y = gt[1];

        if(alignment==1){
            x += FixedPointUtil.fromLong(- width/2);
        }
        else if(alignment==2){
            x += FixedPointUtil.fromLong(- width);
        }

        long xEnd = x + FixedPointUtil.fromLong(width);
        long yEnd = y + FixedPointUtil.fromLong(height);
        long mousePosX = FixedPointUtil.fromLong(loop.getMousePosX());
        long mousePosY = FixedPointUtil.fromLong(loop.getMousePosY());

        hovered = mousePosX >= x && mousePosX <= xEnd && mousePosY >= y && mousePosY <= yEnd;
        //endregion

        if(loop.isKeyInputJustPressed("mouse_button_left") && hovered){
            buttonListener.buttonPressed();
        }
    }

    public void draw(Graphics2D g){
        super.draw(g);

        AffineTransform t = getGlobalTransformForDraw();

        int x = (int)t.getTranslateX();
        int y = (int)t.getTranslateY();

        if(alignment == 1){
            x += - width/2;
        }
        else if(alignment == 2){
            x += - width;
        }

        if(hovered){
            g.setColor(Color.WHITE);
        }
        else{
            g.setColor(Color.LIGHT_GRAY);
        }
        g.fillRoundRect(x,y,width,height,5,5);
        if(hovered){
            g.setColor(Color.LIGHT_GRAY);
        }
        else{
            g.setColor(Color.DARK_GRAY);
        }
        g.fillRoundRect(x + 5,y + 5,width - 10,height - 10,5,5);
    }

    public TextRenderer addTextRenderer(String text, int fontSize, Color color, int fontStyle){
        TextRenderer tr = new TextRenderer(loop, text, fontSize, color, fontStyle);
        tr.alignment = alignment;
        addChild(tr);
        return tr;
    }
}
