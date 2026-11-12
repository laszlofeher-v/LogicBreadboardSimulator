package com.logicbreadboard.simulator;

/**
 * Created by laci on 6/22/15.
 */
public class InverterAnsiViewer extends BufferAnsiViewer {

    InverterAnsiViewer() {
        super();
        model = new InverterModel();
    }
    @Override
    public short getSymbol() {
        return (short) SchematicViewerFactory.Gates.Inverter.ordinal();
    }

    @Override
    public void setNumberOfIos(short numberOfIos) {
        super.setNumberOfIos(numberOfIos);
        firstIoViewer.inverted = true;
    }

    @Override
    public void setDefaultNumberOfIos() {
        setNumberOfIos((short) 2);
    }


}
