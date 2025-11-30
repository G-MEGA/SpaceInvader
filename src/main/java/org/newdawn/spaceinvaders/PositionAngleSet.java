package org.newdawn.spaceinvaders;

public class PositionAngleSet {
    public long positionX;
    public long positionY;
    public long angle;

    public PositionAngleSet() {
        // Kryo 역직렬화를 위한 매개변수 없는 생성자
    }

    public PositionAngleSet(long positionX, long positionY) {
        this.positionX = positionX;
        this.positionY = positionY;
        angle = 0;
    }
    
    public PositionAngleSet(long positionX, long positionY, long angle) {
        this.positionX = positionX;
        this.positionY = positionY;
        this.angle = angle;
    }
}
