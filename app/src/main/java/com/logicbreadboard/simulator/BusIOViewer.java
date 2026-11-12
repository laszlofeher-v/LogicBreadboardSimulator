package com.logicbreadboard.simulator;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by laci on 9/21/15.
 */
public class BusIOViewer extends DeviceIOViewer {
//    static final short LENGTH = 100;
//    static final short CLEARANCE = LENGTH;
//    protected int angleIndex;
//    DeviceIOViewer next;
//    DeviceIOModel model;
//    short startX; // relative to device
//    short startY;
//    short endX;
//    short endY;
//    boolean inverted = false;
//    boolean selected = false;
//    boolean locked = false;
//    int netNumber;

    //    Viewer connection;

    BusIOViewer(short x0, short y0, short x1, short y1, boolean inv) {
        super(x0, y0, x1, y1, inv);
        startX = x0;
        startY = y0;
        endX = x1;
        endY = y1;
        inverted = inv;
        netNumber = NetNumbers.get();
    }

    public void setSelected() {
        selected = true;
    }

    public void setUnSelected() {
        selected = false;
    }

    //todo addCandidate save load and call from deviceviewer
    @Override
    void load(DataInputStream inputStream) throws java.io.IOException {
        inverted = inputStream.readBoolean();
        netNumber = inputStream.readInt();
    }

    void save(DataOutputStream outputStream) throws java.io.IOException {
        outputStream.writeBoolean(inverted);
        outputStream.writeInt(netNumber);
    }

    public void makeLogicalConnection(HashMap<Integer, DeviceIOModel> nets) {
        DeviceIOModel m = nets.get(netNumber);
        if (m != null) {
            m.connectTo(this.model);
        } else {
            nets.put(netNumber, this.model);
        }
        model.setNetNumber(netNumber);
    }

    @Override
    public int getNetNumber() {
        return netNumber;
    }

    @Override
    public void setNetNumber(int netNumber) {
        this.netNumber = netNumber;
    }

    public void putMask(Router router) {
        if (netNumber == router.startNet) {
            return;
        } else if (netNumber == router.targetNet) {
            return;
        }
        router.addLineMask(startX, startY, endX, endY);
    }

    public void putStartAndTarget(Router router) {
        if (netNumber == router.startNet) {
            router.addLineStart(startX, startY, endX, endY);
            return;
        } else if (netNumber == router.targetNet) {
            router.addLineTarget(startX, startY, endX, endY);
            return;
        }
    }

    protected void draw(Canvas canvas, int xo, int yo, Paint paint) {
        if (!locked) {
            paint=Context.unlockedPaint;
        } else {
            paint=Context.lockedPaint;
        }
        if (EditorModel.simulationIsRunning && Settings.displayWireLevel) {
            paint = Viewer.Context.getLevelColorPaint(model.level.ordinal());
        }
        rawDraw(canvas, xo, yo, paint);
        if (netNumberShowEnabled) {
            if (startX > endX) {
                canvas.drawText(Integer.toString(netNumber), xo + startX, yo + startY, Context.netNumberPaint);
            } else if (startX == endX) {
                canvas.drawText(Integer.toString(netNumber), xo + startX, yo + startY, Context.netNumberPaint);
            } else {
                canvas.drawText(Integer.toString(netNumber), xo + startX, yo + startY,
                        Context.netNumberPaint);
            }
        }
    }

    private void rawDraw(Canvas canvas, int xo, int yo, Paint paint) {
        float strokeWith = paint.getStrokeWidth();
        paint.setStrokeWidth(STROKE_WITH * 1.5f);
        canvas.drawLine(xo + startX, yo + startY, xo + endX, yo + endY, paint);
        paint.setStrokeWidth(strokeWith);
    }

    public void renumber(int netNumber, LinkedList<Integer> newNetNumbers) {
        if (this.netNumber == netNumber) {
            this.netNumber = NetNumbers.get();
            newNetNumbers.add(this.netNumber);
        }
    }
}

