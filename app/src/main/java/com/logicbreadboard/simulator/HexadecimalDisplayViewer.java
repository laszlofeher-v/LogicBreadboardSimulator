package com.logicbreadboard.simulator;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created on 8/26/15.
 */
public class HexadecimalDisplayViewer extends DecimalDisplayViewer {
    HexadecimalDisplayViewer() {
        model = new DecimalDisplayModel(this);
    }

    @Override
    public int getContextMenuMask() {
        return CONTEXT_MENU_MASK;
    }

    @Override
    protected void createEnableInput() {
        DeviceIOViewer iov = new DeviceIOViewer((short) ((DISPLAY_WIDTH / 2) + 2),
                (short) (0),
                (short) (DeviceIOViewer.LENGTH + (DISPLAY_WIDTH / 2) - 2),
                (short) (0), false);
        firstIoViewer = iov;
    }
    @Override
    float getSelectionZoom(int canvasHeight, int canvasWidth, float scaleFactor) {
        return Math.min(canvasWidth / (8 * (right - left)),
                canvasHeight / (8 * (bottom - top))) / (scaleFactor);
    }

    @Override
    public short getGroup() {
        return (short) SchematicViewerFactory.DeviceGroup.Displays.ordinal();
    }

    @Override
    public short getSymbol() {
        return (short) SchematicViewerFactory.Displays.Hexadecimal.ordinal();
    }

    @Override
    protected void drawContent(Canvas canvas, Paint paint) {
        float size = paint.getTextSize();
        paint.setTextSize(32);
        if (((DecimalDisplayModel) model).value == DecimalDisplayModel.BAD_VALUE) {
            canvas.drawText("X", left + STROKE_WITH / 2, bottom - STROKE_WITH, paint);
        } else {
            if (((DecimalDisplayModel) model).enabled) {
                canvas.drawText(Integer.toHexString(((DecimalDisplayModel) model).value)
                                .toUpperCase(), left +
                                STROKE_WITH,
                        bottom - STROKE_WITH, paint);
            } else {
                canvas.drawText(" ", left + STROKE_WITH / 2, bottom - STROKE_WITH, paint);
            }
        }
        paint.setTextSize(size);
    }

    @Override
    protected void drawRotated(Canvas canvas, Paint paint) {
        canvas.drawLine(left - 2, (bottom + top) / 2, left + 1, top, paint);
        canvas.drawLine(left + 1, bottom, left - 2, (bottom + top) / 2, paint);
        canvas.drawLine(right + 2, (bottom + top) / 2, right - 1, top, paint);
        canvas.drawLine(right - 1, bottom, right + 2, (bottom + top) / 2, paint);
        canvas.drawLine(left + 1, bottom, right - 1, bottom, paint);
        canvas.drawLine(left + 1, top, right - 1, top, paint);
        float size = paint.getTextSize();
        paint.setTextSize(4);
        canvas.drawText("1", right - (DISPLAY_WIDTH / 6), bottom + 3 + STROKE_WITH, paint);
        drawContent(canvas, Context
                .displayPaint);
        paint.setTextSize(size);
    }

}
