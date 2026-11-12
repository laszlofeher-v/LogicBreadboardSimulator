package com.logicbreadboard.simulator;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 * Created on 8/12/15.
 */
public class LowViewer extends DeviceViewer {
    private static final int WIDTH = 8;
    private static final int HEIGHT = 8;
    int color;

    LowViewer() {
        model = new LowModel();

    }
    @Override
    float getSelectionZoom(int canvasHeight, int canvasWidth, float scaleFactor) {
        return Math.min(canvasWidth / (8 * (right - left)),
                canvasHeight / (8 * (bottom - top))) / (scaleFactor);
    }

    @Override
    public short getGroup() {
        return (short) SchematicViewerFactory.DeviceGroup.Sources.ordinal();
    }

    @Override
    public short getSymbol() {
        return (short) SchematicViewerFactory.Sources.Level_Low.ordinal();
    }

    @Override
    public void setDefaultNumberOfIos() {
        setNumberOfIos((short) 1);
    }

    protected void createOneIo() {
        int x = (right + left) / 2;
//        DeviceIOViewer iov = new DeviceIOViewer((short) 0, (short) (-HEIGHT / 2),
//                (short) (DeviceIOViewer.LENGTH + WIDTH / 2), (short) (-HEIGHT / 2), false);
        DeviceIOViewer iov = new DeviceIOViewer((short) 0, (short) (-HEIGHT / 2),
                (short) 0, (short) (-HEIGHT / 2 - DeviceIOViewer.LENGTH), false);
        iov.model = ((DeviceModel) model).firstOutputModel;
        firstIoViewer = iov;
    }

    @Override
    public void setNumberOfIos(short numberOfIos) {
        this.numberOfIos = numberOfIos;
        model.init(0, 1);
        createOneIo();
    }

    @Override
    public void drawEnlarged(Canvas canvas, float zoom) {
        super.drawEnlarged(canvas,zoom);
        super.draw(canvas);
        rotateCanvas(canvas);
        drawRotated(canvas, Context.selectedPaint);
        restoreCanvas(canvas);
    }

   @Override
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
        this.right = x + WIDTH;
        this.top = y - HEIGHT;
        this.angleIndex = angleIndex;
        setEnclosingRect();
        //       this.numberOfIos = numberOfIos;
        //       this.selectedIos = new boolean[numberOfIos];
    }

    @Override
    protected void drawRotated(Canvas canvas, Paint paint) {
        int x = (right + left) / 2;
        canvas.drawLine(x, bottom, x, top, paint);
        canvas.drawLine(left, bottom, right, bottom, paint);
    }

    public int getWidth() {
        return WIDTH;
    }

    public int getHeight() {
        return HEIGHT;
    }
}
