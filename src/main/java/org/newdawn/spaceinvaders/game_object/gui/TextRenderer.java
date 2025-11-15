package org.newdawn.spaceinvaders.game_object.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import org.newdawn.spaceinvaders.game_object.GameObject2D;
import org.newdawn.spaceinvaders.loop.Loop;

public class TextRenderer extends GameObject2D {
    public static int DEFAULT = 0;
    public static int BOLD = 1;
    public static int ITALIC = 2;

    String text;
    public void setText(String text)
    {
        this.text = text;

        lines = this.text.split("\n");
    }
    int fontSize;
    public void setFontSize(int size) {
        fontSize = size;
    }
    Color color;
    public void setColor(Color color) {
        this.color = color;
    }

    int fontStyle;// 0 기본 1 굵게 2 이탤릭
    public void setFontStyle(int fontStyle) {
        this.fontStyle = fontStyle;
        font = new Font("Dialog.plain", fontStyle, fontSize);
    }
    
    public int alignment = 0;// 0 오른쪽 1 중앙 2 왼쪽 표시
    public void setAlignment(int alignment) {
        this.alignment = alignment;
    }
    
    Font font;
  
    String[] lines;

    // Kryo 역직렬화를 위한 매개변수 없는 생성자
    public TextRenderer(){
        super();
    }
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

    @Override
    public void draw(Graphics2D g){
        super.draw(g);
        AffineTransform t = getGlobalTransform().getAffineTransform();

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
