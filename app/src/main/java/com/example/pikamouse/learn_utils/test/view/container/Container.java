package com.example.pikamouse.learn_utils.test.view.container;

import android.graphics.Canvas;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: jiangfeng
 * @date: 2018/12/28
 */
public class Container {

    private List<Container> mContainers;
    private float x = 0;
    private float y = 0;

    public Container() {
        mContainers = new ArrayList<>();
    }

    public void draw(Canvas canvas) {
        canvas.save();
        canvas.translate(getX(), getY());
        childDraw(canvas);
        for (Container c : mContainers) {
            c.draw(canvas);
        }
        canvas.restore();
    }

    public void childDraw(Canvas canvas) {

    }

    public void addChildren(Container child) {
        mContainers.add(child);
    }

    public void removeChildren(Container child) {
        if (!mContainers.isEmpty()) {
            mContainers.remove(child);
        }
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }
}
