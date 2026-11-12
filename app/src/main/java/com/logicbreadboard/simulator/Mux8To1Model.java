package com.logicbreadboard.simulator;

/**
 * Created by laci on 9/1/15.
 */
public class Mux8To1Model extends MuxModel {
    @Override
    void createNextEvent() {
        DeviceInputModel iom = firstInputModel;
        DeviceInputModel d7 = (DeviceInputModel) iom;
        iom = (DeviceInputModel) iom.next;
        DeviceInputModel d6 = (DeviceInputModel) iom;
        iom = (DeviceInputModel) iom.next;
        DeviceInputModel d5 = (DeviceInputModel) iom;
        iom = (DeviceInputModel) iom.next;
        DeviceInputModel d4 = (DeviceInputModel) iom;
        iom = (DeviceInputModel) iom.next;
        DeviceInputModel d3 = (DeviceInputModel) iom;
        iom = (DeviceInputModel) iom.next;
        DeviceInputModel d2 = (DeviceInputModel) iom;
        iom = (DeviceInputModel) iom.next;
        DeviceInputModel d1 = (DeviceInputModel) iom;
        iom = (DeviceInputModel) iom.next;
        DeviceInputModel d0 = (DeviceInputModel) iom;
        iom = (DeviceInputModel) iom.next;
        DeviceInputModel s2 = (DeviceInputModel) iom;
        iom = (DeviceInputModel) iom.next;
        DeviceInputModel s1 = (DeviceInputModel) iom;
        iom = (DeviceInputModel) iom.next;
        DeviceInputModel s0 = (DeviceInputModel) iom;
        iom = (DeviceInputModel) iom.next;
        int selection = 0;
        if (s0.level == State.Level.high) {
            selection += 1;
        } else if (s0.level == State.Level.undefined) {
            selection += UNDEFINED_SELECTION;
        }
        if (s1.level == State.Level.high) {
            selection += 2;
        } else if (s1.level == State.Level.undefined) {
            selection += UNDEFINED_SELECTION;
        }
        if (s2.level == State.Level.high) {
            selection += 4;
        } else if (s2.level == State.Level.undefined) {
            selection += UNDEFINED_SELECTION;
        }
        State.Level newOutput;
        switch (selection) {
            case 0:
                newOutput = d0.level;
                break;
            case 1:
                newOutput = d1.level;
                break;
            case 2:
                newOutput = d2.level;
                break;
            case 3:
                newOutput = d3.level;
                break;
            case 4:
                newOutput = d4.level;
                break;
            case 5:
                newOutput = d5.level;
                break;
            case 6:
                newOutput = d6.level;
                break;
            case 7:
                newOutput = d7.level;
                break;
            default:
                if (d0.level == d1.level && d0.level == d2.level && d0.level == d3.level &&
                        d0.level == d4.level && d0.level == d5.level && d0.level == d6.level &&
                        d0.level == d7.level) {
                    newOutput = d0.level;
                } else {
                    newOutput = State.Level.undefined;
                }
        }
        if (!powered.isPowered) {
            newOutput = State.Level.undefined;
        }
        Event newEvent;
        int newTime = Event.simulationTime + delays[0];
        newEvent = EventFactory.newEvent(newTime);
//        newEvent.source = firstOutputModel;
        newEvent.sourceTime = lastInputChangeTime;
        newEvent.level = newOutput;
        newEvent.impedance = State.Impedance.low;
        newEvent.deviceOutputModel = firstOutputModel;
        Event.queue.add(newEvent);
    }

}
