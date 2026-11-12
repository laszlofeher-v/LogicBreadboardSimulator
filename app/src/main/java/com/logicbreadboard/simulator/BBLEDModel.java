package com.logicbreadboard.simulator;

/**
 * Created on 4/11/16.
 */
public class BBLEDModel extends DeviceModel {
    enum LedState {
        ON, OFF, UNDEFINED, BURNING
    }

    LedState state;

    public BBLEDModel() {
//        viewer = led;
    }

    @Override
    void reset() {
        super.reset();
        state = LedState.OFF;
    }

    @Override
    void createNextEvent() {
        DeviceIOModel driver0 = firstInputModel.findDriver(DeviceIOModel.newPath++);
        DeviceIOModel driver1 = firstInputModel.next.findDriver(DeviceIOModel.newPath++);
        if (driver0 == null || driver1 == null) {
            state = LedState.OFF;
        } else if (driver0 != null && driver1 != null) {
            if (driver0.impedance == State.Impedance.low && driver1.impedance == State.Impedance.low) {
                if (firstInputModel.level == State.Level.low && firstInputModel.next.level == State.Level.high) {
                    state = LedState.BURNING;
                    FaultViewer.newFaultViewer(firstInputModel, FaultViewer.FaultType.LED_OVERCURRENT);
                } else if (firstInputModel.level == firstInputModel.next.level && firstInputModel.level != State.Level.undefined) {
                    state = LedState.OFF;
                } else if (firstInputModel.level == State.Level.high || firstInputModel.next.level == State.Level.low) {
                    state = LedState.OFF;
                } else {
                    state = LedState.UNDEFINED;
                }
            } else {
                if (firstInputModel.level == State.Level.low && firstInputModel.next.level == State.Level.high) {
                    state = LedState.ON;
                } else if (firstInputModel.level == firstInputModel.next.level && firstInputModel.level != State.Level.undefined) {
                    state = LedState.OFF;
                } else if (firstInputModel.level == State.Level.high || firstInputModel.next.level == State.Level.low) {
                    state = LedState.OFF;
                } else if (driver0 != null || driver1 != null) {
                    state = LedState.OFF;
                } else {
                    state = LedState.UNDEFINED;
                }
            }
            Event.displayEvent = true;
        }
    }
}