package com.logicbreadboard.simulator;

import java.io.DataOutputStream;

/**
 * Created on 5/10/16.
 */
public class BBTactile2_2SwViewer extends BBTactileSwViewer {
    static protected final int CONTEXT_MENU_MASK = ContextMenuIcons.COPY |
            ContextMenuIcons.DELETE |
            ContextMenuIcons.DELAY |
            ContextMenuIcons.FINE_ROTATE |
            ContextMenuIcons.UNSELECT;
    int color;
    protected final int BOX_WIDTH = 3 * unit;
    protected final int BOX_HEIGHT = 3 * unit;

    @Override
    public short getGroup() {
        return (short) BBViewerFactory.factory.getGroupDIPSw();
    }

//    @Override
//    public short getSymbol() {
//        return (short) BBViewerFactory.BB_Switch.Tactile2_2.ordinal();
//    }

    public int getWidth() {
        return BOX_WIDTH;
    }

    public int getHeight() {
        return BOX_HEIGHT;
    }

    @Override
    public void setDefaultNumberOfIos() {
        setNumberOfIos((short) 4);
    }

    @Override
    public void setNumberOfIos(short numberOfIos) {
        this.numberOfIos = numberOfIos;
        model.init(numberOfIos - 4, 4);
        createAllInputs();
    }

    @Override
    void save(DataOutputStream outputStream) throws java.io.IOException {
        super.save(outputStream);
    }

    BBTactile2_2SwViewer() {
        model = new BBTactile2_2Model();
    }

    @Override
    public int getContextMenuMask() {
        return CONTEXT_MENU_MASK;
    }

    @Override
    protected void createAllInputs() {
        DeviceIOViewer iov = firstIoViewer;
        DeviceOutputModel outputModel = ((DeviceModel) model).firstOutputModel;
        iov = createOneIO(iov, 0, (short) (getHeight() / 2), 1);
        iov.model = outputModel;
        outputModel = (DeviceOutputModel) outputModel.next;
        iov = createOneIO(iov, 2, (short) (getHeight() / 2), 3);
        iov.model = outputModel;
        outputModel = (DeviceOutputModel) outputModel.next;
        iov = createOneIO(iov, 0, (short) -(getHeight() / 2), 1);
        iov.model = outputModel;
        outputModel = (DeviceOutputModel) outputModel.next;
        iov = createOneIO(iov, 2, (short) -(getHeight() / 2), 3);
        iov.model = outputModel;
    }

}
