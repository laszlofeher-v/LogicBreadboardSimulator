package com.logicbreadboard.simulator;

/**
 * Created on 8/12/15.
 */
public class LabelModel extends DeviceModel {
    public DeviceIOViewer iov;

    public LabelModel() {
    }

//    @Override
//    void reset() {
//        super.reset();
////        firstInputModel.level = State.Level.undefined;
////        ((DeviceInputModel) firstInputModel).lastChange = 0;
//    }

    @Override
    void createNextEvent() {
        if (Settings.displayWireLevel) {
            Event.displayEvent = true;
            Event.changedNetsAndLevels.add(iov.netNumber);
            Event.changedNetsAndLevels.add(iov.model.level.ordinal());
        }

    }

}
