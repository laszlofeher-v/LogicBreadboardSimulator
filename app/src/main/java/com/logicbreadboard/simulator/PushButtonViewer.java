package com.logicbreadboard.simulator;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 * Created on 8/31/15.
 */
public class PushButtonViewer extends LogicalToggleButtonViewer {
    static RectF paramRect = new RectF(0, 0, 0, 0);

    PushButtonViewer() {
        model = new LogicalButtonModel();
    }

    PushButtonViewer(boolean initialValue) {
        model = new LogicalButtonModel();
        if (initialValue) {
            ((LogicalButtonModel) model).initialLevel = State.Level.high;
            ((LogicalButtonModel) model).level = State.Level.high;
            color = HIGH_COLOR;
        } else {
            ((LogicalButtonModel) model).initialLevel = State.Level.low;
            color = LOW_COLOR;
        }
    }

    @Override
    public int getContextMenuMask() {
        return CONTEXT_MENU_MASK;
    }

    @Override
    public short getGroup() {
        return (short) SchematicViewerFactory.DeviceGroup.Sources.ordinal();
    }

    @Override
    public short getSymbol() {
        if (((LogicalButtonModel) model).initialLevel == State.Level.high) {
            return (short) SchematicViewerFactory.Sources.Push_Button_H.ordinal();
        } else {
            return (short) SchematicViewerFactory.Sources.Push_Button_L.ordinal();
        }
    }

    @Override
    public void setDefaultNumberOfIos() {
        setNumberOfIos((short) 1);
    }

    protected void createOneIo() {
        DeviceIOViewer iov = new DeviceIOViewer((short) ((BUTTON_WIDTH) / 2), (short) 0,
                (short) (DeviceIOViewer.LENGTH + (BUTTON_WIDTH) / 2), (short) 0, false);
        iov.model = ((DeviceModel) model).firstOutputModel;
        firstIoViewer = iov;
    }

    @Override
    public void setNumberOfIos(short numberOfIos) {
        this.numberOfIos = numberOfIos;
        model.init(0,numberOfIos);
        createOneIo();
    }

    public void draw(Canvas canvas) {
        super.draw(canvas);
        rotateCanvas(canvas);
        drawRotated(canvas, currentPaint);
        restoreCanvas(canvas);
    }

    public void drawEnlarged(Canvas canvas, float zoom) {
        selectionZoom = zoom;
        canvas.save();
        canvas.scale(zoom, zoom, left + (right - left) / 2, top + (bottom - top) / 2);
        empty = true;
        draw(canvas);
        empty = false;
        canvas.restore();
    }

    @Override
    public boolean pushButton(PushButtonMsg pushButtonMsg) {
        if (enclosingLeft <= pushButtonMsg.pointerX &&
                enclosingRight >= pushButtonMsg.pointerX &&
                enclosingBottom >= pushButtonMsg.pointerY &&
                enclosingTop <= pushButtonMsg.pointerY) {
            ((LogicalButtonModel) model).setLevel(State.negate(((LogicalButtonModel) model)
                    .initialLevel));
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean releaseButton(PushButtonMsg pushButtonMsg) {
        if (enclosingLeft <= pushButtonMsg.pointerX &&
                enclosingRight >= pushButtonMsg.pointerX &&
                enclosingBottom >= pushButtonMsg.pointerY &&
                enclosingTop <= pushButtonMsg.pointerY) {
            ((LogicalButtonModel) model).setLevel(((LogicalButtonModel) model).initialLevel);
            return true;
        } else {
            return false;
        }
    }

    @Override
    void load(DataInputStream inputStream) throws java.io.IOException {
        super.load(inputStream);
        model.readDelays(inputStream);
    }

    @Override
    void save(DataOutputStream outputStream) throws java.io.IOException {
        super.save(outputStream);
        model.writeDelays(outputStream);
    }


    @Override
    public void initPosition(int x, int y, int angleIndex) {
        this.left = x;
        this.bottom = y;
        this.right = x + BUTTON_WIDTH;
        this.top = y - BUTTON_HEIGHT;
        this.angleIndex = angleIndex;
        setEnclosingRect();
        //       this.numberOfIos = numberOfIos;
        //       this.selectedIos = new boolean[numberOfIos];
    }

    @Override
    protected void drawRotated(Canvas canvas, Paint paint) {
        paramRect.left = left;
        paramRect.right = right;
        paramRect.top = top;
        paramRect.bottom = bottom;
        canvas.drawRoundRect(paramRect, BUTTON_WIDTH / 3, BUTTON_HEIGHT / 3, paint);
        if (!empty) {
            paint.setStyle(Paint.Style.FILL);
            int savedColor = paint.getColor();
            if (((LogicalButtonModel) model).level == State.Level.high) {
                color = HIGH_COLOR;
            } else {
                color = LOW_COLOR;
            }
            paint.setColor(color);
            canvas.drawCircle(right - BUTTON_WIDTH / 2, top + BUTTON_HEIGHT / 2, BUTTON_HEIGHT / 3,
                    paint);
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(savedColor);
        }
    }

    @Override
    float getSelectionZoom(int canvasHeight, int canvasWidth, float scaleFactor) {
        return Math.min(canvasWidth / (8 * (right - left)),
                canvasHeight / (8 * (bottom - top))) / (scaleFactor);
    }

    public int getWidth() {
        return BUTTON_WIDTH;
    }

    public int getHeight() {
        return BUTTON_HEIGHT;
    }

}
