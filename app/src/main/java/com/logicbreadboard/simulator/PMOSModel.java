package com.logicbreadboard.simulator;

/**
 * Created by laci on 10/12/15.
 */
public class PMOSModel extends NMOSModel {
    @Override
    void createNextEvent() {
        int time = lastInputChangeTime + delays[0];
        if (firstInputModel.lastChange == Event.simulationTime) {
            if (firstInputModel.level == State.Level.low) {
                on();
            } else if (firstInputModel.level == State.Level.high) {
                off();
            } else if (firstInputModel.level == State.Level.undefined) {
                off();
            }
        } else {
            if (firstInputModel.level == State.Level.low) {
                onRT();
            } else if (firstInputModel.level == State.Level.high) {
                offRT();
            } else if (firstInputModel.level == State.Level.undefined) {
                offRT();
            }
        }
    }
}
