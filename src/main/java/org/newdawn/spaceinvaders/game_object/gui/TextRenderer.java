package org.newdawn.spaceinvaders.game_object.gui;

import org.newdawn.spaceinvaders.game_object.GameObject2D;
import org.newdawn.spaceinvaders.loop.Loop;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class TextRenderer extends GameObject2D {
    String text;
    public void setText(String text)
    {
        this.text = text;

        lines = this.text.split("\n");
    }
    int fontSize;
    Color color;
    int fontStyle;// 0 기본 1 굵게 2 이탤릭

    public int alignment = 0;// 0 오른쪽 1 중앙 2 왼쪽 표시

    Font font;

    String[] lines;

    public TextRenderer(Loop loop, String text, int fontSize, Color color, int fontStyle) {
        super(loop);

        setText(text);
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
        int height = g.getFontMetrics().getHeight();
        int startY = (int)t.getTranslateY() + height;

        g.setColor(color);

        for(int i = 0; i < lines.length; i++){
            String line = lines[i];

            int x = (int)t.getTranslateX();
            int width = g.getFontMetrics().stringWidth(line);
            if(alignment == 1){
                x = (int)t.getTranslateX() - width/2;
            }
            else if(alignment == 2){
                x = (int)t.getTranslateX() - width;
            }
            g.drawString(line,x, startY + i * height);
        }
    }
}
