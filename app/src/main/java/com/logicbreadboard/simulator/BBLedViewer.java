package com.logicbreadboard.simulator;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 * Created on 3/13/16.
 */
public class BBLedViewer extends BBTwoTerminalViewer {
    public BBLedViewer() {
        super();
        model = new BBLEDModel();
        model.init(2, 0);
//        createInputs();
        color = Context.bbWirePaint.getColor();
//        setDefaultNumberOfIos();
    }
//    @Override
//    public int getContextMenuMask() {
//        return CONTEXT_MENU_MASK;
//    }

    public BBLedViewer(int x0, int y0, int x1, int y1) {
        super();
        model = new BBLEDModel();
        model.init(2, 0);
//        createInputs();
        color = Context.bbWirePaint.getColor();
        startX = x0;
        startY = y0;
        endX = x1;
        endY = y1;
        calc3d();
    }

    @Override
    public short getGroup() {
        return (short) BBViewerFactory.BBDeviceGroup.BBTwoTerminals.ordinal();
    }

    @Override
    public short getSymbol() {
        return (short) BBViewerFactory.BBTwoTerminals.Led.ordinal();
    }

    public static float calcRotationAngleInDegrees(float centerX, float centerY, float targetX, float targetY) {
        Double theta = Math.atan2(targetY - centerY, targetX - centerX);
        //       theta += Math.PI / 2.0;
        double angle = Math.toDegrees(theta);
//        if (angle < 0) {
//            angle += 360;
//        }
        return (float) angle;
    }

    @Override
    public void powerOff() {
        ((BBLEDModel) model).state = BBLEDModel.LedState.OFF;
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
        final float xm = (x2 + x3) / 2;
        final float ym = (y2 + y3) / 2;
        drawDiode(canvas, paint, x2, y2, x3, y3, xm, ym);
        canvas.drawLine(endX, endY, x2, y2, paint);
        canvas.drawLine(startX, startY, xm, ym, paint);
    }

    public void drawDiode(Canvas canvas, Paint paint, float x2, float y2, float x3, float y3, float xm, float ym) {
        canvas.save();
        float ax = -20 + xm;
        float ay = ym;
        float bx = +20 + xm;
        float by = ym;
        canvas.rotate(calcRotationAngleInDegrees(xm, ym, x3, y3) + 90, xm, ym);
        canvas.drawLine(xm, ym, ax, ay + 20, paint);
        canvas.drawLine(xm, ym, bx, by + 20, paint);
        canvas.drawLine(ax, ay + 20, bx, by + 20, paint);
        canvas.drawLine(ax, ay, bx, by, paint);
        canvas.restore();
        if (((BBLEDModel) model).state == BBLEDModel.LedState.ON) {
            final float cX = (x3 + 3 * x2) / 4;
            final float cY = (y3 + 3 * y2) / 4;
            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(cX, cY, 22, paint);
            paint.setStyle(Paint.Style.STROKE);
        } else if (((BBLEDModel) model).state == BBLEDModel.LedState.BURNING) {
            final float cX = (x3 + 3 * x2) / 4;
            final float cY = (y3 + 3 * y2) / 4;
            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(cX, cY, 44, paint);
            paint.setStyle(Paint.Style.STROKE);
        } else if (((BBLEDModel) model).state == BBLEDModel.LedState.UNDEFINED) {
            final float cX = (x3 + 3 * x2) / 4;
            final float cY = (y3 + 3 * y2) / 4;
            canvas.drawCircle(cX, cY, 22, paint);
        }
    }

    @Override
    void load(DataInputStream inputStream) throws java.io.IOException {
        super.load(inputStream);
        startX = inputStream.readInt();
        startY = inputStream.readInt();
        endX = inputStream.readInt();
        endY = inputStream.readInt();
        color = inputStream.readInt();
        firstIoViewer.endX = firstIoViewer.startX = (short) (startX - left);
        firstIoViewer.endY = firstIoViewer.startY = (short) (startY - top);
        firstIoViewer.next.endX = firstIoViewer.next.startX = (short) (endX - right);
        firstIoViewer.next.endY = firstIoViewer.next.startY = (short) (endY - bottom);
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
