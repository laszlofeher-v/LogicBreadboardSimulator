package com.logicbreadboard.simulator;

/**
 * Created by laci on 6/23/15.
 */
public class BufferTSHAnsiViewer extends BufferAnsiViewer {
    BufferTSHAnsiViewer() {
        model = new BufferTSHModel();
    }

    @Override
    public void setDefaultNumberOfIos() {
        setNumberOfIos((short) 3);
    }

    @Override
    public short getSymbol() {
        return (short) SchematicViewerFactory.Gates.Buffer_TS_H.ordinal();
    }

    @Override
    public void setNumberOfIos(short numberOfIos) {
        this.numberOfIos = numberOfIos;
        model.init(numberOfIos - 1, 1);
        DeviceIOViewer iov = new DeviceIOViewer((short) (-GATE_WIDTH / 2), (short) 0, (short)
                (-GATE_WIDTH /
                        2 - DeviceIOViewer.LENGTH), (short) 0, false);
        firstIoViewer = iov;
        int halfHeight = GATE_HEIGHT / 2;
        iov = new DeviceIOViewer((short) 0, (short) (halfHeight / 2), (short) 0, (short) (
                halfHeight + CLEARANCE), false);
        iov.next = firstIoViewer;
        firstIoViewer = iov;
        DeviceIOViewer firstOutputViewer = new DeviceIOViewer((short) (GATE_WIDTH / 2), (short) 0,
                (short) (GATE_WIDTH / 2 + DeviceIOViewer
                        .LENGTH), (short) 0, false);
        firstOutputViewer.next = firstIoViewer;
        firstIoViewer = firstOutputViewer;

        DeviceIOModel ioModel = ((DeviceModel) model).firstInputModel;
        firstIoViewer.next.model = ioModel;
        firstIoViewer.next.next.model = ioModel.next;
        firstIoViewer.model = ((DeviceModel) model).firstOutputModel;
    }

}
