package com.logicbreadboard.simulator;

/**
 * Created by laci on 9/3/15.
 */
public class Demux1To2Model extends DemuxModel {
    @Override
    void createNextEvent() {
        DeviceInputModel x = (DeviceInputModel) firstInputModel;
        DeviceInputModel s0 = (DeviceInputModel) firstInputModel.next;
        DeviceOutputModel y0 = (DeviceOutputModel) firstOutputModel.next;
        DeviceOutputModel y1 = (DeviceOutputModel) firstOutputModel;
        State.Level newOutput;
        newOutput = x.level;
        switch (s0.level) {
            case low:
                newEvent(y0, newOutput);
                newEvent(y1, State.Level.low);
                break;
            case high:
                newEvent(y0, State.Level.low);
                newEvent(y1, newOutput);
                break;
            case undefined:
                if (x.level != State.Level.low) {
                    newOutput = State.Level.undefined;
                    newEvent(y0, newOutput);
                    newEvent(y1, newOutput);
                }
                break;
            default:
        }
    }
}
