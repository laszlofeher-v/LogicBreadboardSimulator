package com.logicbreadboard.simulator;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 * Created on 9/3/15.
 */
public class DemuxViewer extends DeviceViewer {
    protected static final short[][] IO_POS = new short[10][11];
    protected static final short MUX_HEIGHT_HALF;
    protected static final short MUX_WIDTH_HALF;
    protected static final short MUX_HEIGHT;
    protected static final short MUX_WIDTH;
    static final int TEXT_SIZE;
    static final int MAX_NUMBER_OF_IO = 12; //11 inputs 1 output
    static private final int CONTEXT_MENU_MASK =
            ContextMenuIcons.COPY |
                    ContextMenuIcons.DELETE |
                    ContextMenuIcons.DELAY |
                    ContextMenuIcons.ROTATE |
                    ContextMenuIcons.ALIGN_HORIZONTAL | ContextMenuIcons.ALIGN_VERTICAL |
                    ContextMenuIcons.LOCK | ContextMenuIcons.UNLOCK |
                    ContextMenuIcons.UNSELECT;
    //TODO sizes must depend on screen size and resolution
    private static final short UNIT;
    private static final short BASE;

    static {
        UNIT = 2;
        BASE = (short) (UNIT * 8);
        MUX_HEIGHT = (short) (2 * (BASE + 5 * (BASE / 4)));
        MUX_WIDTH = (short) (BASE + 5 * BASE / 4);
        MUX_HEIGHT_HALF = (short) (MUX_HEIGHT / 2);
        MUX_WIDTH_HALF = (short) (MUX_WIDTH / 2);
        TEXT_SIZE = MUX_HEIGHT / 16;
        //
        IO_POS[2][0] = (short) (-2 * BASE / 2);
        IO_POS[2][1] = (short) (2 * BASE / 2);
        IO_POS[2][2] = (short) (0);
        //
        IO_POS[4][0] = (short) (-3 * BASE / 2);
        IO_POS[4][1] = (short) (-BASE / 2);
        IO_POS[4][2] = (short) (BASE / 2);
        IO_POS[4][3] = (short) (3 * BASE / 2);
        IO_POS[4][4] = (short) (-2 * BASE / 4);
        IO_POS[4][5] = (short) (2 * BASE / 4);
        //
        //
        IO_POS[8][0] = (short) (-7 * BASE / 4);
        IO_POS[8][1] = (short) (-5 * BASE / 4);
        IO_POS[8][2] = (short) (-3 * BASE / 4);
        IO_POS[8][3] = (short) (-BASE / 4);
        IO_POS[8][4] = (short) (BASE / 4);
        IO_POS[8][5] = (short) (3 * BASE / 4);
        IO_POS[8][6] = (short) (5 * BASE / 4);
        IO_POS[8][7] = (short) (7 * BASE / 4);
        IO_POS[8][8] = (short) (-3 * BASE / 4);
        IO_POS[8][9] = (short) (0);
        IO_POS[8][10] = (short) (3 * BASE / 4);
    }

    public DemuxViewer() {
        super();
    }

    @Override
    public int getContextMenuMask() {
        return CONTEXT_MENU_MASK;
    }

    @Override
    public short getGroup() {
        return (short) SchematicViewerFactory.DeviceGroup.Demux.ordinal();
    }

    @Override
    void save(DataOutputStream outputStream) throws java.io.IOException {
        super.save(outputStream);
        model.writeDelays(outputStream);
    }

    protected void createIos() {
    }

    protected void createOneOutput(DeviceIOModel ioModel, int iPos, int numberOfDataInputs) {
        short y = IO_POS[numberOfDataInputs][iPos];
        DeviceIOViewer iov = new DeviceIOViewer((short) (MUX_WIDTH_HALF), y,
                (short) (DeviceIOViewer.LENGTH + (MUX_WIDTH_HALF)), y, false);
        iov.model = ioModel;
        iov.next = firstIoViewer;
        firstIoViewer = iov;
    }

    protected void createOneSelectInput(DeviceIOModel ioModel, int iPos, int numberOfDataOutputs) {
        short x = IO_POS[numberOfDataOutputs][iPos];
        DeviceIOViewer iov = new DeviceIOViewer(x, (short) (MUX_HEIGHT_HALF), x,
                (short) (MUX_HEIGHT_HALF + DeviceIOViewer.LENGTH), false);
        iov.model = ioModel;
        iov.next = firstIoViewer;
        firstIoViewer = iov;
    }

    protected void createAllIos(int numberOfDataOutputs, int numberOfSelectInputs) {
        DeviceIOModel ioModel = ((DeviceModel) model).firstOutputModel;
        for (short i = 0; i < numberOfDataOutputs; i++) {
            createOneOutput(ioModel, i, numberOfDataOutputs);
            ioModel = ioModel.next;
        }
        ioModel = ((DeviceModel) model).firstInputModel.next;
        for (short i = 0; i < numberOfSelectInputs; i++) {
            createOneSelectInput(ioModel, i + numberOfDataOutputs, numberOfDataOutputs);
            ioModel = ioModel.next;
        }
    }

    @Override
    public void initPosition(int x, int y, int angleIndex) {
        this.left = x;
        this.bottom = y;
        this.right = x + MUX_WIDTH;
        this.top = y - MUX_HEIGHT;
        this.angleIndex = angleIndex;
        setEnclosingRect();
    }

    @Override
    void load(DataInputStream inputStream) throws java.io.IOException {
        super.load(inputStream);
        model.readDelays(inputStream);
    }

    @Override
    public void delete() {
        model.delete();
    }

    public int getWidth() {
        return MUX_WIDTH;
    }

    public int getHeight() {
        return MUX_HEIGHT;
    }

    protected void drawRotatedWithText(Canvas canvas, Paint paint, int nsi, int ndi) {
        canvas.drawLine(left, bottom, left, top, paint);
        canvas.drawLine(right, bottom, right, top, paint);
        canvas.drawLine(left, bottom, right, bottom, paint);
        canvas.drawLine(left, top, right, top, paint);
        DeviceIOViewer io = firstIoViewer.next;
        int xo = (left + right) / 2;
        int yo = (top + bottom) / 2;
        float size=Viewer.Context.textPaint.getTextSize();
        Viewer.Context.textPaint.setTextSize(TEXT_SIZE);
        for (int i = 0; i < nsi; i++) {
            canvas.drawText("S" + Integer.toString(i), xo + io.startX - TEXT_SIZE, yo + io.startY -
                    STROKE_WITH, Viewer.Context.textPaint);
            io = io.next;
        }
        for (int i = 0; i < ndi; i++) {
            canvas.drawText("Y" + Integer.toString(i), xo + io.startX - STROKE_WITH - TEXT_SIZE,
                    yo + io.startY
                            + TEXT_SIZE / 2, Viewer.Context
                            .textPaint);
            io = io.next;
        }
        canvas.drawText("X",
                xo + firstIoViewer.startX + STROKE_WITH,
                yo + firstIoViewer.startY + TEXT_SIZE / 2, Context
                        .textPaint);
        Viewer.Context.textPaint.setTextSize(size);
    }
}