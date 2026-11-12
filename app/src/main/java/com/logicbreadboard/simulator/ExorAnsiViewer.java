package com.logicbreadboard.simulator;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by laci on 6/23/15.
 */
public class ExorAnsiViewer extends OrAnsiViewer {

    static private final int CONTEXT_MENU_MASK = ContextMenuIcons.COPY |
            ContextMenuIcons.DELETE |
            ContextMenuIcons.DELAY |
            ContextMenuIcons.ROTATE |
            ContextMenuIcons.ALIGN_HORIZONTAL | ContextMenuIcons.ALIGN_VERTICAL|
            ContextMenuIcons.LOCK| ContextMenuIcons.UNLOCK|
            ContextMenuIcons.UNSELECT;

    public ExorAnsiViewer() {
        super();
        model = new ExorModel();
    }

    @Override
    public int getContextMenuMask() {
        return CONTEXT_MENU_MASK;
    }

    @Override
    protected void drawInputArc(Canvas canvas, Paint paint) {
//        float inArcX0;
//        float inArcY0;
//        float inArcR;
        parameterRectF.left = left - (float) (GATE_WIDTH * (1 + Math.sqrt(3) / 2));
        parameterRectF.right = parameterRectF.left + 2 * GATE_WIDTH;
        parameterRectF.bottom = bottom + GATE_HEIGHT / 2;
        parameterRectF.top = top - GATE_HEIGHT / 2;
//        inArcX0 = parameterRectF.centerX();
//        inArcY0 = parameterRectF.centerY();
//        inArcR = parameterRectF.getWidth() / 2;
        canvas.drawArc(parameterRectF, 330, 60, false, paint);
        parameterRectF.left = parameterRectF.left - GATE_WIDTH / 8;
        parameterRectF.right = parameterRectF.left + 2 * GATE_WIDTH;
        canvas.drawArc(parameterRectF, 330, 60, false, paint);
    }

    @Override
    public void putMask(Router router) {
        router.addRectMask(enclosingLeft - CLEARANCE - 2,
                enclosingBottom + CLEARANCE - STROKE_WITH,
                enclosingRight + CLEARANCE,
                enclosingTop - CLEARANCE);
        DeviceIOViewer ioViewer = firstIoViewer;
        while (ioViewer != null) {
            DeviceIOViewer p = getIoCanvasPos(ioViewer);
            p.putMask(router);
            ioViewer = ioViewer.next;
        }
    }

    @Override
    public short getSymbol() {
        return (short) SchematicViewerFactory.Gates.Exor.ordinal();
    }

    @Override
    public void setDefaultNumberOfIos() {
        setNumberOfIos((short) 3);
    }

    protected void drawText(float pos, Canvas canvas, Paint paint) {
        drawThisText("=1",pos,canvas,paint);
    }


}
