package com.logicbreadboard.simulator;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 * Created on 6/22/16.
 */
public class PowerConnectorViewer extends DeviceViewer {
    static final int LED_WIDTH = 32;
    static final int LED_HEIGHT = 32;
    static private final int CONTEXT_MENU_MASK =
            ContextMenuIcons.COPY |
                    ContextMenuIcons.DELETE |
                    ContextMenuIcons.ROTATE |
                    ContextMenuIcons.ALIGN_HORIZONTAL | ContextMenuIcons.ALIGN_VERTICAL |
                    ContextMenuIcons.LOCK | ContextMenuIcons.UNLOCK |
                    ContextMenuIcons.UNSELECT;

    PowerConnectorViewer() {
        model = new PowerConnectorModel();
    }

    @Override
    public int getContextMenuMask() {
        return CONTEXT_MENU_MASK;
    }

    @Override
    public short getGroup() {
        return (short) SchematicViewerFactory.DeviceGroup.Connections.ordinal();
    }

    @Override
    public short getSymbol() {
        return (short) SchematicViewerFactory.Connections.PowerConnector.ordinal();
    }

    @Override
    public void setDefaultNumberOfIos() {
        setNumberOfIos((short) 2);
    }

    protected void createInputs() {
        DeviceIOViewer iov = new DeviceIOViewer((short) (-(LED_WIDTH) / 2), (short) 0,
                (short) (-DeviceIOViewer.LENGTH - (LED_WIDTH) / 2), (short) 0, false);
        iov.model = ((DeviceModel) model).firstInputModel;
        firstIoViewer = iov;
        iov = new DeviceIOViewer((short) ((LED_WIDTH) / 2), (short) 0,
                (short) (DeviceIOViewer.LENGTH + (LED_WIDTH) / 2), (short) 0, false);
        iov.model = ((DeviceModel) model).firstInputModel.next;
        firstIoViewer.next = iov;
    }

    @Override
    public void setNumberOfIos(short numberOfIos) {
        this.numberOfIos = numberOfIos;
        model.init(numberOfIos, 0);
        createInputs();
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
        canvas.drawRect(left, top, right, bottom, paint);
        float size = Context.textPaint.getTextSize();
        Context.textPaint.setTextSize(getHeight() / 3);
        canvas.drawText("+", left - size + STROKE_WITH, top + size, Context.textPaint);
        Context.textPaint.setTextSize(size);
    }

    public void drawEnlarged(Canvas canvas, float zoom) {
        selectionZoom = zoom;
        canvas.save();
        canvas.scale(zoom, zoom, left + (right - left) / 2, top + (bottom - top) / 2);
        draw(canvas);
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
