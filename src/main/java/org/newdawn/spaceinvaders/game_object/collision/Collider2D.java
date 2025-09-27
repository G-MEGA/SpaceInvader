package org.newdawn.spaceinvaders.game_object.collision;

import org.newdawn.spaceinvaders.fixed_point.FixedPointAffineTransform;
import org.newdawn.spaceinvaders.fixed_point.FixedPointUtil;
import org.newdawn.spaceinvaders.game_object.GameObject2D;
import org.newdawn.spaceinvaders.loop.Loop;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class Collider2D extends GameObject2D {
    private ICollider2DOwner owner;
    private boolean drawBounds = false;
    public void setDrawBounds(boolean drawBounds) {
        this.drawBounds = drawBounds;
    }

    public long boundsPosX = 0;
    public long boundsPosY = 0;
    public long boundsWidth = 0;
    public long boundsHeight = 0;

    // left-top, right_top, right_bottom, left_bottom
    long[] globalVertices = new long[8];

    // Kryo 역직렬화를 위한 매개변수 없는 생성자
    public Collider2D(){
        super();
    }
    public Collider2D(Loop loop, ICollider2DOwner owner) {
        super(loop);
        this.owner = owner;
    }
    public void onDestroy() {
        super.onDestroy();
        this.owner = null;
    }

    public ICollider2DOwner getOwner() {
        return owner;
    }
    public long[] getGlobalVertices() {
        FixedPointAffineTransform gt = getGlobalTransform();
        long xAxisX = gt.getXAxisX();
        long xAxisY = gt.getXAxisY();
        long yAxisX = gt.getYAxisX();
        long yAxisY = gt.getYAxisY();

        long x;
        long y;
        // 근거
//        (xAxisX, xAxisY) * x + (yAxisX, yAxisY) * y
//        = (xAxisX*x, xAxisY*x) + (yAxisX*y, yAxisY*y)
//        = (xAxisX*x + yAxisX*y, xAxisY*x + yAxisY*y)
        x = boundsPosX;
        y = boundsPosY;
        globalVertices[0] = FixedPointUtil.mul(xAxisX, x) + FixedPointUtil.mul(yAxisX, y);
        globalVertices[1] = FixedPointUtil.mul(xAxisY, x) + FixedPointUtil.mul(yAxisY, y);
        x = boundsPosX + boundsWidth;
        y = boundsPosY;
        globalVertices[2] = FixedPointUtil.mul(xAxisX, x) + FixedPointUtil.mul(yAxisX, y);
        globalVertices[3] = FixedPointUtil.mul(xAxisY, x) + FixedPointUtil.mul(yAxisY, y);
        x = boundsPosX + boundsWidth;
        y = boundsPosY + boundsHeight;
        globalVertices[4] = FixedPointUtil.mul(xAxisX, x) + FixedPointUtil.mul(yAxisX, y);
        globalVertices[5] = FixedPointUtil.mul(xAxisY, x) + FixedPointUtil.mul(yAxisY, y);
        x = boundsPosX;
        y = boundsPosY +  boundsHeight;
        globalVertices[6] = FixedPointUtil.mul(xAxisX, x) + FixedPointUtil.mul(yAxisX, y);
        globalVertices[7] = FixedPointUtil.mul(xAxisY, x) + FixedPointUtil.mul(yAxisY, y);

        globalVertices[0] += gt.getPosX();
        globalVertices[2] += gt.getPosX();
        globalVertices[4] += gt.getPosX();
        globalVertices[6] += gt.getPosX();

        globalVertices[1] += gt.getPosY();
        globalVertices[3] += gt.getPosY();
        globalVertices[5] += gt.getPosY();
        globalVertices[7] += gt.getPosY();

        return globalVertices;
    }

    @Override
    protected void onInLoopUpdated(boolean value) {
        super.onInLoopUpdated(value);

        if (value) {
            getLoop().addCollider(this);
        }
        else{
            getLoop().removeCollider(this);
        }
    }

    AffineTransform tempTransformToDraw = new AffineTransform();
    public void draw(Graphics2D g2d) {
        super.draw(g2d);

        if(drawBounds){
            long[] globalVertices = getGlobalVertices();

            g2d.setColor(Color.RED);
            g2d.drawLine(
                    FixedPointUtil.toInt(globalVertices[0]), FixedPointUtil.toInt(globalVertices[1]),
                    FixedPointUtil.toInt(globalVertices[2]), FixedPointUtil.toInt(globalVertices[3])
            );
            g2d.drawLine(
                    FixedPointUtil.toInt(globalVertices[2]), FixedPointUtil.toInt(globalVertices[3]),
                    FixedPointUtil.toInt(globalVertices[4]), FixedPointUtil.toInt(globalVertices[5])
            );
            g2d.drawLine(
                    FixedPointUtil.toInt(globalVertices[4]), FixedPointUtil.toInt(globalVertices[5]),
                    FixedPointUtil.toInt(globalVertices[6]), FixedPointUtil.toInt(globalVertices[7])
            );
            g2d.drawLine(
                    FixedPointUtil.toInt(globalVertices[6]), FixedPointUtil.toInt(globalVertices[7]),
                    FixedPointUtil.toInt(globalVertices[0]), FixedPointUtil.toInt(globalVertices[1])
            );

//            long[] globalBounds = getGlobalBounds();
//
//            AffineTransform saveTransform = g2d.getTransform();
//
//            tempTransformToDraw.setTransform(g2d.getTransform());
//            tempTransformToDraw.rotate(
//                    getGlobalTransform().getRotationInRadians(),
//                    getGlobalTransform().getAffineTransform().getTranslateX(),
//                    getGlobalTransform().getAffineTransform().getTranslateY());
//            g2d.setTransform(tempTransformToDraw);
//            g2d.setColor(Color.RED);
//            g2d.drawRect(
//                    (int) FixedPointUtil.toDouble(globalBounds[0]),
//                    (int) FixedPointUtil.toDouble(globalBounds[1]),
//                    (int) FixedPointUtil.toDouble(globalBounds[2]),
//                    (int) FixedPointUtil.toDouble(globalBounds[3])
//            );
//
//            g2d.setTransform(saveTransform);
        }
    }

    public boolean collidesWith(Collider2D other) {
        long[] globalVertices = getGlobalVertices();
        long myLeftTopX = globalVertices[0];
        long myLeftTopY = globalVertices[1];
        long myRightTopX = globalVertices[2];
        long myRightTopY = globalVertices[3];
        long myRightBottomX = globalVertices[4];
        long myRightBottomY = globalVertices[5];
        long myLeftBottomX = globalVertices[6];
        long myLeftBottomY = globalVertices[7];

        // 평균으로 중앙지점 구하기. 합하고 2로 나눔
        long myCenterX = (myLeftTopX + myRightBottomX) / 2;
        long myCenterY = (myLeftTopY + myRightBottomY) / 2;
        // 두 대각선 벡터 절반 구함
        long myHalfDiagonalAX = (myLeftTopX - myCenterX);
        long myHalfDiagonalAY = (myLeftTopY - myCenterY);
        long myHalfDiagonalBX = (myRightTopX - myCenterX);
        long myHalfDiagonalBY = (myRightTopY - myCenterY);
        


        globalVertices = other.getGlobalVertices();
        long otherLeftTopX = globalVertices[0];
        long otherLeftTopY = globalVertices[1];
        long otherRightTopX = globalVertices[2];
        long otherRightTopY = globalVertices[3];
        long otherRightBottomX = globalVertices[4];
        long otherRightBottomY = globalVertices[5];
        long otherLeftBottomX = globalVertices[6];
        long otherLeftBottomY = globalVertices[7];
        
        // 평균으로 중앙지점 구하기. 합하고 2로 나눔
        long otherCenterX = (otherLeftTopX + otherRightBottomX) / 2;
        long otherCenterY = (otherLeftTopY + otherRightBottomY) / 2;
        // 두 대각선 벡터 절반 구함
        long otherHalfDiagonalAX = (otherLeftTopX - otherCenterX);
        long otherHalfDiagonalAY = (otherLeftTopY - otherCenterY);
        long otherHalfDiagonalBX = (otherRightTopX - otherCenterX);
        long otherHalfDiagonalBY = (otherRightTopY - otherCenterY);


        // 나와 상대의 중심 간 거리를 구하기 위하여
        long meToOtherX = otherCenterX - myCenterX;
        long meToOtherY = otherCenterY - myCenterY;

        long axisX;
        long axisY;
        long another;
        long dotHalfDiagonal;
        long dotMeToOther;

        // my X축에 대하여
        axisX = getGlobalTransform().getXAxisX();
        axisY = getGlobalTransform().getXAxisY();
        another = Math.abs(FixedPointUtil.mul(boundsWidth / 2, getGlobalTransform().getScale()));
        dotHalfDiagonal = Math.max(
                FixedPointUtil.dotAbs(otherHalfDiagonalAX, otherHalfDiagonalAY, axisX, axisY),
                FixedPointUtil.dotAbs(otherHalfDiagonalBX, otherHalfDiagonalBY, axisX, axisY)  );
        dotMeToOther = FixedPointUtil.dotAbs(meToOtherX, meToOtherY, axisX, axisY);
        if(another + dotHalfDiagonal < dotMeToOther) return false;

        // my Y축에 대하여
        axisX = getGlobalTransform().getYAxisX();
        axisY = getGlobalTransform().getYAxisY();
        another = Math.abs(FixedPointUtil.mul(boundsHeight / 2, getGlobalTransform().getScale()));
        dotHalfDiagonal = Math.max(
                FixedPointUtil.dotAbs(otherHalfDiagonalAX, otherHalfDiagonalAY, axisX, axisY),
                FixedPointUtil.dotAbs(otherHalfDiagonalBX, otherHalfDiagonalBY, axisX, axisY)  );
        dotMeToOther = FixedPointUtil.dotAbs(meToOtherX, meToOtherY, axisX, axisY);
        if(another + dotHalfDiagonal < dotMeToOther) return false;

        // other X축에 대하여
        axisX = other.getGlobalTransform().getXAxisX();
        axisY = other.getGlobalTransform().getXAxisY();
        another = Math.abs(FixedPointUtil.mul(other.boundsWidth / 2, other.getGlobalTransform().getScale()));
        dotHalfDiagonal = Math.max(
                FixedPointUtil.dotAbs(myHalfDiagonalAX, myHalfDiagonalAY, axisX, axisY),
                FixedPointUtil.dotAbs(myHalfDiagonalBX, myHalfDiagonalBY, axisX, axisY)  );
        dotMeToOther = FixedPointUtil.dotAbs(meToOtherX, meToOtherY, axisX, axisY);
        if(another + dotHalfDiagonal < dotMeToOther) return false;

        // other Y축에 대하여
        axisX = other.getGlobalTransform().getYAxisX();
        axisY = other.getGlobalTransform().getYAxisY();
        another = Math.abs(FixedPointUtil.mul(other.boundsHeight / 2, other.getGlobalTransform().getScale()));
        dotHalfDiagonal = Math.max(
                FixedPointUtil.dotAbs(myHalfDiagonalAX, myHalfDiagonalAY, axisX, axisY),
                FixedPointUtil.dotAbs(myHalfDiagonalBX, myHalfDiagonalBY, axisX, axisY)  );
        dotMeToOther = FixedPointUtil.dotAbs(meToOtherX, meToOtherY, axisX, axisY);
        if(another + dotHalfDiagonal < dotMeToOther) return false;

        return true;
        
//        long[] myBounds = getGlobalBounds();
//        long[] otherBounds = other.getGlobalBounds();
//
//        long myStartX = myBounds[0];
//        long myStartY = myBounds[1];
//        long myEndX = myBounds[0] + myBounds[2];
//        long myEndY = myBounds[1] + myBounds[3];
//
//        long otherStartX = otherBounds[0];
//        long otherStartY = otherBounds[1];
//        long otherEndX = otherBounds[0] + otherBounds[2];
//        long otherEndY = otherBounds[1] + otherBounds[3];
//
//        return myStartX <= otherEndX && myEndX >= otherStartX && myStartY <= otherEndY && myEndY >= otherStartY;
    }
}
