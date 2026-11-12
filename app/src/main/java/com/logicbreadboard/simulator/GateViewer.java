package com.logicbreadboard.simulator;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;

import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 * Created on 12/30/14.
 */
public class GateViewer extends DeviceViewer {
    protected static final short[][] IO_POS = new short[10][8];
    protected static final short GATE_HEIGHT_HALF;
    protected static final short GATE_WIDTH_HALF;
    protected static final short GATE_HEIGHT;
    protected static final short GATE_WIDTH;
    static final int MAX_NUMBER_OF_IO = 9; //8 inputs 1 output
    static private final int CONTEXT_MENU_MASK = ContextMenuIcons.ADD_INPUT |
//            ContextMenuIcons.MORPH |
            ContextMenuIcons.COPY |
            ContextMenuIcons.DELETE |
            ContextMenuIcons.DELAY |
            ContextMenuIcons.ROTATE |
            ContextMenuIcons.ALIGN_HORIZONTAL | ContextMenuIcons.ALIGN_VERTICAL |
            ContextMenuIcons.LOCK | ContextMenuIcons.UNLOCK |
            ContextMenuIcons.UNSELECT;
    static private final int CONTEXT_MENU_MASK_NO_ADD_INPUT = CONTEXT_MENU_MASK &
            (~ContextMenuIcons.ADD_INPUT);
    //TODO sizes must depend on screen size and resolution
    protected static final short UNIT;
    private static final short BASE;

    static {
        UNIT = 2;
        BASE = (short) (UNIT * 8);
        GATE_HEIGHT = (short) (BASE + 5 * (BASE / 4));
        GATE_WIDTH = (short) (BASE + 5 * BASE / 4);
        GATE_HEIGHT_HALF = (short) (GATE_HEIGHT / 2);
        GATE_WIDTH_HALF = (short) (GATE_WIDTH / 2);

//        GATE_HEIGHT[2] = BASE; // 2 ios
//        GATE_HEIGHT[3] = BASE; // 3 ios
//        GATE_HEIGHT[4] = BASE; // 3 inputs
//        GATE_HEIGHT[5] = BASE + (BASE / 4);
//        GATE_HEIGHT[6] = BASE + 2 * (BASE / 4);
//        GATE_HEIGHT[7] = BASE + 3 * (BASE / 4);
//        GATE_HEIGHT[8] = BASE + 4 * (BASE / 4);
//        GATE_HEIGHT[9] = BASE + 5 * (BASE / 4);

//        GATE_HEIGHT[2] = BASE + 5 * (BASE / 4); // 2 ios
//        GATE_HEIGHT[3] = BASE + 5 * (BASE / 4); // 3 ios
//        GATE_HEIGHT[4] = BASE + 5 * (BASE / 4); // 3 inputs
//        GATE_HEIGHT[5] = BASE + 5 * (BASE / 4);
//        GATE_HEIGHT[6] = BASE + 5 * (BASE / 4);
//        GATE_HEIGHT[7] = BASE + 5 * (BASE / 4);
//        GATE_HEIGHT[8] = BASE + 5 * (BASE / 4);
//        GATE_HEIGHT[9] = BASE + 5 * (BASE / 4);

        IO_POS[2][2] = (short) (0); // 1 input, position of input_1
        //
        IO_POS[3][0] = (short) (-2 * BASE / 4); // 2 inputs, position of input_1
        IO_POS[3][1] = (short) (2 * BASE / 4); // 2 inputs, position of input_2
        //
        IO_POS[4][0] = (short) (-2 * BASE / 4); // 3 inputs, position of input_1
        IO_POS[4][1] = (short) (0); // 3 inputs, position of input_2
        IO_POS[4][2] = (short) (2 * BASE / 4); // 3 inputs, position of input_3
        //
        IO_POS[5][0] = (short) (-3 * BASE / 4); // 4 inputs, position of input_1
        IO_POS[5][1] = (short) (-BASE / 4); // 4 inputs, position of input_2
        IO_POS[5][2] = (short) (BASE / 4); // 4 inputs, position of input_3
        IO_POS[5][3] = (short) (3 * BASE / 4); // 4 inputs, position of input_4
        //
        IO_POS[6][0] = (short) (-4 * BASE / 4); // 5 inputs, position of input_1
        IO_POS[6][1] = (short) (-2 * BASE / 4); // 5 inputs, position of input_2
        IO_POS[6][2] = (short) (0); // 5 inputs, position of input_3
        IO_POS[6][3] = (short) (2 * BASE / 4); // 5 inputs, position of input_4
        IO_POS[6][4] = (short) (4 * BASE / 4); // 5 inputs, position of input_5
        //
        IO_POS[7][0] = (short) (-5 * BASE / 8); // 6 inputs, position of input_1
        IO_POS[7][1] = (short) (-3 * BASE / 8); // 6 inputs, position of input_2
        IO_POS[7][2] = (short) (-BASE / 8); // 6 inputs, position of input_3
        IO_POS[7][3] = (short) (BASE / 8); // 6 inputs, position of input_4
        IO_POS[7][4] = (short) (3 * BASE / 8); // 6 inputs, position of input_5
        IO_POS[7][5] = (short) (5 * BASE / 8); // 6 inputs, position of input_6
        //
        IO_POS[8][0] = (short) (-3 * BASE / 4); // 7 inputs, position of input_1
        IO_POS[8][1] = (short) (-2 * BASE / 4); // 7 inputs, position of input_2
        IO_POS[8][2] = (short) (-BASE / 4); // 7 inputs, position of input_3
        IO_POS[8][3] = (short) (0);// 7 inputs, position of input_4
        IO_POS[8][4] = (short) (BASE / 4); // 7 inputs, position of input_5
        IO_POS[8][5] = (short) (2 * BASE / 4); // 7 inputs, position of input_6
        IO_POS[8][6] = (short) (3 * BASE / 4); // 7 inputs, position of input_7
        //
        IO_POS[9][0] = (short) (-7 * BASE / 8); // 8 inputs, position of input_1
        IO_POS[9][1] = (short) (-5 * BASE / 8); // 8 inputs, position of input_2
        IO_POS[9][2] = (short) (-3 * BASE / 8); // 8 inputs, position of input_3
        IO_POS[9][3] = (short) (-BASE / 8); // 8 inputs, position of input_4
        IO_POS[9][4] = (short) (BASE / 8); // 8 inputs, position of input_5
        IO_POS[9][5] = (short) (3 * BASE / 8); // 8 inputs, position of input_6
        IO_POS[9][6] = (short) (5 * BASE / 8); // 8 inputs, position of input_7
        IO_POS[9][7] = (short) (7 * BASE / 8); // 8 inputs, position of input_8

    }

    public GateViewer() {
        super();
    }

    @Override
    public int getContextMenuMask() {
        if (numberOfIos < MAX_NUMBER_OF_IO) {
            return CONTEXT_MENU_MASK;
        } else {
            return CONTEXT_MENU_MASK_NO_ADD_INPUT;
        }
    }

    @Override
    public short getGroup() {
        return (short) SchematicViewerFactory.DeviceGroup.Gates.ordinal();
    }

    @Override
    void save(DataOutputStream outputStream) throws java.io.IOException {
        super.save(outputStream);
        model.writeDelays(outputStream);
    }

    @Override
    public void setNumberOfIos(short numberOfIos) {
        this.numberOfIos = numberOfIos;
        model.init(numberOfIos - 1, 1);
        createInputs();
        DeviceIOModel ioModel = ((DeviceModel) model).firstOutputModel;
        DeviceIOViewer firstOutputViewer = new DeviceIOViewer(GATE_WIDTH_HALF, (short) 0, (short)
                (GATE_WIDTH_HALF + DeviceIOViewer.LENGTH), (short) 0, false);
        firstOutputViewer.model = ioModel;
        firstOutputViewer.next = firstIoViewer;
        firstIoViewer = firstOutputViewer;
    }

    protected void createOneInput(DeviceIOModel ioModel, int iPos) {
        short y = IO_POS[numberOfIos][iPos];
        DeviceIOViewer iov = new DeviceIOViewer((short) (-GATE_WIDTH_HALF), y,
                (short) (-DeviceIOViewer.LENGTH - (GATE_WIDTH_HALF)), y, false);
        iov.model = ioModel;
        iov.next = firstIoViewer;
        firstIoViewer = iov;
    }

    @Override
    public void plusIO(EditorViewer editorViewer) {
        short y;
        if (numberOfIos >= MAX_NUMBER_OF_IO) {
            return;
        }
        numberOfIos++;
        DeviceIOModel im = ((GateModel) model).plusOneInput();
        DeviceIOViewer iv = firstIoViewer.next;
        int iPos = numberOfIos - 2;
        while (iv.next != null) {
            y = IO_POS[numberOfIos][iPos--];
            iv.startY = y;
            iv.endY = y;
            iv = iv.next;
        }
        y = IO_POS[numberOfIos][iPos--];
        iv.startY = y;
        iv.endY = y;
        y = IO_POS[numberOfIos][iPos];
        iv.next = new DeviceIOViewer((short) (-GATE_WIDTH_HALF), y,
                (short) (-DeviceIOViewer.LENGTH - (GATE_WIDTH_HALF)), y, false);
        iv.next.model = im;
    }

    private void createInputs() {
        DeviceIOModel ioModel = ((DeviceModel) model).firstInputModel;
        int numberOfInputs = numberOfIos - 1;
        for (short i = 0; i < numberOfInputs; i++) {
            createOneInput(ioModel, i);
            ioModel = ioModel.next;
        }
    }
//
//    protected void changeOneInput(DeviceIOViewer oldViewer, int iPos) {
//        int y = IO_POS[numberOfIos][iPos];
//        DeviceIOViewer iov = new DeviceIOViewer(-(GATE_WIDTH) / 2, y,
//                -DeviceIOViewer.LENGTH - (GATE_WIDTH) / 2, y, false);
//        iov.model = oldViewer.model;
//        iov.next = firstIoViewer;
//        firstIoViewer = iov;
//    }

    @Override
    float getSelectionZoom(int canvasHeight, int canvasWidth, float scaleFactor) {
        float n = 8;
        if (numberOfIos >= 6) {
            n = 4;
        }
        return Math.max(canvasWidth / (n * (right - left)),
                canvasHeight / (n * (bottom - top))) / (scaleFactor);
    }

    // Removes one or more inputs. Tri state control input cannot be removed in the
    // subclass method.
    @Override
    public boolean removeSelectedIO(Selections selections) {
        boolean found = false;
        int count = 0;
        DeviceIOViewer io = firstIoViewer.next;
        while (io != null) {
            if (selections.isRoutableSelected(io)) {
                //TODO if io.boardConnection then warning and reject
                count++;
            }
            io = io.next;
        }
        if (count > 0) {
            if (numberOfIos - count < 3) {
                // TODO toaster warning
                // TODO check if output is selected
                return true;
            }
            found = true;
            numberOfIos -= count;
            DeviceIOViewer iov = firstIoViewer;
            while (iov.next != null) {
                if (iov.next.selected) {
                    model.removeInput(iov.next.model);
                    iov.next = iov.next.next;
                } else {
                    iov = iov.next;
                }
            }
            iov = firstIoViewer.next;
            int iPos = numberOfIos - 2;
            short y;
            while (iov != null) {
                y = IO_POS[numberOfIos][iPos--];
                iov.startY = iov.endY = y;
                iov = iov.next;
            }
        }
        return found;
    }

    protected void drawThisText(String s, float pos, Canvas canvas, Paint paint) {
        float size = paint.getTextSize();
        paint.setTextSize((bottom - top) / 2);
        paint.setAntiAlias(true);
        paint.setTypeface(Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL));
        float strokeWith = paint.getStrokeWidth();
        paint.setStrokeWidth(STROKE_WITH / 2);
        paint.setLinearText(true);
        paint.setSubpixelText(true);
        paint.setFlags(Paint.EMBEDDED_BITMAP_TEXT_FLAG | Paint.FILTER_BITMAP_FLAG | Paint
                .ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.SUBPIXEL_TEXT_FLAG);
        canvas.drawText(s, pos, bottom - (bottom - top) / 4 - 2 * STROKE_WITH, paint);
        paint.setStrokeWidth(strokeWith);
        paint.setTextSize(size);
    }

    @Override
    public void setDefaultNumberOfIos() {
    }

    @Override
    public void initPosition(int x, int y, int angleIndex) {
        this.left = x;
        this.bottom = y;
        this.right = x + GATE_WIDTH;
        this.top = y - GATE_HEIGHT;
        this.angleIndex = angleIndex;
        setEnclosingRect();
    }

    @Override
    void load(DataInputStream inputStream) throws java.io.IOException {
        super.load(inputStream);
        model.readDelays(inputStream);
    }

    @Override
    public void draw(Canvas canvas) {
        rotateCanvas(canvas);
        if (Settings.symbolType == Settings.ANSI) {
            drawAnsiRotated(canvas, Viewer.currentPaint);
        } else if (Settings.symbolType == Settings.IEC) {
            drawIecRotated(canvas, Viewer.currentPaint);
        } else if (Settings.symbolType== Settings.DIN) {
            drawDinRotated(canvas, Viewer.currentPaint);
        }
        DeviceIOViewer ioViewer = firstIoViewer;
        int xo = Math.round((float) (left + right) / 2f);
        int yo = Math.round((float) (bottom + top) / 2f);
        do {
            ioViewer.draw(canvas, xo, yo, currentPaint);
            ioViewer = ioViewer.next;
        } while (ioViewer != null);
        if (Settings.symbolType == Settings.IEC) {
            if (angleIndex == 4) {
                restoreCanvas(canvas);
                drawText(left + 2 * STROKE_WITH, canvas, Viewer.currentPaint);
            } else {
                drawText(left + 2 * STROKE_WITH, canvas, Viewer.currentPaint);
                restoreCanvas(canvas);
            }
        } else {
            restoreCanvas(canvas);
        }
//        drawToolTip(canvas);
    }

    protected void drawText(float pos, Canvas canvas, Paint paint) {
    }

    protected void drawAnsiRotated(Canvas canvas, Paint paint) {
    }

    protected void drawIecRotated(Canvas canvas, Paint paint) {
        canvas.drawLine(left, bottom, left, top, paint);
        canvas.drawLine(right, bottom, right, top, paint);
        canvas.drawLine(left, bottom, right, bottom, paint);
        canvas.drawLine(left, top, right, top, paint);
    }

    protected void drawDinRotated(Canvas canvas, Paint paint) {
    }

    @Override
    public void delete() {
        model.delete();
    }

    public int getWidth() {
        return GATE_WIDTH;
    }

    public int getHeight() {
        return GATE_HEIGHT;
    }

}
