package com.logicbreadboard.simulator;

/**
 * Created by laci on 11/2/15.
 */
public class DIPViewer64  extends DIPViewer {
    protected final int BOX_WIDTH = 32* unit;
    protected final int BOX_HEIGHT = 6* unit;

    DIPViewer64() {
        model = new DIPModel();
    }

    @Override
    public short getSymbol() {
        return (short) SchematicViewerFactory.Box.DIP_64.ordinal();
    }
    public int getWidth() {
        return BOX_WIDTH;
    }
    public int getHeight() {
        return BOX_HEIGHT;
    }
    @Override
    public void setDefaultNumberOfIos() {
        setNumberOfIos((short) 64);
    }


}
