package com.logicbreadboard.simulator;

/**
 * Created by laci on 6/19/15.
 */
public class NandAnsiViewer extends AndAnsiViewer {
    public NandAnsiViewer() {
        super();
        model = new NandModel();
    }
    @Override
    public short getSymbol() {
        return (short) SchematicViewerFactory.Gates.Nand.ordinal();
    }

    @Override
    public void setNumberOfIos(short numberOfIos) {
        super.setNumberOfIos(numberOfIos);
        firstIoViewer.inverted = true;
    }

    @Override
    public void setDefaultNumberOfIos() {
        setNumberOfIos((short) 3);
    }

}