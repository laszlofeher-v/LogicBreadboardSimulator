package com.logicbreadboard.simulator;

/**
 * Created by laci on 6/23/15.
 */
public class NorAnsiViewer extends OrAnsiViewer {
    public NorAnsiViewer() {
        model = new NorModel();
    }

    @Override
    public short getSymbol() {
        return (short) SchematicViewerFactory.Gates.Nor.ordinal();
    }

    @Override
    public void setNumberOfIos(short numberOfIos) {
        super.setNumberOfIos(numberOfIos);
//        firstOutputViewer.inverted = true;
        firstIoViewer.inverted = true; // first io is the output
    }

    @Override
    public void setDefaultNumberOfIos() {
        setNumberOfIos((short) 3);
    }


//    @Override
//    public void drawOut(Canvas canvas, Paint paint) {
//        drawNOut(canvas, paint);
//    }
//
//    @Override
//    public void draw(Canvas canvas) {
//        Paint paint = getPaint(selected);
//        rotateCanvas(canvas);
//        drawNegation(canvas, paint);
//        drawRotated(canvas, paint);
//        drawNOut(canvas, paint);
//        drawIn(canvas, paint);
//        restoreCanvas(canvas);
//    }
}
