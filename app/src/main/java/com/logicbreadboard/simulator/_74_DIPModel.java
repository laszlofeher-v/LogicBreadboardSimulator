package com.logicbreadboard.simulator;

/**
 * Created on 3/23/16.
 */
public class _74_DIPModel extends DeviceModel {
    void addNextEvent(DeviceOutputModel output, State.Level newOutput, State.Impedance newImpedance, int lastInputChangeTime) {
        Event newEvent;
        newEvent = EventFactory.newEvent(lastInputChangeTime + delays[0]);
//        newEvent.source = this.firstOutputModel;
        newEvent.sourceTime = lastInputChangeTime;
        newEvent.level = newOutput;
        newEvent.impedance = newImpedance;
        newEvent.deviceOutputModel = output;
        Event.queue.add(newEvent);
    }

    void addOCNextEvent(DeviceOutputModel output, State.Level newOutput, State.Impedance newImpedance, int lastInputChangeTime) {
        Event newEvent;
        if (newOutput == State.Level.high) {
            newImpedance = State.Impedance.high;
        }
        newEvent = EventFactory.newEvent(lastInputChangeTime + delays[0]);
//        newEvent.source = this.firstOutputModel;
        newEvent.sourceTime = lastInputChangeTime;
        newEvent.level = newOutput;
        newEvent.impedance = newImpedance;
        newEvent.deviceOutputModel = output;
        Event.queue.add(newEvent);
    }

}
