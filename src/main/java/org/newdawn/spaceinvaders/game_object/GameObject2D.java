package org.newdawn.spaceinvaders.game_object;

import org.newdawn.spaceinvaders.Game;
import org.newdawn.spaceinvaders.loop.Loop;

import java.awt.geom.AffineTransform;

public abstract class GameObject2D extends GameObject{
    AffineTransform transform = new AffineTransform();
    AffineTransform globalTransform = new AffineTransform();
    public GameObject2D(Loop loop) {
        super(loop);
    }


    public AffineTransform getGlobalTransform(){
        globalTransform.setTransform(transform);

        GameObject p = getParent();
        while (p instanceof GameObject2D){
            globalTransform.preConcatenate(((GameObject2D) p).transform);

            p = p.getParent();
        }

        return globalTransform;
    }

    public double getX(){
        return transform.getTranslateX();
    }
    public double getY(){
        return transform.getTranslateY();
    }

    double[] transformMatrix = new double[6];
    public void setX(double x){
        transform.getMatrix(transformMatrix);

        transform.setTransform(
                transformMatrix[0], transformMatrix[1],
                transformMatrix[2], transformMatrix[3],
                x, transformMatrix[5]
        );
    }
    public void setY(double y){
        transform.getMatrix(transformMatrix);

        transform.setTransform(
                transformMatrix[0], transformMatrix[1],
                transformMatrix[2], transformMatrix[3],
                transformMatrix[4], y
        );
    }
    public void setPosition(double x, double y){
        transform.getMatrix(transformMatrix);

        transform.setTransform(
                transformMatrix[0], transformMatrix[1],
                transformMatrix[2], transformMatrix[3],
                x, y
        );
    }
    public void setRotation(double angle){
        transform.getMatrix(transformMatrix);

        double xAxisLength = Math.sqrt(transformMatrix[0] * transformMatrix[0] + transformMatrix[1] * transformMatrix[1]);
        double yAxisLength = Math.sqrt(transformMatrix[2] * transformMatrix[2] + transformMatrix[3] * transformMatrix[3]);

        transform.setTransform(
                Math.cos(angle) * xAxisLength, Math.sin(angle) * xAxisLength,
                Math.cos(angle + Math.PI*0.5) * yAxisLength, Math.sin(angle + Math.PI*0.5) * yAxisLength,
                transformMatrix[4], transformMatrix[5]
        );
    }
    public void setScale(double scale){
        transform.getMatrix(transformMatrix);

        double xAxisLength = Math.sqrt(transformMatrix[0] * transformMatrix[0] + transformMatrix[1] * transformMatrix[1]);
        double yAxisLength = Math.sqrt(transformMatrix[2] * transformMatrix[2] + transformMatrix[3] * transformMatrix[3]);


        transform.setTransform(
                transformMatrix[0]/xAxisLength * scale, transformMatrix[1]/xAxisLength * scale,
                transformMatrix[2]/yAxisLength * scale, transformMatrix[3]/yAxisLength * scale,
                transformMatrix[4], transformMatrix[5]
        );
    }
}
