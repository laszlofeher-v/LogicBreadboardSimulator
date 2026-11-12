package com.logicbreadboard.simulator;

/**
 * Created by laci on 6/23/15.
 */
public class ExnorAnsiViewer extends ExorAnsiViewer {

    public ExnorAnsiViewer() {
        super();
        model = new ExnorModel();
    }

    @Override
    public void setNumberOfIos(short numberOfIos) {
        super.setNumberOfIos(numberOfIos);
        firstIoViewer.inverted = true;
    }
//TODO exnor model

    //    @Override
//    public void drawOut(Canvas canvas, Paint paint) {
//        drawNOut(canvas, paint);
//    }

    @Override
    public short getSymbol() {
        return (short) SchematicViewerFactory.Gates.Exnor.ordinal();
    }
}
