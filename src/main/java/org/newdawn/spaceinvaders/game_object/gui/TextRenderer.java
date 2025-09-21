package org.newdawn.spaceinvaders.game_object.gui;

import org.newdawn.spaceinvaders.game_object.GameObject2D;
import org.newdawn.spaceinvaders.loop.Loop;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class TextRenderer extends GameObject2D {
    String text;
    int fontSize;
    Color color;
    int fontStyle;// 0 기본 1 굵게 2 이탤릭

    public int alignment = 0;// 0 오른쪽 1 중앙 2 왼쪽 표시

    Font font;

    public TextRenderer(Loop loop, String text, int fontSize, Color color, int fontStyle) {
        super(loop);

        this.text = text;
        this.fontSize = fontSize;
        this.color = color;
        this.fontStyle = fontStyle;

        font = new Font("Dialog.plain", fontStyle, fontSize);
    }
    public TextRenderer(Loop loop, String text, int fontSize, Color color) {
        this(loop, text, fontSize, color, 0);
    }
    public TextRenderer(Loop loop, String text, int fontSize) {
        this(loop, text, fontSize, Color.WHITE, 0);
    }

    public void draw(Graphics2D g){
        super.draw(g);
        AffineTransform t = getGlobalTransformForDraw();

        g.setFont(font);
        int width = g.getFontMetrics().stringWidth(text);
        int height = g.getFontMetrics().getHeight();

        g.setColor(color);

        if(alignment == 0){
            g.drawString(text,(int)t.getTranslateX(), (int)t.getTranslateY() + height);
        }
        else if(alignment == 1){
            g.drawString(text,(int)t.getTranslateX() - width/2, (int)t.getTranslateY() + height);
        }
        else if(alignment == 2){
            g.drawString(text,(int)t.getTranslateX() - width, (int)t.getTranslateY() + height);
        }
    }
}
