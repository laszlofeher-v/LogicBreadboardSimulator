package com.logicbreadboard.simulator;


import android.graphics.Canvas;
import android.graphics.Paint;

public class AndAnsiViewer extends GateViewer {

    public AndAnsiViewer() {
        model = new AndModel();
    }
    @Override
    public short getSymbol() {
        return (short) SchematicViewerFactory.Gates.And.ordinal();
    }

    @Override
    public void setDefaultNumberOfIos() {
        setNumberOfIos((short)3);
    }

    @Override
    protected void drawAnsiRotated(Canvas canvas, Paint paint) {
        canvas.drawLine(left, bottom, (left + right) / 2, bottom, paint);
        canvas.drawLine(left, top, (left + right) / 2, top, paint);
        canvas.drawLine(left, bottom, left, top, paint);
        parameterRectF.left = left;
        parameterRectF.bottom = bottom;
        parameterRectF.right = right;
        parameterRectF.top = top;
        canvas.drawArc(parameterRectF, 270, 180, false, paint);
    }

    protected void drawText(float pos, Canvas canvas, Paint paint) {
        drawThisText(" &", pos, canvas, paint);
    }

}
