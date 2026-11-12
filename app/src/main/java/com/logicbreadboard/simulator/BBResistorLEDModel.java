package com.logicbreadboard.simulator;

/**
 * Created on 4/10/16.
 */
public class BBResistorLEDModel extends BBLEDModel {
    public BBResistorLEDModel() {
//        viewer = led;
    }

    @Override
    void reset() {
        super.reset();
        state = LedState.OFF;
    }

    @Override
    void createNextEvent() {
        if (firstInputModel.level == State.Level.low && firstInputModel.next.level == State.Level.high) {
            state = LedState.ON;
        } else if (firstInputModel.level == firstInputModel.next.level && firstInputModel.level != State.Level.undefined) {
            state = LedState.OFF;
        } else if (firstInputModel.level == State.Level.high || firstInputModel.next.level == State.Level.low) {
            state = LedState.OFF;
        } else if (firstInputModel.level == State.Level.undefined || firstInputModel.next.level == State.Level.undefined) {
            DeviceIOModel driver0 = firstInputModel.findDriver(DeviceIOModel.newPath++);
            DeviceIOModel driver1 = firstInputModel.next.findDriver(DeviceIOModel.newPath++);
            if (driver0 == null || driver1 == null) {
                state = LedState.OFF;
            } else {
                state = LedState.UNDEFINED;
            }
        } else {
            state = LedState.UNDEFINED;
        }
        Event.displayEvent = true;
    }
}
