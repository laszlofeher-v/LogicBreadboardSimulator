package com.logicbreadboard.simulator;

import android.graphics.Canvas;
import android.graphics.Paint;

public class BufferAnsiViewer extends GateViewer {
    static private final int CONTEXT_MENU_MASK = ContextMenuIcons.COPY |
            ContextMenuIcons.DELETE |
            ContextMenuIcons.DELAY |
            ContextMenuIcons.ROTATE |
            ContextMenuIcons.ALIGN_HORIZONTAL | ContextMenuIcons.ALIGN_VERTICAL |
            ContextMenuIcons.LOCK | ContextMenuIcons.UNLOCK |
            ContextMenuIcons.UNSELECT;

    public BufferAnsiViewer() {
        model = new BufferModel();
    }

    @Override
    public int getContextMenuMask() {
        return CONTEXT_MENU_MASK;
    }

    @Override
    public short getSymbol() {
        return (short) SchematicViewerFactory.Gates.Buffer.ordinal();
    }

    @Override
    public void setDefaultNumberOfIos() {
        setNumberOfIos((short) 2);
    }
//
//    @Override
//    protected void drawOneInput(Canvas canvas, float iPos, Paint paint) {
//        canvas.drawLine((left - IN_LENGTH), (iPos), (left), (iPos), paint);
//    }

    @Override
    protected void drawAnsiRotated(Canvas canvas, Paint paint) {
        float half = (bottom - top) / 2;
        canvas.drawLine(left, bottom, right, bottom - half, paint);
        canvas.drawLine(left, top, right, top + half, paint);
        canvas.drawLine(left, bottom, left, top, paint);
    }

    @Override
    protected void drawIecRotated(Canvas canvas, Paint paint) {
        float d = 4 * DeviceIOViewer.INVERTER_CIRCLE_R + STROKE_WITH;
        canvas.drawLine(left, bottom - d, left, top + d, paint);
        canvas.drawLine(right, bottom - d, right, top + d, paint);
        canvas.drawLine(left, bottom - d, right, bottom - d, paint);
        canvas.drawLine(left, top + d, right, top + d, paint);
    }

    @Override
    public void plusIO(EditorViewer editorViewer) {
    }

    protected void drawText(float pos, Canvas canvas, Paint paint) {
        drawThisText(" 1", pos, canvas, paint);
    }

}