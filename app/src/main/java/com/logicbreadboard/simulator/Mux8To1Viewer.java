package com.logicbreadboard.simulator;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by laci on 9/1/15.
 */
public class Mux8To1Viewer extends MuxViewer {
    final static short NUMBER_OF_IOS = 13; // 8 data, 3 select, 1 strobe, 1 out
    final static short NUMBER_OF_DATA_INPUTS = 8;
    final static short NUMBER_OF_SELECT_INPUTS = 3;

    public Mux8To1Viewer() {
        model = new Mux8To1Model();
    }

    @Override
    public void setDefaultNumberOfIos() {
        setNumberOfIos(NUMBER_OF_IOS);
    }

    @Override
    protected void createInputs() {
        createAllInputs(NUMBER_OF_DATA_INPUTS, NUMBER_OF_SELECT_INPUTS);
    }

    @Override
    public short getSymbol() {
        return (short) SchematicViewerFactory.Mux.Mux_8_to_1.ordinal();
    }

    @Override
    protected void drawRotated(Canvas canvas, Paint paint) {
        drawRotatedWithText(canvas, paint, NUMBER_OF_SELECT_INPUTS, NUMBER_OF_DATA_INPUTS);
    }

}
