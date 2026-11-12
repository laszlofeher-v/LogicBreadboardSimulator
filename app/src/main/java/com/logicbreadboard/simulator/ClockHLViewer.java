package com.logicbreadboard.simulator;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by laci on 10/21/15.
 */
public class ClockHLViewer extends ClockLHViewer {
    ClockHLViewer() {
        model = new ClockHLModel();
    }

    @Override
    public short getSymbol() {
        return (short) SchematicViewerFactory.Sources.Clock_HL.ordinal();
    }
    @Override
    float getSelectionZoom(int canvasHeight, int canvasWidth, float scaleFactor) {
        return Math.min(canvasWidth / (8 * (right - left)),
                canvasHeight / (8 * (bottom - top))) / (scaleFactor);
    }

    @Override
    protected void drawRotated(Canvas canvas, Paint paint) {
        int xo = (left + right) / 2;
        int yo = (top + bottom) / 2;
        int r = (right - left) / 2;
        canvas.drawCircle(xo, yo, r, paint);
        int up0 = (left + xo) / 2;
        int up1 = (xo + right) / 2;
        int high = (yo + top) / 2;
        int low = (bottom + yo) / 2;
        canvas.drawLine(up0, low, up0, high, paint);
        canvas.drawLine(up0, low, xo, low, paint);
        canvas.drawLine(xo, high, xo, low, paint);
        canvas.drawLine(xo, high, up1, high, paint);
        canvas.drawLine(up1, low, up1, high, paint);
        float textYpos;
        if (angleIndex == 2) {
            textYpos = top - 2 * paint.getStrokeWidth();
        } else {
            textYpos = bottom + currentTextPaint.getTextSize();
        }
        canvas.drawText(Integer.toString(model.delays[((ClockLHModel) model).PERIOD_TIME_INDEX])
                + " ns", left, textYpos, currentTextPaint);
    }

}
