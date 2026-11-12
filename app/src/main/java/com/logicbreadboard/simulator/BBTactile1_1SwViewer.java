package com.logicbreadboard.simulator;

/**
 * Created on 5/10/16.
 */
public class BBTactile1_1SwViewer extends BBTactileSwViewer {
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

    @Override
    public short getSymbol() {
        return (short) BBViewerFactory.BB_Switch.Tactile1_1.ordinal();
    }

    public int getWidth() {
        return BOX_WIDTH;
    }

    public int getHeight() {
        return BOX_HEIGHT;
    }

    BBTactile1_1SwViewer() {
        model = new BBTactile1_1Model();
    }

    @Override
    public int getContextMenuMask() {
        return CONTEXT_MENU_MASK;
    }

    @Override
    protected void createAllInputs() {
        DeviceIOViewer iov = firstIoViewer;
        DeviceOutputModel outputModel = ((DeviceModel) model).firstOutputModel;
        iov = createOneIO(iov, 1, (short) (getHeight() / 2), 2);
        iov.model = outputModel;
        iov = createOneIO(iov, 1, (short) -(getHeight() / 2), 2);
        iov.model = outputModel.next;
    }

    @Override
    public void setNumberOfIos(short numberOfIos) {
        this.numberOfIos = numberOfIos;
        model.init(numberOfIos - 2, 2);
        createAllInputs();
    }

    @Override
    public void setDefaultNumberOfIos() {
        setNumberOfIos((short) 2);
    }

}
