package com.logicbreadboard.simulator;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Created by laci on 8/10/15.
 */
public class JKFlipFlopViewer extends FlipFlopViewer {
    final static float leftIoTextPos = 3 * STROKE_WITH;
    final static float rightIoTextPos = FF_WIDTH - TEXT_SIZE;
    final static float bottomIoTextPos = -STROKE_WITH - TEXT_SIZE;
    final static float topIoTextPos = -FF_HEIGHT - STROKE_WITH + 2 * TEXT_SIZE;
    private final Rect textBounds = new Rect(); //don't new this up in a draw method

    JKFlipFlopViewer() {
        model = new JKFlipFlopModel();
    }

    @Override
    public short getSymbol() {
        return (short) SchematicViewerFactory.Flip_Flops.JK_Flip_Flop.ordinal();
    }

    @Override
    public void setDefaultNumberOfIos() {
        setNumberOfIos((short) 7);
    }

    void createOneInput(short x0, short y0, short x1, short y1, boolean inv, DeviceIOModel
            ioModel) {
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
        //TODO get appropriate model
        createOneInput((short) 0, (short) (-FF_HEIGHT / 2), (short) 0, (short) (-FF_HEIGHT / 2 -
                DeviceIOViewer.LENGTH), false, ioModel);
        ioModel = ioModel.next;
        createOneInput((short) 0, (short) (FF_HEIGHT / 2), (short) 0, (short) (FF_HEIGHT / 2 +
                DeviceIOViewer.LENGTH), false, ioModel);
        ioModel = ioModel.next;
        short horizontalIOFromBottom = (short) (Math.round((float) FF_HEIGHT / 3) - STROKE_WITH);
        //- STROKE_WITH * 2;
        short horizontalIOFromTop = (short) -horizontalIOFromBottom;
        createOneInput((short) (-FF_WIDTH / 2), horizontalIOFromBottom, (short) (-FF_WIDTH
                        / 2 - DeviceIOViewer.LENGTH), horizontalIOFromBottom,
                false, ioModel);
        ioModel = ioModel.next;
        createOneInput((short) (-FF_WIDTH / 2), horizontalIOFromTop, (short) (-FF_WIDTH /
                        2 - DeviceIOViewer.LENGTH), horizontalIOFromTop, false,
                ioModel);
        ioModel = ioModel.next;
        createOneInput((short) (-FF_WIDTH / 2), (short) 0, (short) (-FF_WIDTH /
                        2 - DeviceIOViewer.LENGTH), (short) 0, false,
                ioModel);
        ioModel = ((DeviceModel) model).firstOutputModel;
        createOneInput((short) (FF_WIDTH / 2), horizontalIOFromTop,
                (short) (FF_WIDTH / 2 + DeviceIOViewer.LENGTH),
                horizontalIOFromTop, false, ioModel);
        ioModel = ioModel.next;
        createOneInput((short) (FF_WIDTH / 2), horizontalIOFromBottom,
                (short) (FF_WIDTH / 2 + DeviceIOViewer.LENGTH), horizontalIOFromBottom, false,
                ioModel);
//
//        DeviceIOViewer firstOutputViewer = new DeviceIOViewer(DISPLAY_WIDTH / 2, horizontalIOFromTop,
//                DISPLAY_WIDTH / 2 +
//                        DeviceIOViewer.LENGTH, horizontalIOFromTop, false, this);
//        firstOutputViewer.model = ioModel;
//        firstOutputViewer.next = new DeviceIOViewer(DISPLAY_WIDTH / 2, horizontalIOFromBottom,
//                DISPLAY_WIDTH / 2 + DeviceIOViewer.LENGTH, horizontalIOFromBottom, false, this);
//        firstOutputViewer.next.model = ioModel;
//        firstOutputViewer.next = firstIoViewer;
//        firstIoViewer = firstOutputViewer;
    }

    public void drawTextCentred(Canvas canvas, Paint paint, String text, float cx, float cy) {
        paint.getTextBounds(text, 0, text.length(), textBounds);
        canvas.drawText(text, cx - textBounds.exactCenterX(), cy - textBounds.exactCenterY(), paint);
    }

    @Override
    protected void drawRotated(Canvas canvas, Paint paint) {
        super.drawRotated(canvas, paint);
        //TODO draw predefined bitmap
        float size = Context.textPaint.getTextSize();
        Context.textPaint.setTextSize(TEXT_SIZE);
//        paint.setAntiAlias(true);
//        paint.setTypeface(Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL));
//        float strokeWith = paint.getStrokeWidth();
//        paint.setStrokeWidth(STROKE_WITH / 2);
//        paint.setLinearText(true);
//        paint.setSubpixelText(true);
//        paint.setFlags(Paint.EMBEDDED_BITMAP_TEXT_FLAG | Paint.FILTER_BITMAP_FLAG | Paint
//                .ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.SUBPIXEL_TEXT_FLAG);
//        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("S", left + (-TEXT_SIZE / 2 + FF_WIDTH) / 2, top + TEXT_SIZE + STROKE_WITH, Context.textPaint);
        // canvas.drawLine(left - IN_LENGTH, topIoTextPos - textSize / 2, left, topIoTextPos - textSize / 2, textPaint);
        canvas.drawText("R", left + (-TEXT_SIZE / 2 + FF_WIDTH) / 2, bottom - 3 * STROKE_WITH, Context.textPaint);
//        drawTextCentred(canvas, paint, ">C", left, bottom + bottomIoTextPos - STROKE_WITH);
        canvas.drawText("K", left + leftIoTextPos, bottom + bottomIoTextPos, Context.textPaint);
//        canvas.drawLine(left+20, bottom + bottomIoTextPos - STROKE_WITH, left+20, bottom, paint);
        canvas.drawText("J", left + leftIoTextPos, bottom + topIoTextPos, Context.textPaint);
        canvas.drawText(">C", left, bottom - FF_HEIGHT / 2 + TEXT_SIZE / 2 - 1, Context.textPaint);
        canvas.drawText("Q", left + rightIoTextPos, bottom + topIoTextPos, Context.textPaint);
        canvas.drawText("_", left + rightIoTextPos, bottom + bottomIoTextPos - TEXT_SIZE, Context.textPaint);
        canvas.drawText("Q", left + rightIoTextPos, bottom + bottomIoTextPos, Context.textPaint);
//        paint.setStrokeWidth(strokeWith);
        Context.textPaint.setTextSize(size);
    }


}
