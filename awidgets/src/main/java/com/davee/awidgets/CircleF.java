package com.davee.awidgets;

import android.graphics.Point;
import androidx.annotation.FloatRange;

/**
 * CircleF
 * <p>
 * <bold>Note:</bold>
 * <p>- angle(0 degrees) start from right, sweep by clockwise direction</p>
 * Created by davee 2018/7/9.
 * Copyright (c) 2018 davee. All rights reserved.
 */
public class CircleF {
    
    private static final double MIN_NUM = 1.0E-15;
    
    public float centerX;
    public float centerY;
    public float radius;
    
    public CircleF() {
    }
    
    public CircleF(float centerX, float centerY, float radius) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.radius = radius;
    }
    
    public boolean containsPoint(double px, double py){
        return radius >= distanceToCenter(px, py);
    }
    
    public double distanceToCenter(double x1, double y1){
        return Math.sqrt(Math.pow(x1 - centerX, 2) + Math.pow(y1 - centerY, 2));
    }
    
    public Point pointWithAngle(float angle/*degrees*/){
        double x = xWithAngle(angle);
        double y = yWithAngle(angle);
        return new Point((int)x, (int)y);
    }
    
    public Point pointWithAngle(float angle/*degrees*/, float customRadius){
        double x = xWithAngle(angle, customRadius);
        double y = yWithAngle(angle, customRadius);
        return new Point((int)x, (int)y);
    }
    
    public float xWithAngle(@FloatRange(from = 0.0f, to = 360.f) float angle/*degrees*/){
        double weight = Math.cos(Math.toRadians(angle));
        if (Math.abs(weight) < MIN_NUM){
            weight = 0;
        }
        return (float) (centerX + radius * weight);
    }
    
    public float xWithAngle(@FloatRange(from = 0.0f, to = 360.f) float angle/*degrees*/, float customRadius){
        double weight = Math.cos(Math.toRadians(angle));
        if (Math.abs(weight) < MIN_NUM){
            weight = 0d;
        }
        return (float) (centerX + customRadius * weight);
    }
    
    public float yWithAngle(@FloatRange(from = 0.0f, to = 360.f) float angle/*degrees*/){
        double weight = Math.sin(Math.toRadians(angle));
        if (Math.abs(weight) < MIN_NUM){
            weight = 0d;
        }
        return (float) (centerY + radius * weight);
    }
    
    public float yWithAngle(@FloatRange(from = 0.0f, to = 360.f) float angle/*degrees*/, float customRadius){
        double weight = Math.sin(Math.toRadians(angle));
        if (Math.abs(weight) < MIN_NUM){
            weight = 0d;
        }
        return (float) (centerY + customRadius * weight);
    }
    
    
    // public static void main(String[] arguments){
    //     CircleF circleF = new CircleF();
    //     circleF.centerX = 0;
    //     circleF.centerY = 0;
    //     circleF.radius = 1;
    //
    //     for (int i = 0; i < 8; i++) {
    //         int angle = 45 * i;
    //         float x = circleF.xWithAngle(45 * i);
    //         float y = circleF.yWithAngle(45 * i);
    //         System.out.println("angle = " + angle + ", x = " + x +", y = " + y);
    //     }
    //     System.exit(0);
    // }
}
