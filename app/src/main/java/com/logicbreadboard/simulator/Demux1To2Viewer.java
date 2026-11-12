package com.logicbreadboard.simulator;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created on 9/3/15.
 */
public class Demux1To2Viewer extends DemuxViewer {
    final static short NUMBER_OF_IOS = 4; // 1 data, 1 select, 2 out
    final static short NUMBER_OF_DATA_OUTPUTS = 2;
    final static short NUMBER_OF_SELECT_INPUTS = 1;

    public Demux1To2Viewer() {
        model = new Demux1To2Model();
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
    protected void createIos() {
        createAllIos(NUMBER_OF_DATA_OUTPUTS, NUMBER_OF_SELECT_INPUTS);
    }

    @Override
    protected void drawRotated(Canvas canvas, Paint paint) {
        drawRotatedWithText(canvas, paint, NUMBER_OF_SELECT_INPUTS, NUMBER_OF_DATA_OUTPUTS);
    }

    @Override
    public void setNumberOfIos(short numberOfIos) {
        this.numberOfIos = numberOfIos;
        model.init(NUMBER_OF_IOS-NUMBER_OF_DATA_OUTPUTS,NUMBER_OF_DATA_OUTPUTS);
        createIos();
        DeviceIOModel ioModel = ((DeviceModel) model).firstInputModel;
        DeviceIOViewer firstInputViewer = new DeviceIOViewer((short) (-MUX_WIDTH_HALF), (short) 0,
                (short)
                        (-MUX_WIDTH_HALF - DeviceIOViewer.LENGTH), (short) 0, false);
        firstInputViewer.model = ioModel;
        firstInputViewer.next = firstIoViewer;
        firstIoViewer = firstInputViewer;
    }

}