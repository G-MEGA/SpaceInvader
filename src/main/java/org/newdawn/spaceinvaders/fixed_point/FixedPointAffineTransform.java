package org.newdawn.spaceinvaders.fixed_point;

import java.awt.geom.AffineTransform;


public class FixedPointAffineTransform {
    long posX = 0;
    long posY = 0;
    long rotation = 0;
    long scale = FixedPointUtil.ONE;

    // 이상의 정보로부터 다음 값들을 계산
    AffineTransform affineTransform = new AffineTransform();
    AffineTransform tempTransform = new AffineTransform();
    long xAxisX = FixedPointUtil.ONE;
    long xAxisY = 0L;
    long yAxisX = 0L;
    long yAxisY = FixedPointUtil.ONE;
    double rotationInRadians = 0.0;

    // Kryo 역직렬화를 위한 매개변수 없는 생성자
    public  FixedPointAffineTransform(){
        super();
    }

    public void setTransform(long posX, long posY, long rotation, long scale) {
        this.posX = posX;
        this.posY = posY;
        this.rotation = rotation;
        this.scale = scale;
        update();
    }
    public void setPosX(long posX) {
        this.posX = posX;
        update();
    }
    public void setPosY(long posY) {
        this.posY = posY;
        update();
    }
    public void setPos(long posX, long posY) {
        this.posX = posX;
        this.posY = posY;
        update();
    }
    public void setRotation(long degrees) {
        while (degrees > 360 << 16) {
            degrees -= 360 << 16;
        }
        while (degrees < 0) {
            degrees += 360 << 16;
        }

        this.rotation = degrees;
        update();
    }
    public void setPosRotation(long posX, long posY, long degrees) {
        this.posX = posX;
        this.posY = posY;

        while (degrees > 360 << 16) {
            degrees -= 360 << 16;
        }
        while (degrees < 0) {
            degrees += 360 << 16;
        }

        this.rotation = degrees;
        update();
    }
    public void setScale(long scale) {
        this.scale = scale;
        update();
    }

    public long getPosX() {return posX;}
    public long getPosY() {return posY;}
    public long getRotation() {return rotation;}
    public long getScale() {return scale;}

    public AffineTransform getAffineTransform() {return affineTransform;}
    public long getXAxisX() {return xAxisX;}
    public long getXAxisY() {return xAxisY;}
    public long getYAxisX() {return yAxisX;}
    public long getYAxisY() {return yAxisY;}
    public double getRotationInRadians() {return rotationInRadians;}

    void update(){
        //region affineTransform업데이트
        double x = FixedPointUtil.toDouble(this.posX);
        double y = FixedPointUtil.toDouble(this.posY);
        double rotation = Math.toRadians(FixedPointUtil.toDouble(this.rotation));
        double scale = FixedPointUtil.toDouble(this.scale);

        affineTransform.setToIdentity();

        tempTransform.setToScale(scale, scale);
        affineTransform.preConcatenate(tempTransform);

        tempTransform.setToRotation(rotation);
        affineTransform.preConcatenate(tempTransform);

        tempTransform.setToTranslation(x, y);
        affineTransform.preConcatenate(tempTransform);
        //endregion
        xAxisX = FixedPointUtil.mul(FixedPointUtil.cos(this.rotation), this.scale);
        xAxisY = FixedPointUtil.mul(FixedPointUtil.sin(this.rotation), this.scale);
        yAxisX = -xAxisY;
        yAxisY = xAxisX;
        rotationInRadians = Math.toRadians(FixedPointUtil.toDouble(this.rotation));
    }
}
