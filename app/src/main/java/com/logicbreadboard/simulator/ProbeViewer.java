package com.logicbreadboard.simulator;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 * Created by laci on 8/17/15.
 */
public class ProbeViewer extends LabelViewer {
    public static final int CHANNEL_HEIGHT = 32;
    public static final int LEVEL = CHANNEL_HEIGHT * 3 / 4;
    private static final int HEIGHT = 16;
    private static final int WIDTH = 48;
    static private final int CONTEXT_MENU_MASK = ContextMenuIcons.COPY |
            ContextMenuIcons.DELETE |
            ContextMenuIcons.EDIT |
            ContextMenuIcons.ROTATE |
            ContextMenuIcons.DELAY |
            ContextMenuIcons.ALIGN_HORIZONTAL | ContextMenuIcons.ALIGN_VERTICAL |
            ContextMenuIcons.LOCK | ContextMenuIcons.UNLOCK |
            ContextMenuIcons.UNSELECT;

    ProbeViewer() {
        model = new ProbeModel();
        id = new String("CH0");
    }

    ProbeViewer(String id) {
        model = new ProbeModel();
        this.id = new String(id);
    }

    public int getMaxSimTime(int soFar) {
        return Math.max(soFar, model.delays[((ProbeModel) model).END_TIME_INDEX]);
    }

    @Override
    float getSelectionZoom(int canvasHeight, int canvasWidth, float scaleFactor) {
        return Math.min(canvasWidth / (8 * (right - left)),
                canvasHeight / (8 * (bottom - top))) / (scaleFactor);
    }

    @Override
    protected void drawRotated(Canvas canvas, Paint paint) {
        canvas.drawLine(left, bottom, left, top, Context.scopePaint);
        canvas.drawLine(right, bottom, right, top, paint);
        canvas.drawLine(left, bottom, right, bottom, paint);
        canvas.drawLine(left, top, right, top, paint);
//        paint.setTextSize(bottom - top - 4 * STROKE_WITH);
//        paint.setAntiAlias(true);
//        paint.setTypeface(Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL));
//        float strokeWith = paint.getStrokeWidth();
//        paint.setStrokeWidth(STROKE_WITH / 2);
//        paint.setLinearText(true);
//        paint.setSubpixelText(true);
//        paint.setFlags(Paint.EMBEDDED_BITMAP_TEXT_FLAG | Paint.FILTER_BITMAP_FLAG | Paint
//                .ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.SUBPIXEL_TEXT_FLAG);
        canvas.drawText(id, left + STROKE_WITH, bottom - 4 * STROKE_WITH, Context.scopeTextPaint);
//        paint.setStrokeWidth(strokeWith);
    }

    @Override
    public short getGroup() {
        return (short) SchematicViewerFactory.DeviceGroup.Displays.ordinal();
    }

    @Override
    public short getSymbol() {
        return (short) SchematicViewerFactory.Displays.Probe.ordinal();
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
    public float showText(Canvas canvas, float x, float y) {
        canvas.drawText(id, x, y - CHANNEL_HEIGHT, Context.scopeTextPaint);
        return y + CHANNEL_HEIGHT;
    }

    @Override
    void load(DataInputStream inputStream) throws java.io.IOException {
        super.load(inputStream);
        model.readDelays(inputStream);
    }

    @Override
    void save(DataOutputStream outputStream) throws java.io.IOException {
        super.save(outputStream);
        model.writeDelays(outputStream);
    }

    @Override
    public boolean showRecord(Canvas canvas, float xOffset, float endTime, float y, float zoom) {
        float x0;
        float x1 = 0;
        int level1 = 0;
        int endOfTime = 0;
        if (((ProbeModel) model).last > 0) {
            endOfTime = Math.max(Event.simulationTime, ((ProbeModel) model)
                    .time[((ProbeModel) model).last - 1]);
        }
//        endOfTime = Math.round((endOfTime + x) );
        endOfTime = Math.round((endOfTime + xOffset) * zoom);
        canvas.drawLine(xOffset * zoom, y, endOfTime, y, Viewer
                .Context.cannotDeletePaint);
        if (((ProbeModel) model).time[0] > 0) {
            x0 = ((ProbeModel) model).time[0] + xOffset;
            x0 *= zoom;
            canvas.drawRect(0, y - LEVEL, x0, y, Context
                    .probeOverflowPaint);
        }
        int level0 = ((ProbeModel) model).level[0];
        level1 = level0;
        for (int i = 0; (i + 1) < ((ProbeModel) model).last; i++) {
            level0 = ((ProbeModel) model).level[i]; // TODO: 9/9/15 check if it can be in if
            // statement
            level1 = ((ProbeModel) model).level[i + 1];
            int time = ((ProbeModel) model).time[i];
            int nextTime = ((ProbeModel) model).time[i + 1];
            if (nextTime > (-xOffset) && time < endTime) {
                x0 = time + xOffset;
                x1 = nextTime + xOffset;
                x0 *= zoom;
                x1 *= zoom;
                drawSegment(canvas, y, x0, x1, level0);
            }
        }
        if (x1 < endOfTime) {
            if (((ProbeModel) model).last == ((ProbeModel) model).time.length) {
                canvas.drawRect(x1, y - LEVEL, endOfTime, y, Context
                        .probeOverflowPaint);
            } else {
                float lastValidX = endOfTime;
                if (lastValidX > x1) {
                    drawLastSegment(canvas, y, x1, lastValidX, level1);
                }
                if (lastValidX < endOfTime) {
                    canvas.drawRect(lastValidX, y - LEVEL, endOfTime, y, Context
                            .probeOverflowPaint);
                }
            }
        } else if (x1 > endOfTime) {
//            System.out.println("Probe time issue");
        }
        return true;
    }

    public void drawSegment(Canvas canvas, float y, float x0, float x1, int level0) {
        if (x0 == x1) {
            canvas.drawLine(x0, y, x1, y - LEVEL, Context.scopePaint);
        } else if (level0 == State.Level.undefined.ordinal()) {
            canvas.drawRect(x0, y - LEVEL, x1, y, Context.scopeUndefPaint);
        } else if (level0 == State.Level.low.ordinal()) {
            canvas.drawLine(x0, y, x1, y, Context.scopePaint);
            canvas.drawLine(x1, y, x1, y - LEVEL, Context.scopePaint);
        } else {
            canvas.drawLine(x0, y - LEVEL, x1, y - LEVEL, Context.scopePaint);
            canvas.drawLine(x1, y - LEVEL, x1, y, Context.scopePaint);
        }
    }

    public void drawLastSegment(Canvas canvas, float y, float x0, float x1, int level0) {
        if (level0 == State.Level.undefined.ordinal()) {
            canvas.drawRect(x0, y - LEVEL, x1, y, Context.scopeUndefPaint);
        } else if (level0 == State.Level.low.ordinal()) {
            canvas.drawLine(x0, y, x1, y, Context.scopePaint);
        } else {
            canvas.drawLine(x0, y - LEVEL, x1, y - LEVEL, Context.scopePaint);
        }
    }

    @Override
    public String getLabel() {
        return id;
    }

    @Override
    public void setLabel(String label) {
        id = new String(label);
    }

    public int getWidth() {
        return WIDTH;
    }

    public int getHeight() {
        return HEIGHT;
    }

}
