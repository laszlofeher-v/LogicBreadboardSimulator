package com.logicbreadboard.simulator;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 * Created on 4/29/16.
 */
public class BBClockViewer extends ClockLHViewer {
    static protected final int CONTEXT_MENU_MASK = ContextMenuIcons.DELAY | ContextMenuIcons.L_H |
            ContextMenuIcons.H_L | ContextMenuIcons.DISABLE | ContextMenuIcons.UNSELECT;
    String name;

    BBClockViewer() {
        model = new BBClockModel();
        ((BBClockModel) model).state = BBClockModel.DISABLED;
    }

    BBClockViewer(String name) {
        model = new BBClockModel();
        this.name = name;
        ((BBClockModel) model).state = BBClockModel.DISABLED;
    }

    @Override
    public boolean isName(String s) {
        return s.equals(name);
    }

    @Override
    public boolean isMovable() {
        return false;
    }

    @Override
    public int getContextMenuMask() {
        return CONTEXT_MENU_MASK;
    }

    void move(EditorViewer editorViewer, int dx, int dy, boolean checkInside) {
        Log.i("BBCLOCK", "bad try to move");
        // it is not movable
    }

    @Override
    void save(DataOutputStream outputStream) throws java.io.IOException {
        super.save(outputStream);
        outputStream.writeUTF(name);
        outputStream.writeInt(((BBClockModel) model).state);
    }

    @Override
    void load(DataInputStream inputStream) throws java.io.IOException {
        int left = inputStream.readInt();
        int bottom = inputStream.readInt();
        Log.i("DEVLOAD", "left=" + left + " bottom=" + bottom);
        byte angleIndex = inputStream.readByte();
        numberOfIos = inputStream.readShort();
        initPosition(left, bottom, angleIndex);
        setNumberOfIos(numberOfIos);
        loadIos(inputStream, firstIoViewer);
        model.readDelays(inputStream);
        name = inputStream.readUTF();
        ((BBClockModel) model).state = inputStream.readInt();
    }

    @Override
    public void draw(Canvas canvas) {
        drawRotated(canvas, currentPaint);
//        rotateIo();
//        DeviceIOViewer ioViewer = firstIoViewer;
//        int xo = Math.round((float) (left + right) / 2f);
//        int yo = Math.round((float) (bottom + top) / 2f);
//        do {
//            ioViewer.draw(canvas, xo, yo, currentPaint);
//            ioViewer = ioViewer.next;
//        } while (ioViewer != null);
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
        if ((((BBClockModel) model).state & BBClockModel.DISABLED) != 0) {

        } else if ((((BBClockModel) model).state & BBClockModel.L_TO_H) != 0) {
            canvas.drawLine(up0, low, up0, high, paint);
            canvas.drawLine(up0, high, xo, high, paint);
            canvas.drawLine(xo, high, xo, low, paint);
            canvas.drawLine(xo, low, up1, low, paint);
            canvas.drawLine(up1, low, up1, high, paint);
            writeCycleTime(canvas, paint);
        } else if ((((BBClockModel) model).state & BBClockModel.H_TO_L) != 0) {
            canvas.drawLine(up0, low, up0, high, paint);
            canvas.drawLine(up0, low, xo, low, paint);
            canvas.drawLine(xo, high, xo, low, paint);
            canvas.drawLine(xo, high, up1, high, paint);
            canvas.drawLine(up1, low, up1, high, paint);
            writeCycleTime(canvas, paint);
        }
    }

    @Override
    public void initPosition(int x, int y, int angleIndex) {
        this.left = x;
        this.bottom = y;
        this.right = x + getWidth();
        this.top = y - getHeight();
        this.angleIndex = angleIndex;
        setEnclosingRectWithoutCheck();
        //       this.numberOfIos = numberOfIos;
        //       this.selectedIos = new boolean[numberOfIos];
    }

    private void writeCycleTime(Canvas canvas, Paint paint) {
        float textYpos;
        textYpos = top + getHeight() / 2;
        canvas.drawText(Integer.toString(model.delays[((BBClockModel) model).PERIOD_TIME_INDEX])
                + " ns", left - 2 * getWidth(), textYpos, currentTextPaint);
    }

    @Override
    public short getGroup() {
        return (short) BBViewerFactory.BBDeviceGroup.BBLabDevices.ordinal();
    }

    @Override
    public short getSymbol() {
        return (short) BBViewerFactory.BBLabDevices.BBClockViewer.ordinal();
    }

    public void setLToH() {
        ((BBClockModel) model).state = BBClockModel.L_TO_H;
    }

    public void setHToL() {
        ((BBClockModel) model).state = BBClockModel.H_TO_L;
    }

    public void setDisabled() {
        ((BBClockModel) model).state = BBClockModel.DISABLED;
    }
}
