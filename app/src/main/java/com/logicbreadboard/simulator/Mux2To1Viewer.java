package com.logicbreadboard.simulator;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by laci on 9/1/15.
 */
public class Mux2To1Viewer extends MuxViewer {
    final static short NUMBER_OF_IOS = 5; // 2 data, 1 select, 1 strobe, 1 out
    final static short NUMBER_OF_DATA_INPUTS = 2;
    final static short NUMBER_OF_SELECT_INPUTS = 1;

    public Mux2To1Viewer() {
        model = new Mux2To1Model();
    }

    @Override
    public short getSymbol() {
        return (short) SchematicViewerFactory.Mux.Mux_2_to_1.ordinal();
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
    protected void drawRotated(Canvas canvas, Paint paint) {
        drawRotatedWithText(canvas, paint, NUMBER_OF_SELECT_INPUTS, NUMBER_OF_DATA_INPUTS);
    }

}
