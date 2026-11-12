package com.logicbreadboard.simulator;

/**
 * Created by laci on 11/2/15.
 */
public class DIPViewer40 extends DIPViewer {
    protected final int BOX_WIDTH = 20* unit;
    protected final int BOX_HEIGHT = 6* unit;

    DIPViewer40() {
        model = new DIPModel();
    }

    @Override
    public short getSymbol() {
        return (short) SchematicViewerFactory.Box.DIP_40.ordinal();
    }
    public int getWidth() {
        return BOX_WIDTH;
    }
    public int getHeight() {
        return BOX_HEIGHT;
    }
    @Override
    public void setDefaultNumberOfIos() {
        setNumberOfIos((short) 40);
    }


}
