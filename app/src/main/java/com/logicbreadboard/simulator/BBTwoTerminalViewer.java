package com.logicbreadboard.simulator;

/**
 * Created on 3/20/16.
 */
public class BBTwoTerminalViewer extends InvisibleDeviceViewer {
    int startX;
    int startY;
    int endX;
    int endY;
    int color;
    float ox;
    float oy;
    float z;
    static private final int CONTEXT_MENU_MASK =
            ContextMenuIcons.DELETE |
                    ContextMenuIcons.UNSELECT;

    @Override
    public int getContextMenuMask() {
        return CONTEXT_MENU_MASK;
    }

    public BBTwoTerminalViewer() {
        super();
        left = startX;
        right = endX;
        top = startY;
        bottom = endY;
        color = Context.bbWirePaint.getColor();
    }

    public boolean isRoutable() {
        return false;
    }

    protected void calc3d() {
        ox = (startX + endX) / 2;
        oy = (startY + endY) / 2;
        float dx = startX - endX;
        float dy = startY - endY;
        float s = (float) Math.sqrt(dx * dx + dy * dy);
        z = 2 + 1024 / s;
    }

    @Override
    public boolean setHolesUsed(EditorViewer editorViewer) {
        if (!editorViewer.setHoleUsed(startX, startY, firstIoViewer) ||
                !editorViewer.setHoleUsed(endX, endY, firstIoViewer.next)) {
            return false;
        }
        return true;
    }

    @Override
    public void delete(EditorViewer editorViewer) {
        editorViewer.setHoleFree(startX, startY);
        editorViewer.setHoleFree(endX, endY);
    }

    @Override
    public Viewer deleteAt(int x, int y, BreadBoardViewer breadBoardViewer) {
        if ((startX == x && startY == y) || (endX == x && endY == y)) {
            breadBoardViewer.setHoleFree(startX, startY);
            breadBoardViewer.setHoleFree(endX, endY);
            breadBoardViewer.propagateNetNumberAt(startX, startY, NetNumbers.get());
            breadBoardViewer.propagateNetNumberAt(endX, endY, NetNumbers.get());
            return this;
        }
        return null;
    }

    @Override
    public Viewer selectAt(int x, int y, BreadBoardViewer breadBoardViewer) {
        if ((startX == x && startY == y) || (endX == x && endY == y)) {
            return this;
        }
        return null;
    }


    protected void createInputs() {
        InvisibleDeviceIOViewer iov = new InvisibleDeviceIOViewer((short) (startX - left), (short) (startY - top),
                (short) (short) (startX - left), (short) (startY - top), false);
        iov.model = ((DeviceModel) model).firstInputModel;
        firstIoViewer = iov;
        iov = new InvisibleDeviceIOViewer((short) (endX - right), (short) (endY - bottom),
                (short) (endX - right), (short) (endY - bottom), false);
        iov.model = ((DeviceModel) model).firstInputModel.next;
        firstIoViewer.next = iov;
    }

    @Override
    public void setNumberOfIos(short numberOfIos) {
        this.numberOfIos = numberOfIos;
        model.init(numberOfIos, 0);
        createInputs();
    }

    @Override
    public void setDefaultNumberOfIos() {
        setNumberOfIos((short) 2);
    }

}
