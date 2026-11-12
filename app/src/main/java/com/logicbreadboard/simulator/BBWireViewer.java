package com.logicbreadboard.simulator;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 * Created on 2/3/16.
 */
public class BBWireViewer extends Viewer {
    int startX;
    int startY;
    int endX;
    int endY;
    int netNumber;
    float ox;
    float oy;
    float z;
    int color;
    static private final int CONTEXT_MENU_MASK =
//            ContextMenuIcons.LOCK | ContextMenuIcons.UNLOCK | must not enable java.util.ConcurrentModificationException
            ContextMenuIcons.RESISTOR |
                    ContextMenuIcons.RLED |
                    ContextMenuIcons.DELETE |
                    ContextMenuIcons.LED |
                    ContextMenuIcons.UNSELECT;

    public BBWireViewer() {
        super();
        netNumber = NetNumbers.get();
        color = Context.bbWirePaint.getColor();
    }

    @Override
    public void setHolesFree(EditorViewer editorViewer) {
        editorViewer.setHoleFree(startX, startY);
        editorViewer.setHoleFree(endX, endY);
        editorViewer.propagateNetNumberAt(startX, startY, NetNumbers.get());
        editorViewer.propagateNetNumberAt(endX, endY, NetNumbers.get());
    }

    private void calc3d() {
        ox = (startX + endX) / 2;
        oy = (startY + endY) / 2;
        float dx = startX - endX;
        float dy = startY - endY;
        float s = (float) Math.sqrt(dx * dx + dy * dy);
        z = 2 + 256 / s;
    }

    public BBWireViewer(int x0, int y0, int x1, int y1) {
        super();
        color = Context.bbWirePaint.getColor();
        netNumber = NetNumbers.get();
        startX = x0;
        startY = y0;
        endX = x1;
        endY = y1;
        calc3d();
    }

    public Viewer deleteAt(int x, int y, BreadBoardViewer breadBoardViewer) {
        if ((startX == x && startY == y) || (endX == x && endY == y)) {
            breadBoardViewer.setHoleFree(startX, startY);
            breadBoardViewer.setHoleFree(endX, endY);
            breadBoardViewer.propagateNetNumberAt(startX, startY, NetNumbers.get());
            breadBoardViewer.propagateNetNumberAt(endX, endY, NetNumbers.get());
            return this;
        }
        return null;
    }

    @Override
    public void delete(EditorViewer editorViewer) {
        editorViewer.setHoleFree(startX, startY);
        editorViewer.setHoleFree(endX, endY);
        editorViewer.propagateNetNumberAt(startX, startY, NetNumbers.get());
        editorViewer.propagateNetNumberAt(endX, endY, NetNumbers.get());
    }

    public Viewer selectAt(int x, int y, BreadBoardViewer breadBoardViewer) {
        if ((startX == x && startY == y) || (endX == x && endY == y)) {
            return this;
        }
        return null;
    }

    public void delete(BreadBoardViewer breadBoardViewer) {
        breadBoardViewer.setHoleFree(startX, startY);
        breadBoardViewer.setHoleFree(endX, endY);
        breadBoardViewer.propagateNetNumberAt(startX, startY, NetNumbers.get());
        breadBoardViewer.propagateNetNumberAt(endX, endY, NetNumbers.get());
    }

    @Override
    public void setNetNumber(int netNumber) {
        this.netNumber = netNumber;
    }

    @Override
    public boolean setHolesUsed(EditorViewer editorViewer) {
        if (!editorViewer.setHoleUsed(startX, startY, this) || !editorViewer.setHoleUsed(endX, endY, this)) {
            return false;
        }
        return true;
    }

    @Override
    public void propagateNetNumber(BreadBoardViewer breadBoardViewer, int netNumber) {
        if (this.netNumber != netNumber) {
            this.netNumber = netNumber;
            breadBoardViewer.propagateNetNumberAt(startX, startY, netNumber);
            breadBoardViewer.propagateNetNumberAt(endX, endY, netNumber);
        }
    }

    @Override
    public int getContextMenuMask() {
        return CONTEXT_MENU_MASK;
    }

    public int getNetNumber() {
        return netNumber;
    }

    public boolean isRoutable() {
        return false;
    }

    @Override
    public short getGroup() {
        return (short) BBViewerFactory.BBDeviceGroup.BBWires.ordinal();
    }

    @Override
    public short getSymbol() {
        return (short) BBViewerFactory.BBWires.FreeWire.ordinal();
    }

    @Override
    public void draw(Canvas canvas) {
        Paint paint = Viewer.Context.bbWirePaint;
//        if (EditorModel.simulationIsRunning && Settings.displayWireLevel) {
//            paint = Viewer.Context.bbWirePaint;
//        } else {
//            paint = Viewer.Context.bbWirePaint;
//        }
//
//        paint.setColor(color);
        //       canvas.drawLine(startX, startY, endX, endY, paint);
//        Paint paint = new Paint() {
//            {
//                setStyle(Paint.Style.STROKE);
//                setStrokeCap(Paint.Cap.ROUND);
//                setStrokeWidth(3.0f);
//                setAntiAlias(true);
//                setColor(Color.GREEN);
//            }
//        };
//        paint.setARGB(160, netNumber * 128, netNumber * 64, netNumber * 16);
        float dox = (ox - BreadBoardViewer.xOrigo + BreadBoardViewer.staticXOffset) * BreadBoardViewer.staticScaleFactor + BreadBoardViewer.xOrigo;
        float doy = (oy - BreadBoardViewer.yOrigo + BreadBoardViewer.staticYOffset) * BreadBoardViewer.staticScaleFactor + BreadBoardViewer.yOrigo;
//        Log.i("WIRE", BreadBoardViewer.staticXOffset + " " + BreadBoardViewer.staticYOffset + " " + startX + " " + startY + " " + ox + " " + oy + " " + endX +
//                " " + endY + "      " + dox + " " + doy);
        final Path path = new Path();
        float xs = startX - endX;
        float ys = startY - endY;
        float s = (float) Math.sqrt(xs * xs + ys * ys);
        path.moveTo(startX, startY);
//        final float x2 = ox + ((dox - BreadBoardViewer.xOrigo) / BreadBoardViewer.staticScaleFactor) / z;
//        final float y2 = oy + ((doy - BreadBoardViewer.yOrigo) / BreadBoardViewer.staticScaleFactor) / z;
        final float x2 = ox + ((dox - BreadBoardViewer.xOrigo)) / z;
        final float y2 = oy + ((doy - BreadBoardViewer.yOrigo)) / z;
        path.quadTo(x2, y2, endX, endY);
//        Log.i("WIRE", BreadBoardViewer.staticXOffset + " " + BreadBoardViewer.staticYOffset + " " + startX + " " + startY + " " + x2 + " " + y2 + " " + endX + " " +
//                endY);
        canvas.drawPath(path, paint);
        if (netNumberShowEnabled) {
            canvas.drawText(Integer.toString(netNumber), startX, startY, Context.netNumberPaint);
            canvas.drawText(Integer.toString(netNumber), endX, endY, Context.netNumberPaint);
        }
    }

    @Override
    void load(DataInputStream inputStream) throws java.io.IOException {
//        netNumber = inputStream.readInt();
        startX = inputStream.readInt();
        startY = inputStream.readInt();
        endX = inputStream.readInt();
        endY = inputStream.readInt();
        color = inputStream.readInt();
        calc3d();
    }

    @Override
    void save(DataOutputStream outputStream) throws java.io.IOException {
        outputStream.writeShort(getGroup());
        outputStream.writeShort(getSymbol());
//        outputStream.writeInt(netNumber); Do not save netnumber. Every wire has unique netnumber.
        // After net propagation
        outputStream.writeInt(startX);
        outputStream.writeInt(startY);
        outputStream.writeInt(endX);
        outputStream.writeInt(endY);
        outputStream.writeInt(color);
    }

}
