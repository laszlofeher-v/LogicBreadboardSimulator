package com.logicbreadboard.simulator;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 * Created by laci on 7/29/15.
 */
public class LogicalToggleButtonViewer extends DeviceViewer {
    static final int BUTTON_WIDTH = 16;
    static final int BUTTON_HEIGHT = 16;
    static final int HIGH_COLOR = Color.RED;
    static final int UNDEFINED_COLOR = Color.YELLOW;
    static final int LOW_COLOR = Color.GREEN;
    static protected final int CONTEXT_MENU_MASK = ContextMenuIcons.COPY |
            ContextMenuIcons.DELETE |
            ContextMenuIcons.DELAY |
            ContextMenuIcons.ROTATE |
            ContextMenuIcons.ALIGN_HORIZONTAL | ContextMenuIcons.ALIGN_VERTICAL|
            ContextMenuIcons.LOCK| ContextMenuIcons.UNLOCK|
            ContextMenuIcons.UNSELECT;
    static boolean empty = false;
    int color;

    LogicalToggleButtonViewer() {
        model = new LogicalButtonModel();
    }

    LogicalToggleButtonViewer(boolean initialValue) {
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
            return (short) SchematicViewerFactory.Sources.Toggle_Button_H.ordinal();
        } else {
            return (short) SchematicViewerFactory.Sources.Toggle_Button_L.ordinal();
        }
    }

    @Override
    float getSelectionZoom(int canvasHeight, int canvasWidth, float scaleFactor) {
        return Math.min(canvasWidth / (8 * (right - left)),
                canvasHeight / (8 * (bottom - top))) / (scaleFactor);
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
            ((LogicalButtonModel) model).changeLevel();
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
        canvas.drawLine(left, bottom, left, top, paint);
        canvas.drawLine(right, bottom, right, top, paint);
        canvas.drawLine(left, bottom, right, bottom, paint);
        canvas.drawLine(left, top, right, top, paint);
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

    public int getWidth() {
        return BUTTON_WIDTH;
    }

    public int getHeight() {
        return BUTTON_HEIGHT;
    }

}
