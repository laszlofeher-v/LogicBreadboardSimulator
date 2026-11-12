package com.logicbreadboard.simulator;

/**
 * Created by laci on 6/23/15.
 */
public class BufferTSLAnsiViewer extends BufferTSHAnsiViewer {
    BufferTSLAnsiViewer() {
        model = new BufferTSLModel();
    }
    @Override
    public short getSymbol() {
        return (short) SchematicViewerFactory.Gates.Buffer_TS_L.ordinal();
    }

    @Override
    public void setNumberOfIos(short numberOfIos) {
        super.setNumberOfIos(numberOfIos);
        firstIoViewer.next.inverted=true;
//        firstOutputViewer.inverted = true;
    }

    @Override
    public void setDefaultNumberOfIos() {
        setNumberOfIos((short)3);
    }
//
//    @Override
//    protected void drawIn(Canvas canvas, Paint paint) {
//        float offset = (GATE_HEIGHT) / 2;
//        float iPos = bottom - offset;
//        drawOneInput(canvas, iPos, paint);
//        float x = left + (right - left) / 2;
//        canvas.drawLine(x, 2 * (INVERTER_CIRCLE_R + STROKE_WITH) +
//                bottom - (bottom - top) / 4, x, bottom, paint);
//        canvas.drawCircle(x, (INVERTER_CIRCLE_R + STROKE_WITH) + bottom - (bottom - top) / 4, INVERTER_CIRCLE_R, paint);
//    }

}
