package com.logicbreadboard.simulator;

import android.graphics.Canvas;

/**
 * Created on 5/10/15.
 */
public class Cursor extends DeviceViewer {
    public static final float CURSOR_RADIUS = 32.0f;
    public static int touchX;
    public static int touchY;
    boolean enabled = false;
    private int x;
    private int y;
    boolean touch = false;

    Cursor(int x, int y) {
        set(x, y);
    }

    public void redCursor(Router router) {
        Context.cursorPaint = Context.redCursorPaint;
    }

    public void normalCursor() {
        Context.cursorPaint = Context.normalCursorPaint;
    }

    public void draw(Canvas canvas, float scaleFactor) {
        if (enabled) {
            Context.cursorPaint.setStrokeWidth(2 / scaleFactor);
            this.left = Math.round(x - CURSOR_RADIUS / scaleFactor);
            this.right = Math.round(x + CURSOR_RADIUS / scaleFactor);
            this.top = Math.round(y - CURSOR_RADIUS / scaleFactor);
            this.bottom = Math.round(y + CURSOR_RADIUS / scaleFactor);

            canvas.drawLine(left, y, right, y, Context.cursorPaint);
            canvas.drawLine(x, top, x, bottom, Context.cursorPaint);
        }
        if (touch) {
            canvas.drawCircle(touchX, touchY, 12, Context.cursorPaint);
            canvas.drawCircle(touchX, touchY, 10, Context.cursorPaint);
            canvas.drawCircle(touchX, touchY, 8, Context.cursorPaint);
        }
    }

    void set(float newX, float newY) {
        int iNewX = Math.round(newX);
        int iNewY = Math.round(newY);
        if (iNewX < EditorModel.MIN_X) {
            x = EditorModel.MIN_X;
        } else if (iNewX > EditorModel.MAX_X) {
            x = EditorModel.MAX_X;
        } else {
            x = iNewX;
        }
        if (iNewY < EditorModel.MIN_Y) {
            y = EditorModel.MIN_Y;
        } else if (iNewY > EditorModel.MAX_Y) {
            y = EditorModel.MAX_Y;
        } else {
            y = iNewY;
        }
//        Log.i("set", " Cursor.x=" + x + " " + "Cursor.y=" + y);
    }

    void setNoCheck(float newX, float newY) {
        int iNewX = Math.round(newX);
        int iNewY = Math.round(newY);
        x = iNewX;
        y = iNewY;
//        Log.i("set", " Cursor.x=" + x + " " + "Cursor.y=" + y);
    }

    // do not disturb move to center
    public int getLeft() {
        return EditorModel.MAX_X;
    }

    public int getTop() {
        return EditorModel.MAX_Y;
    }

    public int getBottom() {
        return EditorModel.MIN_Y;
    }

    public int getRight() {
        return EditorModel.MIN_X;
    }

    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }

}
