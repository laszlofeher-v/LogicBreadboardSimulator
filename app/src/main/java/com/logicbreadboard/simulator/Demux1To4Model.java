package com.logicbreadboard.simulator;

/**
 * Created by laci on 9/3/15.
 */
public class Demux1To4Model extends DemuxModel {
    @Override
    void createNextEvent() {
        DeviceInputModel x = (DeviceInputModel) firstInputModel;
        DeviceInputModel s1 = (DeviceInputModel) x.next;
        DeviceInputModel s0 = (DeviceInputModel) s1.next;
        DeviceOutputModel y3 = (DeviceOutputModel) firstOutputModel;
        DeviceOutputModel y2 = (DeviceOutputModel) y3.next;
        DeviceOutputModel y1 = (DeviceOutputModel) y2.next;
        DeviceOutputModel y0 = (DeviceOutputModel) y1.next;
        State.Level newOutput;
        newOutput = x.level;
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
        switch (selection) {
            case 0:
                newEvent(y0, newOutput);
                newEvent(y1, State.Level.low);
                newEvent(y2, State.Level.low);
                newEvent(y3, State.Level.low);
                break;
            case 1:
                newEvent(y0, State.Level.low);
                newEvent(y1, newOutput);
                newEvent(y2, State.Level.low);
                newEvent(y3, State.Level.low);
                break;
            case 2:
                newEvent(y0, State.Level.low);
                newEvent(y1, State.Level.low);
                newEvent(y2, newOutput);
                newEvent(y3, State.Level.low);
                break;
            case 3:
                newEvent(y0, State.Level.low);
                newEvent(y1, State.Level.low);
                newEvent(y2, State.Level.low);
                newEvent(y3, newOutput);
                break;
            default:
                if (x.level != State.Level.low) {
                    newOutput = State.Level.undefined;
                    newEvent(y0, newOutput);
                    newEvent(y1, newOutput);
                    newEvent(y2, newOutput);
                    newEvent(y3, newOutput);
                }
        }
    }
}