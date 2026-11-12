package com.logicbreadboard.simulator;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 * Created on 5/9/16.
 */
public class BBSwViewer extends _74_DIPViewer {
    protected final int BOX_WIDTH = 7 * unit;
    protected final int BOX_HEIGHT = 3 * unit;

    @Override
    public short getGroup() {
        return (short) BBViewerFactory.factory.getGroupDIPSw();
    }

    public int getWidth() {
        return BOX_WIDTH;
    }

    public int getHeight() {
        return BOX_HEIGHT;
    }

    @Override
    public void setDefaultNumberOfIos() {
        setNumberOfIos((short) 14);
    }

    @Override
    void save(DataOutputStream outputStream) throws java.io.IOException {
        super.save(outputStream);
    }

    @Override
    void load(DataInputStream inputStream) throws java.io.IOException {
        super.load(inputStream);
    }

    BBSwViewer() {
    }

    @Override
    protected void drawRotated(Canvas canvas, Paint paint) {
        float w = 0;
        if (floating) {
            w = paint.getStrokeWidth();
            paint.setStrokeWidth(4 * w);
//            Log.i("strokewidth1", "w=" + w);
        }
        canvas.drawLine(left, bottom, left, top, paint);
        canvas.drawLine(right, bottom, right, top, paint);
        canvas.drawLine(left, bottom, right, bottom, paint);
        canvas.drawLine(left, top, right, top, paint);
        int d = unit / 2;
        parameterRectF.left = left - d;
        parameterRectF.bottom = (bottom + top) / 2 + d;
        parameterRectF.right = left + d;
        parameterRectF.top = (bottom + top) / 2 - d;
        canvas.drawArc(parameterRectF, 270, 180, false, paint);
        if (floating) {
            paint.setStrokeWidth(w);
        }
    }

    protected DeviceIOViewer createOneIO(DeviceIOViewer prev, int iPos, short yPos, int index) {
        short xPos;
        DeviceIOViewer iov;
        if (yPos < 0) {
            xPos = (short) (getWidth() / 2 - iPos * unit - unit / 2);
            iov = new DIPIoViewer(xPos, yPos,
                    xPos, (short) (yPos - DeviceIOViewer.LENGTH), false, index);
        } else {
            xPos = (short) (-getWidth() / 2 + iPos * unit + unit / 2);
            iov = new DIPIoViewer(xPos, yPos,
                    xPos, (short) (yPos + DeviceIOViewer.LENGTH), false, index);
        }
        if (prev != null) {
            prev.next = iov;
        } else {
            firstIoViewer = iov;
        }
        return iov;
    }

    @Override
    protected void createAllInputs() {
        DeviceIOViewer iov = firstIoViewer;
        DeviceOutputModel outputModel = ((DeviceModel) model).firstOutputModel;
        for (short i = 0; i < numberOfIos / 2; i++) {
            iov = createOneIO(iov, i, (short) (getHeight() / 2), i + 1);
            iov.model = outputModel;
            outputModel = (DeviceOutputModel) outputModel.next;
        }
        for (short i = 0; i < numberOfIos / 2; i++) {
            iov = createOneIO(iov, i, (short) -(getHeight() / 2), i + 1);
            iov.model = outputModel;
            outputModel = (DeviceOutputModel) outputModel.next;
        }
    }
}