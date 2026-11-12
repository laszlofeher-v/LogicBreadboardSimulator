package com.logicbreadboard.simulator;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 * Created by laci on 10/21/15.
 */
public class ClockLHViewer extends DeviceViewer {
    static protected final int CONTEXT_MENU_MASK = ContextMenuIcons.COPY |
            ContextMenuIcons.DELETE |
            ContextMenuIcons.DELAY |
            ContextMenuIcons.ROTATE |
            ContextMenuIcons.ALIGN_HORIZONTAL | ContextMenuIcons.ALIGN_VERTICAL |
            ContextMenuIcons.LOCK | ContextMenuIcons.UNLOCK |
            ContextMenuIcons.UNSELECT;
    private static final int WIDTH = 32;
    private static final int HEIGHT = 32;

    ClockLHViewer() {
        model = new ClockLHModel();
    }
//    @Override
//    public int getContextMenuMask() {
//        return CONTEXT_MENU_MASK;
//    }

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
        return (short) SchematicViewerFactory.Sources.Clock_LH.ordinal();
    }

    @Override
    public void setDefaultNumberOfIos() {
        setNumberOfIos((short) 1);
    }

    protected void createOneIo() {
        DeviceIOViewer iov = new DeviceIOViewer((short) ((WIDTH) / 2), (short) 0,
                (short) (DeviceIOViewer.LENGTH + (WIDTH) / 2), (short) 0, false);
        iov.model = ((DeviceModel) model).firstOutputModel;
        firstIoViewer = iov;
    }

    @Override
    public void setNumberOfIos(short numberOfIos) {
        this.numberOfIos = numberOfIos;
        model.init(0, numberOfIos);
        createOneIo();
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
        this.right = x + WIDTH;
        this.top = y - HEIGHT;
        this.angleIndex = angleIndex;
        setEnclosingRect();
        //       this.numberOfIos = numberOfIos;
        //       this.selectedIos = new boolean[numberOfIos];
    }

    @Override
    public void rotate(int n) {
        if (n < 0) {
            n = n + Rotate.NUM_OF_ROT;
        }
        angleIndex = (angleIndex + n) % Rotate.NUM_OF_ROT;
        rotateIo();
    }

    @Override
    public DeviceIOViewer getIoCanvasPos(DeviceIOViewer ioViewer) {
//        if (angleIndex == Rotate.R_0) {
        staticIO.startX = (short) ((right + left) / 2 + ioViewer.startX);
        staticIO.endX = (short) ((right + left) / 2 + ioViewer.endX);
        staticIO.startY = (short) ((bottom + top) / 2 + ioViewer.startY);
        staticIO.endY = (short) ((bottom + top) / 2 + ioViewer.endY);
        staticIO.inverted = ioViewer.inverted;
        staticIO.netNumber = ioViewer.netNumber;
        staticIO.locked = ioViewer.locked;
        //       staticIO.maskEnabled=ioViewer.maskEnabled;
        return staticIO;
    }

    @Override
    public DeviceIOViewer findIO(int x, int y) {
        DeviceIOViewer selectedIo = null;
        x = x - Math.round((left + right) / 2.f);
        y = y - Math.round((top + bottom) / 2.f);
        if (selectionZoom != 1) {
            x = Math.round(x / selectionZoom);
            y = Math.round(y / selectionZoom);
        }
        DeviceIOViewer ioViewer = firstIoViewer;
        float distance = Router.MAX_CONNECTION_DISTANCE;
        while (ioViewer != null) {
            float newDistance = Math.abs(x - ioViewer.endX) + Math.abs(y - ioViewer.endY);
//            System.out.println("newDistance=" + newDistance);
            if (newDistance < distance) {
                distance = newDistance;
                selectedIo = ioViewer;
            }
            ioViewer = ioViewer.next;
        }
        return selectedIo;
    }

    protected void rotateIo() {
        int startX = firstIoViewer.startX;
        int startY = firstIoViewer.startY;
        int endX = firstIoViewer.endX;
        int endY = firstIoViewer.endY;
//        Log.i("rotateIo", "angleIndex=" + angleIndex);
        if (angleIndex == 0) {
            startX = WIDTH / 2;
            startY = 0;
            endX = startX + DeviceIOViewer.LENGTH;
            endY = 0;
        } else if (angleIndex == 6) {
            startX = 0;
            startY = -HEIGHT / 2;
            endX = 0;
            endY = startY - DeviceIOViewer.LENGTH;
        } else if (angleIndex == 4) {
            startX = -WIDTH / 2;
            startY = 0;
            endX = startX - DeviceIOViewer.LENGTH;
            endY = 0;
        } else if (angleIndex == 2) {
            startX = 0;
            startY = HEIGHT / 2;
            endX = 0;
            endY = startY + DeviceIOViewer.LENGTH;
        }
        firstIoViewer.startX = (short) startX;
        firstIoViewer.startY = (short) startY;
        firstIoViewer.endX = (short) endX;
        firstIoViewer.endY = (short) endY;
    }

    @Override
    public void draw(Canvas canvas) {
        drawRotated(canvas, currentPaint);
        rotateIo();
        DeviceIOViewer ioViewer = firstIoViewer;
        int xo = Math.round((float) (left + right) / 2f);
        int yo = Math.round((float) (bottom + top) / 2f);
        do {
            ioViewer.draw(canvas, xo, yo, currentPaint);
            ioViewer = ioViewer.next;
        } while (ioViewer != null);
    }

    @Override
    protected void drawRotated(Canvas canvas, Paint paint) {
        int xo = (left + right) / 2;
        int yo = (top + bottom) / 2;
        int r = (right - left) / 2;
        canvas.drawCircle(xo, yo, r, paint);
        int up0 = (left + xo) / 2;
        int up1 = (xo + right) / 2;
        int high = (yo + top) / 2;
        int low = (bottom + yo) / 2;
        canvas.drawLine(up0, low, up0, high, paint);
        canvas.drawLine(up0, high, xo, high, paint);
        canvas.drawLine(xo, high, xo, low, paint);
        canvas.drawLine(xo, low, up1, low, paint);
        canvas.drawLine(up1, low, up1, high, paint);
        float textYpos;
        if (angleIndex == 2) {
            textYpos = top - 2 * paint.getStrokeWidth();
        } else {
            textYpos = bottom + currentTextPaint.getTextSize();
        }
        canvas.drawText(Integer.toString(model.delays[((ClockLHModel) model).PERIOD_TIME_INDEX])
                + " ns", left, textYpos, currentTextPaint);
    }

    public int getWidth() {
        return WIDTH;
    }

    public int getHeight() {
        return HEIGHT;
    }

}
