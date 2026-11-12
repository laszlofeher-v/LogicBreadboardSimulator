package com.logicbreadboard.simulator;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by laci on 9/1/15.
 */
public class Mux4To1Viewer extends MuxViewer {
    final static short NUMBER_OF_IOS = 8; // 4 data, 2 select, 1 strobe, 1 out
    final static short NUMBER_OF_DATA_INPUTS = 4;
    final static short NUMBER_OF_SELECT_INPUTS = 2;

    public Mux4To1Viewer() {
        model = new Mux4To1Model();
    }

    @Override
    public short getSymbol() {
        return (short) SchematicViewerFactory.Mux.Mux_4_to_1.ordinal();
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
