package com.logicbreadboard.simulator;

/**
 * Created on 4/8/16.
 */
public class BBResistorModel extends DeviceModel {
    public BBResistorModel() {
        super();
    }

    @Override
    void reset() {
        super.reset();
        checkConnections();
    }

    private void checkConnections() {
        // any output led
        if (singleLedConnected(firstOutputModel)) {
            return;
        }
        if (singleLedConnected((DeviceOutputModel) firstOutputModel.next)) {
            return;
        }
        if (checkOc(firstOutputModel)) {
            return;
        }
        if (checkOc(firstOutputModel.next)) {
            return;
        }

    }

    private boolean checkOc(DeviceIOModel riom) {
        DeviceIOModel iom;
        iom = riom.connected;
        while (iom != riom) {
            if (!(iom instanceof DeviceIOModel) && !iom.isOc()) {
                createFault(riom);
                return true;
            }
            iom = iom.connected;
        }
        return false;
    }

    private void createFault(DeviceIOModel iom) {
        FaultViewer.newFaultViewer(firstOutputModel, FaultViewer.FaultType.BADLY_PLACED_RESISTOR);
    }

    private boolean singleLedConnected(DeviceOutputModel outputModel) {
        return (outputModel.connected.model instanceof BBLEDModel) &&
                (outputModel.connected.connected == outputModel);
    }
    // open collector high


    @Override
    public void init(int numberOfInputs, int numberOfOutputs) {
        while (numberOfOutputs > 0) {
            ResistorOutputModel om = new ResistorOutputModel(this);
            om.next = firstOutputModel;
            firstOutputModel = om;
            numberOfOutputs--;
        }
    }

    @Override
    void createNextEvent() {
        DeviceIOModel driver0 = firstOutputModel.findDriver(DeviceIOModel.newPath++);
        DeviceIOModel driver1 = firstOutputModel.next.findDriver(DeviceIOModel.newPath++);
        if (driver0 == null && driver1 == null) {
            Event newEvent;
            int eventTime = Event.simulationTime + 1;
            newEvent = EventFactory.newEvent(eventTime);
            newEvent.sourceTime = eventTime;
            newEvent.level = State.Level.undefined;
            newEvent.impedance = State.Impedance.high;
            newEvent.deviceOutputModel = firstOutputModel;
            Event.queue.add(newEvent);
            newEvent = EventFactory.newEvent(eventTime);
            newEvent.sourceTime = eventTime;
            newEvent.level = State.Level.undefined;
            newEvent.impedance = State.Impedance.high;
            newEvent.deviceOutputModel = ((DeviceOutputModel) firstOutputModel.next);
            Event.queue.add(newEvent);
        } else {
            if (driver0 == null) {
                // TODO: 6/3/16 test
                if (firstOutputModel.level != State.Level.undefined) {
                    return;
                }
                if (firstOutputModel.level == firstOutputModel.next.level) {
                    return;
                }
                Event newEvent;
                int eventTime = Event.simulationTime + 1;
                newEvent = EventFactory.newEvent(eventTime);
                newEvent.sourceTime = eventTime;
                newEvent.level = firstOutputModel.next.level;
                newEvent.impedance = State.Impedance.weak;
                newEvent.deviceOutputModel = firstOutputModel;
                Event.queue.add(newEvent);
//                newEvent = EventFactory.newEvent(eventTime);
//                newEvent.sourceTime = eventTime;
//                newEvent.level = firstOutputModel.level;
//                newEvent.impedance = State.Impedance.high;
//                newEvent.deviceOutputModel = ((DeviceOutputModel) firstOutputModel.next);
//                Event.queue.add(newEvent);
            } else { //driver1==null
                // TODO: 6/3/16 test
                if (firstOutputModel.next.level != State.Level.undefined) {
                    return;
                }
                if (firstOutputModel.level == firstOutputModel.next.level) {
                    return;
                }
                Event newEvent;
                int eventTime = Event.simulationTime + 1;
//                newEvent = EventFactory.newEvent(eventTime);
//                newEvent.sourceTime = eventTime;
//                newEvent.level = firstOutputModel.next.level;
//                newEvent.impedance = State.Impedance.high;
//                newEvent.deviceOutputModel = firstOutputModel;
//                Event.queue.add(newEvent);
                newEvent = EventFactory.newEvent(eventTime);
                newEvent.sourceTime = eventTime;
                newEvent.level = firstOutputModel.level;
                newEvent.impedance = State.Impedance.weak;
                newEvent.deviceOutputModel = ((DeviceOutputModel) firstOutputModel.next);
                Event.queue.add(newEvent);
            }
        }
    }

}
