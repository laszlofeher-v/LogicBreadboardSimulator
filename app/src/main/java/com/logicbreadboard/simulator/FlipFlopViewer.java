package com.logicbreadboard.simulator;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 * Created by laci on 6/23/15.
 */
public class FlipFlopViewer extends DeviceViewer {
    static final int FF_WIDTH = 48;
    static final int FF_HEIGHT = 64;
    static final int TEXT_SIZE = FF_HEIGHT / 8;
    static private final int CONTEXT_MENU_MASK = ContextMenuIcons.COPY |
            ContextMenuIcons.DELETE |
            ContextMenuIcons.DELAY |
            ContextMenuIcons.ROTATE |
            ContextMenuIcons.ALIGN_HORIZONTAL | ContextMenuIcons.ALIGN_VERTICAL|
            ContextMenuIcons.LOCK| ContextMenuIcons.UNLOCK|
            ContextMenuIcons.UNSELECT;

    @Override
    public int getContextMenuMask() {
        int orMask = 0;
        int andMask = 0xFFFFFFFF;
        DeviceIOViewer io = firstIoViewer;
        while (io != null) {
            if (io.selected) {
                if (io.model.isOutputModel()) {
                    andMask = ~ContextMenuIcons.CHANGE_INV;
                } else {
                    orMask = ContextMenuIcons.CHANGE_INV;
                }
            }
            io = io.next;
        }
        return (CONTEXT_MENU_MASK | orMask) & andMask;
    }

    @Override
    public void setNumberOfIos(short numberOfIos) {
        this.numberOfIos = numberOfIos;
        model.init(numberOfIos-2,2);
    }

    @Override
    public short getGroup() {
        return (short) SchematicViewerFactory.DeviceGroup.Flip_Flops.ordinal();
    }

    public void draw(Canvas canvas) {
        super.draw(canvas);
        rotateCanvas(canvas);
        drawRotated(canvas, currentPaint);
        restoreCanvas(canvas);
    }

    @Override
    void load(DataInputStream inputStream) throws java.io.IOException {
        super.load(inputStream);
//        int left = inputStream.readInt();
//        int bottom = inputStream.readInt();
//        byte angleIndex = inputStream.readByte();
//        initPosition(left, bottom, angleIndex);
//        numberOfIos = inputStream.readShort();
//        setNumberOfIos(numberOfIos);
        model.readDelays(inputStream);
        model.setInverted(firstIoViewer);
        //       ((FlipFlopModel) model).delay = inputStream.readInt();
    }

    @Override
    void save(DataOutputStream outputStream) throws java.io.IOException {
        super.save(outputStream);
//        outputStream.writeInt(getGroup());
//        outputStream.writeInt(getSymbol());
//        outputStream.writeInt(left);
//        outputStream.writeInt(bottom);
//        outputStream.writeByte(angleIndex);
//        outputStream.writeShort(numberOfIos);
        model.writeDelays(outputStream);
//        outputStream.writeInt(((FlipFlopModel) model).delay);
    }

    @Override
    public void initPosition(int x, int y, int angleIndex) {
        this.left = x;
        this.bottom = y;
        this.right = x + FF_WIDTH;
        this.top = y - FF_HEIGHT;
        this.angleIndex = angleIndex;
        setEnclosingRect();
        //       this.numberOfIos = numberOfIos;
        //       this.selectedIos = new boolean[numberOfIos];
    }

    @Override
    protected void drawRotated(Canvas canvas, Paint paint) {
        canvas.drawLine(left, bottom, left, top, paint);
        canvas.drawLine(right, bottom, right, top, paint);
        canvas.drawLine(left, bottom, right, bottom, paint);
        canvas.drawLine(left, top, right, top, paint);
    }

    @Override
    public void changeInv(Selections selections) {
        DeviceIOViewer io = firstIoViewer;
        int pos = 1;
        while (io != null) {
            if (selections.isRoutableSelected(io)) {
                selections.selectRoutable(io);
                io.inverted = !io.inverted;
            }
            pos = pos << 1;
            io = io.next;
        }
        model.setInverted(firstIoViewer);
//        io = firstOutputViewer;
//        while (io != null) {
//            if (io.selected) {
//                io.selected = false;
//            }
//            io = io.next;
//        }
    }

    public int getWidth() {
        return FF_WIDTH;
    }

    public int getHeight() {
        return FF_HEIGHT;
    }

}
