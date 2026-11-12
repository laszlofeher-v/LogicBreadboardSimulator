package com.logicbreadboard.simulator;

/**
 * Created by laci on 6/23/15.
 */
public class InverterTSHAnsiViewer extends BufferTSHAnsiViewer {
    InverterTSHAnsiViewer() {
        model = new  InverterTSHModel();
    }

    @Override
    public void setNumberOfIos(short numberOfIos) {
        super.setNumberOfIos(numberOfIos);
        firstIoViewer.inverted = true;
    }
    @Override
    public short getSymbol() {
        return (short) SchematicViewerFactory.Gates.Inverter_TS_H.ordinal();
    }

    @Override
    public void setDefaultNumberOfIos() {
        setNumberOfIos((short) 3);
    }

//    @Override
//    public void drawOut(Canvas canvas, Paint paint) {
//        drawNOut(canvas, paint);
//    }

}
