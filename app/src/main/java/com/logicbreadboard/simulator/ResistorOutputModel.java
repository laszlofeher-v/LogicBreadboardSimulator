package com.logicbreadboard.simulator;

/**
 * Created on 4/12/16.
 */
public class ResistorOutputModel extends DeviceOutputModel {
    State.Level prevLevel;
    int lastChange;

    ResistorOutputModel(DeviceModel model) {
        super(model);
        impedance = State.Impedance.high;
        level = prevLevel = State.Level.undefined;
        lastChange = 0;
    }

    @Override
    public DeviceIOModel setValue(Event event) {
        if (Event.spikeWidth[0] > 0 && prevLevel != level && (event.time - lastChange) <= Event
                .spikeWidth[0]) {
            FaultViewer.newFaultViewer(this, FaultViewer.FaultType.SPIKE);
        }
        if (level == event.level) {
            prevLevel = level;
        } else {
            if (event.level == State.Level.undefined) {
//                DeviceIOModel driver = findDriver(DeviceIOModel.newPath++);
//                if (driver != null) {
                prevLevel = level;
                lastChange = event.time;
                this.level = event.level;
                DeviceModel.registrateModelForChange(this.model, event.time);
//                }
            } else {
                prevLevel = level;
                lastChange = event.time;
                this.level = event.level;
                DeviceModel.registrateModelForChange(this.model, event.time);
            }
        }
        return this.connected;
    }

    @Override
    public State.Level getDriverLevel() {
        // this is the driver of the node because it has no low impedance output
        if (this == model.firstOutputModel) {
            return model.firstOutputModel.next.level;
        } else {
            return model.firstOutputModel.level;
        }
    }


    @Override
    public void reset() {
        super.reset();
        level = prevLevel = State.Level.undefined;
        impedance = State.Impedance.high;
        lastChange = 0;
    }

}
