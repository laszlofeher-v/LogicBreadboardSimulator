package com.logicbreadboard.simulator;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

/**
 * Created by laci on 8/15/15.
 */
public class MonoStableViewer extends FlipFlopViewer {
    final static float leftIoTextPos = 3 * STROKE_WITH;
    final static float rightIoTextPos = FF_WIDTH - TEXT_SIZE;
    final static float bottomIoTextPos = -STROKE_WITH - TEXT_SIZE;
    final static float topIoTextPos = -FF_HEIGHT - STROKE_WITH + 2 * TEXT_SIZE;
    private final Rect textBounds = new Rect(); //don't new this up in a draw method

    MonoStableViewer() {
        model = new MonoStableModel();
    }

    @Override
    public short getSymbol() {
        return (short) SchematicViewerFactory.Flip_Flops.Monostable.ordinal();
    }

    @Override
    public void setDefaultNumberOfIos() {
        setNumberOfIos((short) 3);
    }

    void createOneIo(short x0, short y0, short x1, short y1, boolean inv, DeviceIOModel ioModel) {
        DeviceIOViewer iov = new DeviceIOViewer(x0, y0, x1, y1, inv);
        iov.model = ioModel;
        iov.next = firstIoViewer;
        firstIoViewer = iov;
    }

    @Override
    public void setNumberOfIos(short numberOfIos) {
        this.numberOfIos = numberOfIos;
        model.init(numberOfIos - 2, 2);
        DeviceIOModel ioModel = ((DeviceModel) model).firstInputModel;
        short horizontalIOFromBottom = (short) (Math.round((float) FF_HEIGHT / 3) - STROKE_WITH);
        short horizontalIOFromTop = (short) -horizontalIOFromBottom;
        createOneIo((short) (-FF_WIDTH / 2), (short) 0, (short) (-FF_WIDTH /
                        2 - DeviceIOViewer.LENGTH), (short) 0, false,
                ioModel);
        ioModel = ((DeviceModel) model).firstOutputModel;
        createOneIo((short) (FF_WIDTH / 2), horizontalIOFromTop,
                (short) (FF_WIDTH / 2 + DeviceIOViewer.LENGTH), horizontalIOFromTop, false, ioModel);
        ioModel = ioModel.next;
        createOneIo((short) (FF_WIDTH / 2), horizontalIOFromBottom,
                (short) (FF_WIDTH / 2 + DeviceIOViewer.LENGTH), horizontalIOFromBottom, false,
                ioModel);
    }

    public void drawTextCentred(Canvas canvas, Paint paint, String text, float cx, float cy) {
        paint.getTextBounds(text, 0, text.length(), textBounds);
        canvas.drawText(text, cx - textBounds.exactCenterX(), cy - textBounds.exactCenterY(), paint);
    }

    @Override
    protected void drawRotated(Canvas canvas, Paint paint) {
        super.drawRotated(canvas, paint);
        //TODO draw predefined bitmap
        float size=paint.getTextSize();
        paint.setTextSize(TEXT_SIZE);
        paint.setAntiAlias(true);
        paint.setTypeface(Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL));
        float strokeWith = paint.getStrokeWidth();
        paint.setStrokeWidth(STROKE_WITH / 2);
        paint.setLinearText(true);
        paint.setSubpixelText(true);
        paint.setFlags(Paint.EMBEDDED_BITMAP_TEXT_FLAG | Paint.FILTER_BITMAP_FLAG | Paint
                .ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.SUBPIXEL_TEXT_FLAG);
//        paint.setTextAlign(Paint.Align.CENTER);
//        drawTextCentred(canvas, paint, ">C", left, bottom + bottomIoTextPos - STROKE_WITH);
        canvas.drawText(">T", left, TEXT_SIZE / 2 - STROKE_WITH + bottom - FF_HEIGHT / 2, paint);
//        canvas.drawLine(left+20, bottom + bottomIoTextPos - STROKE_WITH, left+20, bottom, paint);
        canvas.drawText("Q", left + rightIoTextPos, bottom + topIoTextPos, paint);
        canvas.drawText("_", left + rightIoTextPos, bottom + bottomIoTextPos - TEXT_SIZE, paint);
        canvas.drawText("Q", left + rightIoTextPos, bottom + bottomIoTextPos, paint);
        paint.setStrokeWidth(strokeWith);
        final float xstart = left + FF_WIDTH / 5;
        final float xup = left + FF_WIDTH / 3;
        final float xdown = right - FF_WIDTH / 3;
        final float xend = right - FF_WIDTH / 5;
        final float ybottom = bottom - FF_HEIGHT / 3;
        final float ytop = top + FF_HEIGHT / 3;
        canvas.drawLine(xstart, ybottom, xup, ybottom, paint);
        canvas.drawLine(xup, ybottom, xup, ytop, paint);
        canvas.drawLine(xup, ytop, xdown, ytop, paint);
        canvas.drawLine(xdown, ytop, xdown, ybottom, paint);
        canvas.drawLine(xdown, ybottom, xend, ybottom, paint);
        paint.setTextSize(size);
    }

}
