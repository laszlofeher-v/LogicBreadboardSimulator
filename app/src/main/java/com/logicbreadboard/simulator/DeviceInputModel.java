package com.logicbreadboard.simulator;

public class DeviceInputModel extends DeviceIOModel {
    State.Level prevLevel;
    int lastChange;

    DeviceInputModel(DeviceModel model) {
        super(model);
        impedance = State.Impedance.high;
    }

//    DeviceInputModel() {
//        super();
//        impedance = State.Impedance.high;
//    }

    @Override
    public DeviceIOModel setValue(Event event) {
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

    @Override
    public void reset() {
        level = prevLevel = State.Level.undefined;
        lastChange = 0;
    }

}