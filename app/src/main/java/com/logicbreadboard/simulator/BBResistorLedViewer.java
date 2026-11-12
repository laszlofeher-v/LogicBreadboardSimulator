package com.logicbreadboard.simulator;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 * Created on 3/13/16.
 */
public class BBResistorLedViewer extends BBLedViewer {

    public BBResistorLedViewer() {
        super();
        model = new BBResistorLEDModel();
//        model.init(2, 0);
//        createInputs();
//        setDefaultNumberOfIos();
        color = Context.bbWirePaint.getColor();
    }
//    @Override
//    public int getContextMenuMask() {
//        return CONTEXT_MENU_MASK;
//    }

    public BBResistorLedViewer(int x0, int y0, int x1, int y1) {
        super();
        color = Context.bbWirePaint.getColor();
        startX = x0;
        startY = y0;
        endX = x1;
        endY = y1;
        model=new BBResistorLEDModel();
//        model.init(2, 0);
//        createInputs();
        calc3d();
    }

    @Override
    public short getGroup() {
        return (short) BBViewerFactory.BBDeviceGroup.BBTwoTerminals.ordinal();
    }

    @Override
    public short getSymbol() {
        return (short) BBViewerFactory.BBTwoTerminals.ResistorLed.ordinal();
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
        final float x4 = ox + 80 * (startX - endX) / s + ((dox - BreadBoardViewer.xOrigo)) / z;
        final float y4 = oy + 80 * (startY - endY) / s + ((doy - BreadBoardViewer.yOrigo)) / z;
        path.quadTo(x2, y2, endX, endY);
//        canvas.drawPath(path, paint);
//        float w = paint.getStrokeWidth();
//        paint.setStrokeWidth(20);
        float w = paint.getStrokeWidth();
//        canvas.drawLine((x2 + x3) / 2, (y2 + y3) / 2, (x4 + x3) / 2, (y4 + y3) / 2, paint);
        final float xm = (x2 + x3) / 2;
        final float ym = (y2 + y3) / 2;
        drawDiode(canvas, paint, x2, y2, x3, y3, xm, ym);
        paint.setStrokeWidth(20);
        canvas.drawLine(x4, y4, x3, y3, paint);
        paint.setStrokeWidth(w);
        canvas.drawLine(xm, ym, x3, y3, paint);

        canvas.drawLine(endX, endY, x2, y2, paint);
        canvas.drawLine(startX, startY, x4, y4, paint);
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
        color = inputStream.readInt();
        calc3d();
    }

    @Override
    void save(DataOutputStream outputStream) throws java.io.IOException {
        super.save(outputStream);
        outputStream.writeInt(startX);
        outputStream.writeInt(startY);
        outputStream.writeInt(endX);
        outputStream.writeInt(endY);
        outputStream.writeInt(color);
    }

}
