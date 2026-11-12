package com.logicbreadboard.simulator;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;

import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 * Created by laci on 11/5/15.
 */
public class PortViewer extends DeviceViewer {
    public static final int MAX_LENGTH = 6;
    private static final int HEIGHT = 16;
    private static final int WIDTH = 48;
    static private final int CONTEXT_MENU_MASK = ContextMenuIcons.COPY |
            ContextMenuIcons.DELETE |
            ContextMenuIcons.EDIT |
            ContextMenuIcons.ROTATE |
            ContextMenuIcons.ALIGN_HORIZONTAL | ContextMenuIcons.ALIGN_VERTICAL |
            ContextMenuIcons.LOCK | ContextMenuIcons.UNLOCK |
            ContextMenuIcons.UNSELECT;
    String id;

    PortViewer() {
        model = new LabelModel();
        id = new String("1");
    }

    //    LabelViewer(String id) {
//        model = new LabelModel();
//        id = new String(id);
//    }
    @Override
    public void initPosition(int x, int y, int angleIndex) {
        this.left = x;
        this.bottom = y;
        this.right = x + WIDTH;
        this.top = y - HEIGHT;
        this.angleIndex = angleIndex;
        setEnclosingRect();
    }

    @Override
    float getSelectionZoom(int canvasHeight, int canvasWidth, float scaleFactor) {
        return Math.min(canvasWidth / (8 * (right - left)),
                canvasHeight / (8 * (bottom - top))) / (scaleFactor);
    }

    @Override
    public short getGroup() {
        return (short) SchematicViewerFactory.DeviceGroup.Connections.ordinal();
    }

    @Override
    public short getSymbol() {
        return (short) SchematicViewerFactory.Connections.Port.ordinal();
    }

    @Override
    public void setDefaultNumberOfIos() {
        setNumberOfIos((short) 1);
    }

    @Override
    public int getContextMenuMask() {
        return CONTEXT_MENU_MASK;
    }

    @Override
    void load(DataInputStream inputStream) throws java.io.IOException {
        super.load(inputStream);
        id = inputStream.readUTF();
    }

    @Override
    public String getLabel() {
        return id;
    }

    @Override
    public void setLabel(String label) {
        id = new String(label);
    }

    @Override
    public String getPortLabel() {
        return id;
    }

    public String getEditMessage() {
        return "Enter a label";
    }

    public String getEditTitle() {
        return "Label editor";
    }

    public int getMaxIdLength() {
        return MAX_LENGTH;
    }

    @Override
    void save(DataOutputStream outputStream) throws java.io.IOException {
        super.save(outputStream);
        outputStream.writeUTF(id);
    }

    protected void createOneIo() {
//        DeviceIOViewer iov = new DeviceIOViewer((short) (WIDTH / 2), (short) (0),
//                (short) (DeviceIOViewer.LENGTH + WIDTH / 2), (short) (0), false);
//        DeviceIOViewer iov = new DeviceIOViewer((short) ((WIDTH) / 2), (short) 0,
//                (short) (DeviceIOViewer.LENGTH + (WIDTH) / 2), (short) 0, false);
        DeviceIOViewer iov = new DeviceIOViewer((short) (-WIDTH / 2),
                (short) (0),
                (short) (-WIDTH / 2 - DeviceIOViewer.LENGTH),
                (short) (0),
                false);
        iov.model = ((DeviceModel) model).firstInputModel;
        firstIoViewer = iov;
        ((LabelModel) model).iov = iov;
    }

    @Override
    public void setNumberOfIos(short numberOfIos) {
        this.numberOfIos = numberOfIos;
        model.init(numberOfIos, 0);
        createOneIo();
    }

    @Override
    void clone(int x, int y, Viewer old) {
        super.clone(x, y, old);
        String s = ((PortViewer) old).id;
        int n = 0;
        try {
            n = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            this.id = new String(((PortViewer) old).id);
            return;
        }
        n++;
        this.id = new String(Integer.toString(n));
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        rotateCanvas(canvas);
        drawRotated(canvas, Viewer.currentPaint);
        DeviceIOViewer ioViewer = firstIoViewer;
        int xo = Math.round((float) (left + right) / 2f);
        int yo = Math.round((float) (bottom + top) / 2f);
        do {
            ioViewer.draw(canvas, xo, yo, currentPaint);
            ioViewer = ioViewer.next;
        } while (ioViewer != null);
        if (angleIndex == 4) {
            restoreCanvas(canvas);
            drawText(left + 2 * STROKE_WITH, canvas, Viewer.currentPaint);
        } else {
            drawText(left + (HEIGHT) / 2, canvas, Viewer.currentPaint);
            restoreCanvas(canvas);
        }
    }

    private void drawText(float pos, Canvas canvas, Paint paint) {
        float size = paint.getTextSize();
        paint.setTextSize(bottom - top - 4 * STROKE_WITH);
        paint.setAntiAlias(true);
        paint.setTypeface(Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL));
        float strokeWith = paint.getStrokeWidth();
        paint.setStrokeWidth(STROKE_WITH / 2);
        paint.setLinearText(true);
        paint.setSubpixelText(true);
        paint.setFlags(Paint.EMBEDDED_BITMAP_TEXT_FLAG | Paint.FILTER_BITMAP_FLAG | Paint
                .ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.SUBPIXEL_TEXT_FLAG);
        canvas.drawText(id, pos, bottom - 4 * STROKE_WITH, paint);
        paint.setStrokeWidth(strokeWith);
        paint.setTextSize(size);
    }

    @Override
    protected void drawRotated(Canvas canvas, Paint paint) {
        int d = (HEIGHT) / 2;
        canvas.drawLine(left, (bottom + top) / 2, left + d, top, paint);
        canvas.drawLine(left, (bottom + top) / 2, left + d, bottom, paint);

//        canvas.drawLine(left, bottom, left, top, paint);
        canvas.drawLine(right, bottom, right, top, paint);
        canvas.drawLine(left + d, bottom, right, bottom, paint);
        canvas.drawLine(left + d, top, right, top, paint);
    }

    public int getWidth() {
        return WIDTH;
    }

    public int getHeight() {
        return HEIGHT;
    }

}
