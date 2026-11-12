package com.logicbreadboard.simulator;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 * Created on 3/13/16.
 */
public class BBResistorViewer extends BBTwoTerminalViewer {

    public BBResistorViewer() {
//        super();
        model = new BBResistorModel();
//        setDefaultNumberOfIos();
//        createOutputs();
    }

    public BBResistorViewer(int x0, int y0, int x1, int y1) {
        super();
        model = new BBResistorModel();
//        setDefaultNumberOfIos();
        startX = x0;
        startY = y0;
        endX = x1;
        endY = y1;
//        createOutputs();
        calc3d();
    }

    protected void createOutputs() {
        InvisibleDeviceIOViewer iov = new InvisibleDeviceIOViewer((short) (startX - left), (short) (startY - top),
                (short) (short) (startX - left), (short) (startY - top), false);
        iov.model = ((DeviceModel) model).firstOutputModel;
        iov.model.impedance = State.Impedance.high;
        firstIoViewer = iov;
        iov = new InvisibleDeviceIOViewer((short) (endX - right), (short) (endY - bottom),
                (short) (endX - right), (short) (endY - bottom), false);
        iov.model = ((DeviceModel) model).firstOutputModel.next;
        iov.model.impedance = State.Impedance.high;
        firstIoViewer.next = iov;
        Log.i("create:", left + " " + right + " " + top + " " + bottom + " " + firstIoViewer.startX + " " + firstIoViewer.startY + " " + firstIoViewer.next.startX + " " + firstIoViewer.next.startY);
    }

    @Override
    public void setNumberOfIos(short numberOfIos) {
        this.numberOfIos = numberOfIos;
        model.init(numberOfIos, numberOfIos);
        createOutputs();
    }

    @Override
    public void setDefaultNumberOfIos() {
        setNumberOfIos((short) 2);
    }

    @Override
    public short getGroup() {
        return (short) BBViewerFactory.BBDeviceGroup.BBTwoTerminals.ordinal();
    }

    @Override
    public short getSymbol() {
        return (short) BBViewerFactory.BBTwoTerminals.Resistor.ordinal();
    }

    @Override
    public void draw(Canvas canvas) {
        Paint paint = Viewer.Context.bbWirePaint;
        float dox = (ox - BreadBoardViewer.xOrigo + BreadBoardViewer.staticXOffset) * BreadBoardViewer.staticScaleFactor + BreadBoardViewer.xOrigo;
        float doy = (oy - BreadBoardViewer.yOrigo + BreadBoardViewer.staticYOffset) * BreadBoardViewer.staticScaleFactor + BreadBoardViewer.yOrigo;
        final Path path = new Path();
        float xs = startX - endX;
        float ys = startY - endY;
        float s = (float) Math.sqrt(xs * xs + ys * ys);
        path.moveTo(startX, startY);
        final float x2 = ox + ((dox - BreadBoardViewer.xOrigo)) / z;
        final float y2 = oy + ((doy - BreadBoardViewer.yOrigo)) / z;

        final float x3 = ox + 40 * (startX - endX) / s + ((dox - BreadBoardViewer.xOrigo)) / z;
        final float y3 = oy + 40 * (startY - endY) / s + ((doy - BreadBoardViewer.yOrigo)) / z;
        path.quadTo(x2, y2, endX, endY);
//        canvas.drawPath(path, paint);
        float w = paint.getStrokeWidth();
        paint.setStrokeWidth(20);
        canvas.drawLine(x2, y2, x3, y3, paint);
//        canvas.drawCircle(x2, y2, 5, paint);
//        canvas.drawCircle(x3, y3, 5, paint);
        paint.setStrokeWidth(w);
        canvas.drawLine(startX, startY, x3, y3, paint);
        canvas.drawLine(endX, endY, x2, y2, paint);
        if (netNumberShowEnabled) {
            canvas.drawText(Integer.toString(firstIoViewer.netNumber), startX, startY, Context.netNumberPaint);
            canvas.drawText(Integer.toString(firstIoViewer.next.netNumber), endX, endY, Context.netNumberPaint);
        }
    }

    @Override
    void load(DataInputStream inputStream) throws java.io.IOException {
        super.load(inputStream);
        startX = inputStream.readInt();
        startY = inputStream.readInt();
        endX = inputStream.readInt();
        endY = inputStream.readInt();
        firstIoViewer.endX = firstIoViewer.startX = (short) (startX - left);
        firstIoViewer.endY = firstIoViewer.startY = (short) (startY - top);
        firstIoViewer.next.endX = firstIoViewer.next.startX = (short) (endX - right);
        firstIoViewer.next.endY = firstIoViewer.next.startY = (short) (endY - bottom);
        Log.i("Load:", left + " " + right + " " + top + " " + bottom + " " + firstIoViewer.startX + " " + firstIoViewer.startY + " " + firstIoViewer.next.startX + " " + firstIoViewer.next.startY);
        calc3d();
    }

    @Override
    void save(DataOutputStream outputStream) throws java.io.IOException {
        super.save(outputStream);
        outputStream.writeInt(startX);
        outputStream.writeInt(startY);
        outputStream.writeInt(endX);
        outputStream.writeInt(endY);
    }

}
