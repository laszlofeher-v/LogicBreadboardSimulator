package com.logicbreadboard.simulator;

/**
 * Created by laci on 6/23/15.
 */
public class InverterTSLAnsiViewer extends InverterTSHAnsiViewer {
    InverterTSLAnsiViewer() {
        model = new InverterTSLModel();
    }

    @Override
    public void setNumberOfIos(short numberOfIos) {
        super.setNumberOfIos(numberOfIos);
     //   firstOutputViewer.inverted=true;
        firstIoViewer.next.inverted=true;
    }

    @Override
    public short getSymbol() {
        return (short) SchematicViewerFactory.Gates.Inverter_TS_L.ordinal();
    }

    @Override
    public void setDefaultNumberOfIos() {
        setNumberOfIos((short)3);
    }

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
