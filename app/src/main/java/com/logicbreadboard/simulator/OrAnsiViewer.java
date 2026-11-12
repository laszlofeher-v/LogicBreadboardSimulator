package com.logicbreadboard.simulator;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by laci on 6/22/15.
 */
public class OrAnsiViewer extends GateViewer {

    public static final float LINE_RATIO = 0.125f;
//    protected float inArcX0;
//    protected float inArcY0;
//    protected float inArcR;

    public OrAnsiViewer() {
        model = new OrModel();
    }

    @Override
    public short getSymbol() {
        return (short) SchematicViewerFactory.Gates.Or.ordinal();
    }

    @Override
    public void setDefaultNumberOfIos() {
        setNumberOfIos((short) 3);
    }

    @Override
    protected void drawAnsiRotated(Canvas canvas, Paint paint) {
        canvas.drawLine(left, bottom, left + LINE_RATIO * (right - left), bottom, paint);
        canvas.drawLine(left, top, left + LINE_RATIO * (right - left), top, paint);
        drawInputArc(canvas, paint);
        drawOutArcs(canvas, paint);
    }

    protected void drawInputArc(Canvas canvas, Paint paint) {
        parameterRectF.left = left - (float) (GATE_WIDTH * (1 + Math.sqrt(3) / 2));
        parameterRectF.right = parameterRectF.left + 2 * GATE_WIDTH;
        float halfHeight = GATE_HEIGHT / 2;
        parameterRectF.bottom = bottom + halfHeight;
        parameterRectF.top = top - halfHeight;
        canvas.drawArc(parameterRectF, 330, 60, false, paint);
    }


    @Override
    protected void createOneInput(DeviceIOModel ioModel, int iPos) {
        if (Settings.symbolType == Settings.IEC) {
            super.createOneInput(ioModel, iPos);
        } else {
            float inArcX0;
            float inArcY0;
            float inArcR;
            parameterRectF.left = -(float) (GATE_WIDTH * (1 + Math.sqrt(3) / 2));
            parameterRectF.right = parameterRectF.left + 2 * GATE_WIDTH;
            float height = GATE_HEIGHT;
            parameterRectF.bottom = height / 2;
            parameterRectF.top = -height / 2;
            inArcX0 = parameterRectF.centerX();
            inArcY0 = parameterRectF.centerY();
            inArcR = parameterRectF.width() / 2;
            float y = IO_POS[numberOfIos][iPos];
            short iy = IO_POS[numberOfIos][iPos];
            short d = (short) Math.round(Math.sqrt(inArcR * inArcR - (y - inArcY0) * (y - inArcY0)) -
                    (-inArcX0) - (float) GATE_WIDTH / 2 - (float) STROKE_WITH / 2);
            DeviceIOViewer iov = new DeviceIOViewer(d, iy, (short) (-GATE_WIDTH_HALF -
                    DeviceIOViewer.LENGTH), iy, false);
            iov.model = ioModel;
            iov.next = firstIoViewer;
            firstIoViewer = iov;
        }
    }

    @Override
    public void plusIO(EditorViewer editorViewer) {
        super.plusIO(editorViewer);
        setStartX();
    }

    @Override
    public boolean removeSelectedIO(Selections selections) {
        boolean found = super.removeSelectedIO(selections);

        if (found) {
            setStartX();
        }
        return found;
    }

    public void setStartX() {
        float inArcX0;
        float inArcY0;
        float inArcR;
        parameterRectF.left = -(float) (GATE_WIDTH * (1 + Math.sqrt(3) / 2));
        parameterRectF.right = parameterRectF.left + 2 * GATE_WIDTH;
        float height = GATE_HEIGHT;
        parameterRectF.bottom = height / 2;
        parameterRectF.top = -height / 2;
        inArcX0 = parameterRectF.centerX();
        inArcY0 = parameterRectF.centerY();
        inArcR = parameterRectF.width() / 2;
        int iPos = 0;
        DeviceIOViewer io = firstIoViewer.next;
        while (io != null) {
            short y = IO_POS[numberOfIos][iPos];
            io.startX = (short) Math.round(Math.sqrt(inArcR * inArcR - (y - inArcY0) * (y -
                    inArcY0)) -
                    (-inArcX0) - (float) GATE_WIDTH / 2 - (float) STROKE_WITH / 2);
            iPos++;
            io = io.next;
        }
    }

    protected void drawOutArcs(Canvas canvas, Paint paint) {
        parameterRectF.left = left + (LINE_RATIO * (right - left));
        //    rectF.right = rectF.left + GATE_WIDTH;
        float d = (right - parameterRectF.left);
        float b = (bottom - top) / 2;
        float a = (d * d - b * b) / (2 * b);
        float r = a + b;
        final float doubleR = 2 * r;
        parameterRectF.top = top;
        parameterRectF.bottom = top + doubleR;
        parameterRectF.left = parameterRectF.left - r;
        parameterRectF.right = parameterRectF.left + doubleR;
        final float degrees = (float) Math.toDegrees(Math.atan(d / (a)));
        canvas.drawArc(parameterRectF, 270, degrees, false, paint);
        float height = GATE_HEIGHT;
        parameterRectF.bottom = parameterRectF.bottom + height - doubleR;
        parameterRectF.top = parameterRectF.bottom - doubleR;
        canvas.drawArc(parameterRectF, 90 - degrees, degrees, false, paint);
    }

    @Override
    protected void drawText(float pos, Canvas canvas, Paint paint) {
        drawThisText("\u2265" + "1", pos, canvas, paint);
    }

}
