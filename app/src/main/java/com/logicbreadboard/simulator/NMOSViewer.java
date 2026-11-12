package com.logicbreadboard.simulator;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by laci on 10/10/15.
 */
public class NMOSViewer extends DeviceViewer {
    protected static final short TRANSISTOR_HEIGHT = 24;
    protected static final short TRANSISTOR_WIDTH = 4;
    protected static final short TRANSISTOR_HEIGHT_HALF = TRANSISTOR_HEIGHT / 2;
    protected static final short TRANSISTOR_WIDTH_HALF = TRANSISTOR_WIDTH / 2;

    public NMOSViewer() {
        model = new NMOSModel();
    }

    @Override
    public short getGroup() {
        return (short) SchematicViewerFactory.DeviceGroup.Transistors.ordinal();
    }

    @Override
    public short getSymbol() {
        return (short) SchematicViewerFactory.Transistors.NMOS.ordinal();
    }

    @Override
    public void setDefaultNumberOfIos() {
        setNumberOfIos((short) 3);
    }

    @Override
    public void initPosition(int x, int y, int angleIndex) {
        this.left = x;
        this.bottom = y;
        this.right = x + TRANSISTOR_WIDTH;
        this.top = y - TRANSISTOR_HEIGHT;
        this.angleIndex = angleIndex;
        setEnclosingRect();
    }

    @Override
    public void setNumberOfIos(short numberOfIos) {
        this.numberOfIos = numberOfIos;
        model.init(numberOfIos - 2, 2);
        createInput();
        createOutputs();
    }

    private void createOutputs() {
        DeviceIOModel ioModel = ((DeviceModel) model).firstOutputModel;
        DeviceIOViewer ioViewer = new DeviceIOViewer(TRANSISTOR_WIDTH_HALF, (short)
                TRANSISTOR_HEIGHT_HALF,
                (short) (TRANSISTOR_WIDTH_HALF + DeviceIOViewer.LENGTH), (short) TRANSISTOR_HEIGHT_HALF, false);
        ioViewer.model = ioModel;
        ioModel = ioModel.next;
        ioViewer.next = firstIoViewer;
        firstIoViewer = ioViewer;
        ioViewer = new DeviceIOViewer(TRANSISTOR_WIDTH_HALF, (short)
                -TRANSISTOR_HEIGHT_HALF,
                (short) (TRANSISTOR_WIDTH_HALF + DeviceIOViewer.LENGTH), (short)
                -TRANSISTOR_HEIGHT_HALF, false);
        ioViewer.model = ioModel;
        ioViewer.next = firstIoViewer;
        firstIoViewer = ioViewer;
    }

    private void createInput() {
        DeviceIOModel ioModel = ((DeviceModel) model).firstInputModel;
        DeviceIOViewer iov = new DeviceIOViewer((short) (-TRANSISTOR_WIDTH_HALF), (short) 0,
                (short) (-DeviceIOViewer.LENGTH - (TRANSISTOR_WIDTH_HALF)), (short) 0, false);
        iov.model = ioModel;
        firstIoViewer = iov;
    }

    @Override
    protected void drawRotated(Canvas canvas, Paint paint) {
        canvas.drawLine(left, bottom, left, top, paint);
        canvas.drawLine(right, bottom, right, top, paint);
    }

    @Override
    public void delete() {
        model.delete();
    }

    public int getWidth() {
        return TRANSISTOR_WIDTH;
    }

    public int getHeight() {
        return TRANSISTOR_HEIGHT;
    }

}
