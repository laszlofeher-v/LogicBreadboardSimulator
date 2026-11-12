package com.logicbreadboard.simulator;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 * Created on 4/11/16.
 */
public class InvisibleGndViewer extends InvisibleDeviceViewer {
    InvisibleGndViewer() {
        super();
        model = new LowModel();
    }

    @Override
    float getSelectionZoom(int canvasHeight, int canvasWidth, float scaleFactor) {
        return 1;
    }

    @Override
    public short getGroup() {
        return (short) SchematicViewerFactory.DeviceGroup.Sources.ordinal();
    }

    @Override
    public short getSymbol() {
        return (short) SchematicViewerFactory.Sources.Level_High.ordinal();
    }

    @Override
    public void setDefaultNumberOfIos() {
        setNumberOfIos((short) 1);
    }

    protected void createOneIo() {
        InvisibleDeviceIOViewer iov = new InvisibleDeviceIOViewer((short) 0, (short) (1 / 2),
                (short) 0, (short) (1 / 2 + DeviceIOViewer.LENGTH), false);
        iov.model = ((DeviceModel) model).firstOutputModel;
        firstIoViewer = iov;
    }

    @Override
    public void setNumberOfIos(short numberOfIos) {
        this.numberOfIos = numberOfIos;
        model.init(0, numberOfIos);
        createOneIo();
    }

    public void draw(Canvas canvas) {
    }

    @Override
    void load(DataInputStream inputStream) throws java.io.IOException {
//        super.load(inputStream);
    }

    @Override
    void save(DataOutputStream outputStream) throws java.io.IOException {
        //       super.save(outputStream);
    }

    @Override
    public void initPosition(int x, int y, int angleIndex) {
        this.left = x;
        this.bottom = y;
        this.right = x + 1;
        this.top = y - 1;
        this.angleIndex = angleIndex;
        setEnclosingRect();
        //       this.numberOfIos = numberOfIos;
        //       this.selectedIos = new boolean[numberOfIos];
    }

    @Override
    public void drawEnlarged(Canvas canvas, float zoom) {
    }

    @Override
    protected void drawRotated(Canvas canvas, Paint paint) {
    }

    public int getWidth() {
        return 1;
    }

    public int getHeight() {
        return 1;
    }


}
