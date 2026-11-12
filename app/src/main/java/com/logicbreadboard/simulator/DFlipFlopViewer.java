package com.logicbreadboard.simulator;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

/**
 * Created by laci on 6/23/15.
 */
public class DFlipFlopViewer extends FlipFlopViewer {
    final static float leftIoTextPos = 3 * STROKE_WITH;
    final static float rightIoTextPos = FF_WIDTH - TEXT_SIZE;
    final static float bottomIoTextPos = -STROKE_WITH - TEXT_SIZE;
    final static float topIoTextPos = -FF_HEIGHT - STROKE_WITH + 2 * TEXT_SIZE;
    private final Rect textBounds = new Rect(); //don't new this up in a draw method

    DFlipFlopViewer() {
        model = new DFlipFlopModel();
    }

    @Override
    public short getSymbol() {
        return (short) SchematicViewerFactory.Flip_Flops.D_Flip_Flop.ordinal();
    }

    @Override
    public void setDefaultNumberOfIos() {
        setNumberOfIos((short) 6);
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
        model.init(numberOfIos-2,2);
        DeviceIOModel ioModel = ((DeviceModel) model).firstInputModel;
        createOneIo((short) 0, (short) (-FF_HEIGHT / 2), (short) 0, (short) (-FF_HEIGHT / 2 -
                DeviceIOViewer.LENGTH), false, ioModel);
        ioModel = ioModel.next;
        createOneIo((short) 0, (short) (FF_HEIGHT / 2), (short) 0, (short) (FF_HEIGHT / 2 +
                DeviceIOViewer.LENGTH), false, ioModel);
        ioModel = ioModel.next;
        short horizontalIOFromBottom = (short) (Math.round((float) FF_HEIGHT / 3) - STROKE_WITH);
        short horizontalIOFromTop = (short) -horizontalIOFromBottom;
        createOneIo((short) (-FF_WIDTH / 2), horizontalIOFromBottom, (short) (-FF_WIDTH
                        / 2 - DeviceIOViewer.LENGTH), horizontalIOFromBottom,
                false, ioModel);
        ioModel = ioModel.next;
        createOneIo((short) (-FF_WIDTH / 2), horizontalIOFromTop, (short) (-FF_WIDTH /
                        2 - DeviceIOViewer.LENGTH), horizontalIOFromTop, false,
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
        canvas.drawText("S", left + (-TEXT_SIZE / 2 + FF_WIDTH) / 2, top + TEXT_SIZE + STROKE_WITH, paint);
        // canvas.drawLine(left - IN_LENGTH, topIoTextPos - textSize / 2, left, topIoTextPos - textSize / 2, paint);
        canvas.drawText("R", left + (-TEXT_SIZE / 2 + FF_WIDTH) / 2, bottom - 3 * STROKE_WITH, paint);
//        drawTextCentred(canvas, paint, ">C", left, bottom + bottomIoTextPos - STROKE_WITH);
        canvas.drawText(">C", left, bottom + bottomIoTextPos, paint);
//        canvas.drawLine(left+20, bottom + bottomIoTextPos - STROKE_WITH, left+20, bottom, paint);
        canvas.drawText("D", left + leftIoTextPos, bottom + topIoTextPos, paint);
        canvas.drawText("Q", left + rightIoTextPos, bottom + topIoTextPos, paint);
        canvas.drawText("_", left + rightIoTextPos, bottom + bottomIoTextPos - TEXT_SIZE, paint);
        canvas.drawText("Q", left + rightIoTextPos, bottom + bottomIoTextPos, paint);
        paint.setStrokeWidth(strokeWith);
        paint.setTextSize(size);
    }


}
