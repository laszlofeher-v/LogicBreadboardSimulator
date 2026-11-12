package com.logicbreadboard.simulator;

/**
 * Created on 6/22/16.
 */
public class PowerConnectorModel extends DeviceModel {
    public PowerConnectorModel() {
    }

    @Override
    void createNextEvent() {
        boolean oldState = powered.isPowered;
        if (firstInputModel.level == State.Level.high &&
                firstInputModel.next.level == State.Level.low) {
            powered.isPowered = true;
        } else {
            powered.isPowered = false;
        }
        if (oldState != powered.isPowered) {
            powered.editorModel.registrateAllBut(Event.simulationTime, this);
        }
    }

    @Override
    void reset() {
        super.reset();
        powered.isPowered = false;
        firstInputModel.level = State.Level.undefined;
        firstInputModel.next.level = State.Level.undefined;
    }
}
