package com.logicbreadboard.simulator;

/**
 * Created by laci on 9/1/15.
 */
public class Mux2To1Model extends MuxModel {

    @Override
    void createNextEvent() {
        DeviceInputModel s0 = (DeviceInputModel) firstInputModel.next.next;
        DeviceInputModel d0 = (DeviceInputModel) firstInputModel.next;
        DeviceInputModel d1 = (DeviceInputModel) firstInputModel;
        State.Level newOutput;
        switch (s0.level) {
            case low:
                newOutput = d0.level;
                break;
            case high:
                newOutput = d1.level;
                break;
            case undefined:
                if (d0.level != d1.level) {
                    newOutput = State.Level.undefined;
                } else {
                    newOutput = d0.level;
                }
                break;
            default:
                newOutput = State.Level.undefined;
        }
        if (!powered.isPowered) {
            newOutput = State.Level.undefined;
        }
        Event newEvent;
        int newTime = Event.simulationTime+delays[0];
        newEvent = EventFactory.newEvent(newTime);
//        newEvent.source = firstOutputModel;
        newEvent.sourceTime = lastInputChangeTime;
        newEvent.level = newOutput;
        newEvent.impedance = State.Impedance.low;
        newEvent.deviceOutputModel = firstOutputModel;
        Event.queue.add(newEvent);
    }
}
