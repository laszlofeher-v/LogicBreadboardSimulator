package com.logicbreadboard.simulator;

/**
 * Created by laci on 10/10/15.
 */
public class PMOSViewer extends NMOSViewer {
    public PMOSViewer() {
        model = new PMOSModel();
    }
    @Override
    public short getSymbol() {
        return (short) SchematicViewerFactory.Transistors.PMOS.ordinal();
    }

    @Override
    public void setNumberOfIos(short numberOfIos) {
        super.setNumberOfIos(numberOfIos);
        firstIoViewer.next.next.inverted = true;
    }

}
