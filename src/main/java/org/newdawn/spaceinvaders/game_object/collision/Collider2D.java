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
        // 1. 나의 기하 정보 계산
        long[] myV = getGlobalVertices();
        long myCenterX = (myV[0] + myV[4]) / 2;
        long myCenterY = (myV[1] + myV[5]) / 2;
        long myHalfDiagAX = myV[0] - myCenterX;
        long myHalfDiagAY = myV[1] - myCenterY;
        long myHalfDiagBX = myV[2] - myCenterX;
        long myHalfDiagBY = myV[3] - myCenterY;

        // 2. 상대의 기하 정보 계산
        long[] otherV = other.getGlobalVertices();
        long otherCenterX = (otherV[0] + otherV[4]) / 2;
        long otherCenterY = (otherV[1] + otherV[5]) / 2;
        long otherHalfDiagAX = otherV[0] - otherCenterX;
        long otherHalfDiagAY = otherV[1] - otherCenterY;
        long otherHalfDiagBX = otherV[2] - otherCenterX;
        long otherHalfDiagBY = otherV[3] - otherCenterY;

        // 3. 중심 간 거리 벡터
        long toOtherX = otherCenterX - myCenterX;
        long toOtherY = otherCenterY - myCenterY;

        // 4. 분리 여부 판단 (if문 제거)
        // "어느 한 축에서라도 분리되어 있다면(isSeparated... 가 true라면) 충돌이 아님"
        // || 연산자는 앞의 조건이 true면 뒤를 실행하지 않으므로(Short-circuit),
        // 기존 if문과 성능 및 동작이 100% 동일합니다.

        boolean isSeparated =
                // Check 1: 나의 X축
                isSeparatedOnAxis(
                        getGlobalTransform().getXAxisX(), getGlobalTransform().getXAxisY(),
                        getScaledHalfSize(boundsWidth),
                        otherHalfDiagAX, otherHalfDiagAY, otherHalfDiagBX, otherHalfDiagBY,
                        toOtherX, toOtherY)
                        ||
                        // Check 2: 나의 Y축
                        isSeparatedOnAxis(
                                getGlobalTransform().getYAxisX(), getGlobalTransform().getYAxisY(),
                                getScaledHalfSize(boundsHeight),
                                otherHalfDiagAX, otherHalfDiagAY, otherHalfDiagBX, otherHalfDiagBY,
                                toOtherX, toOtherY)
                        ||
                        // Check 3: 상대의 X축
                        isSeparatedOnAxis(
                                other.getGlobalTransform().getXAxisX(), other.getGlobalTransform().getXAxisY(),
                                other.getScaledHalfSize(other.boundsWidth),
                                myHalfDiagAX, myHalfDiagAY, myHalfDiagBX, myHalfDiagBY,
                                toOtherX, toOtherY)
                        ||
                        // Check 4: 상대의 Y축
                        isSeparatedOnAxis(
                                other.getGlobalTransform().getYAxisX(), other.getGlobalTransform().getYAxisY(),
                                other.getScaledHalfSize(other.boundsHeight),
                                myHalfDiagAX, myHalfDiagAY, myHalfDiagBX, myHalfDiagBY,
                                toOtherX, toOtherY);

        // 분리되지 않았다면(!isSeparated) 충돌한 것(true)
        return !isSeparated;
    }

// --- Helper Methods (private) ---

    /**
     * 특정 축(axis)에 대해 분리되어 있는지(충돌하지 않는지) 검사합니다.
     * @param axisX, axisY : 검사할 축의 단위 벡터 (또는 정규화된 축)
     * @param halfSizeOnAxis : 검사 축의 주인인 객체의 투영된 절반 크기 (이미 축 정렬 상태이므로 단순 스칼라값)
     * @param diagAX, diagAY, diagBX, diagBY : 상대 객체의 두 대각선 벡터
     * @param distX, distY : 두 객체 중심 간의 거리 벡터
     */
    private boolean isSeparatedOnAxis(long axisX, long axisY, long halfSizeOnAxis,
                                      long diagAX, long diagAY, long diagBX, long diagBY,
                                      long distX, long distY) {
        // 상대 대각선들을 해당 축에 투영했을 때 더 긴 길이를 구함
        long projectionOther = Math.max(
                FixedPointUtil.dotAbs(diagAX, diagAY, axisX, axisY),
                FixedPointUtil.dotAbs(diagBX, diagBY, axisX, axisY)
        );

        // 중심 간 거리를 해당 축에 투영
        long projectionCenter = FixedPointUtil.dotAbs(distX, distY, axisX, axisY);

        // (내 절반 크기 + 상대 절반 크기) < 중심 거리 이면 분리된 것
        return (halfSizeOnAxis + projectionOther) < projectionCenter;
    }

    /**
     * 월드 스케일이 적용된 절반 크기를 구합니다. (중복 계산 제거)
     */
    private long getScaledHalfSize(long size) {
        return Math.abs(FixedPointUtil.mul(size / 2, getGlobalTransform().getScale()));
    }
}
