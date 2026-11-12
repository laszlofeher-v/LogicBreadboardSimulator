package com.logicbreadboard.simulator;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 * Created by laci on 7/29/15.
 */
public class LEDViewer extends DeviceViewer {
    static final int LED_WIDTH = 16;
    static final int LED_HEIGHT = 16;
    static final int HIGH_COLOR = Color.RED;
    static final int UNDEFINED_COLOR = Color.YELLOW;
    static final int LOW_COLOR = Color.GREEN;
    static private final int CONTEXT_MENU_MASK =
            ContextMenuIcons.COPY |
                    ContextMenuIcons.DELETE |
                    ContextMenuIcons.ROTATE |
                    ContextMenuIcons.ALIGN_HORIZONTAL | ContextMenuIcons.ALIGN_VERTICAL|
                    ContextMenuIcons.LOCK| ContextMenuIcons.UNLOCK|
                    ContextMenuIcons.UNSELECT;
    static boolean empty = false;
    int color = UNDEFINED_COLOR;

    LEDViewer() {
        model = new LEDModel();
    }

    @Override
    public int getContextMenuMask() {
        return CONTEXT_MENU_MASK;
    }

    @Override
    public short getGroup() {
        return (short) SchematicViewerFactory.DeviceGroup.Displays.ordinal();
    }

    @Override
    public short getSymbol() {
        return (short) SchematicViewerFactory.Displays.LED.ordinal();
    }

    @Override
    public void setDefaultNumberOfIos() {
        setNumberOfIos((short) 1);
    }

    protected void createOneInput() {
        DeviceIOViewer iov = new DeviceIOViewer((short) (-(LED_WIDTH) / 2), (short) 0,
                (short) (-DeviceIOViewer.LENGTH - (LED_WIDTH) / 2), (short) 0, false);
        iov.model = ((DeviceModel) model).firstInputModel;
        firstIoViewer = iov;
    }

    @Override
    public void setNumberOfIos(short numberOfIos) {
        this.numberOfIos = numberOfIos;
        model.init(numberOfIos, 0);
        createOneInput();
    }

    public void draw(Canvas canvas) {
        super.draw(canvas);
        rotateCanvas(canvas);
        drawRotated(canvas, currentPaint);
        restoreCanvas(canvas);
    }

    @Override
    void load(DataInputStream inputStream) throws java.io.IOException {
        super.load(inputStream);
    }

    @Override
    void save(DataOutputStream outputStream) throws java.io.IOException {
        super.save(outputStream);
    }

    @Override
    public void initPosition(int x, int y, int angleIndex) {
        this.left = x;
        this.bottom = y;
        this.right = x + LED_WIDTH;
        this.top = y - LED_HEIGHT;
        this.angleIndex = angleIndex;
        setEnclosingRect();
        //       this.numberOfIos = numberOfIos;
        //       this.selectedIos = new boolean[numberOfIos];
    }

    @Override
    protected void drawRotated(Canvas canvas, Paint paint) {
        if (!empty) {
            int savedColor = paint.getColor();
            State.Level level = ((DeviceModel) model).firstInputModel.level;
            if (level == State.Level.high) {
                color = LEDViewer.HIGH_COLOR;
            } else if (level == State.Level.low) {
                color = LEDViewer.LOW_COLOR;
            } else {
                color = LEDViewer.UNDEFINED_COLOR;
            }
            paint.setColor(color);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(left + LED_WIDTH / 2, top + LED_HEIGHT / 2, (LED_HEIGHT) / 2, paint);
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(savedColor);
        }
        canvas.drawCircle(left + LED_WIDTH / 2, top + LED_HEIGHT / 2, LED_HEIGHT / 2, paint);
//        drawToolTip(canvas);
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
    float getSelectionZoom(int canvasHeight, int canvasWidth, float scaleFactor) {
        return Math.min(canvasWidth / (8 * (right - left)),
                canvasHeight / (8 * (bottom - top))) / (scaleFactor);
    }

    public int getWidth() {
        return LED_WIDTH;
    }

    public int getHeight() {
        return LED_HEIGHT;
    }

}
