package com.logicbreadboard.simulator;

/**
 * Created by laci on 10/12/15.
 */
public class DeviceBidirectionIOModel extends DeviceOutputModel {
    static int pathLength;
    State.Level prevLevel;
    int lastChange;
    int path;

    DeviceBidirectionIOModel(DeviceModel model) {
        super(model);
        impedance = State.Impedance.high;
        path = 0;
    }

    @Override
    public DeviceIOModel setValue(Event event) {
        //assuming it is input. the createNextEvent in the model handles the output case
        if (Event.spikeWidth[0] > 0 && prevLevel != level && (event.time - lastChange) <= Event
                .spikeWidth[0]) {
            FaultViewer.newFaultViewer(this, FaultViewer.FaultType.SPIKE);
        }
        prevLevel = level;
        lastChange = event.time;
        this.level = event.level;
        DeviceModel.registrateModelForChange(this.model, event.time);
        return this.connected;
    }

//    @Override
//    public State.Impedance isDriver(int path) {
//        if (path == this.path) {
//            return State.Impedance.high;
//        }
//        this.path = path;
//        if (impedance == State.Impedance.high) {
//            return State.Impedance.high;
//        }
//        DeviceIOModel ioModel;
//        pathLength++;
//        if (this == model.firstOutputModel) {
//            ioModel = model.firstOutputModel.next.findDriver(path);
//        } else {
//            ioModel = model.firstOutputModel.findDriver(path);
//        }
//        if (ioModel != null && ioModel.level == level) {
//            return impedance;
//        }
//        return State.Impedance.high;
//    }
//
    void eventHandler(Event event) {
//        System.out.println("Handling event with time=" + event.time + " source time=" + event
//                .sourceTime + " " + event
//                .level + " " +
//                "net:" + event
//                .deviceOutputModel.netNumber);
//        System.out.flush();
        super.eventHandler(event);
    }

    @Override
    public boolean bidirectionalFault() {
        return model.bidirectionalFault();
    }

}
