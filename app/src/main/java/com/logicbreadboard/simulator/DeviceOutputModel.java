package com.logicbreadboard.simulator;

public class DeviceOutputModel extends DeviceIOModel {
    static final short FIRST_OUTPUT_INDEX = 0;
    Event lastEvent; // Reset adds a reset event to each DeviceIOModel groups ?
    int netNumber;   // for wire coloring

    DeviceOutputModel(DeviceModel model) {
        super(model);
        impedance = State.Impedance.low;
    }

    public boolean isOutputModel() {
        return true;
    }

    public void setNetNumber(int netNumber) {
        this.netNumber = netNumber;
    }

    public void reset() {
        level = State.Level.undefined;
        if (lastEvent != null) {
            EventFactory.disposeEvent(lastEvent);
            lastEvent = null;
        }
    }

    @Override
    public boolean isOc() {
        return model.isOc();
    }

    @Override
    public State.Level getDriverLevel() {
        return level;
    }

    void eventHandler(Event event) {
//        System.out.println("Handling event with time="+event.time);
//        System.out.flush();
        if (event.time > Event.simulationTime) { // do not set event.time
            DeviceModel.handleRegistratedModels();
            Event.simulationTime = event.time;
            Event.loadButtonEvents();
        }
        model.outputChanged(event);
        if ((event.level != level) || (event.impedance != impedance)) { // event changes something
            if (lastEvent != null) {                                    // this is the first event
                if (event.sourceTime >= lastEvent.sourceTime) {         // sourcetime: when inputs and
                    EventFactory.disposeEvent(lastEvent);               // drop previous event
                    lastEvent = event;                                  // current is the last event
//                    Log.i("accept", "time=" + event.time + " level=" + event.level + " net=" + event.deviceOutputModel.netNumber);
                    propagateState(event);                      // set new level on boardConnection ios
                    if (Settings.displayWireLevel) {
                        Event.displayEvent = true;
                        Event.changedNetsAndLevels.add(netNumber);
                        Event.changedNetsAndLevels.add(event.level.ordinal());
                    }
                } else {
//                    System.out.println("Dropped outdated event time=" + event.time + " level=" + State.Level
//                            .values()
//                            [event.level.ordinal()] + " old level=" + State.Level.values()
//                            [level.ordinal()] + " imp=" + State.Impedance.values()[event.impedance.ordinal()] +
//                            " old imp=" + State.Impedance.values()[impedance.ordinal()] + " " + event
//                            .sourceTime + " < " + lastEvent.sourceTime);
//                    System.out.flush();
//                    Log.i("dropp", "xxxxxxxxxxxxxxx time=" + event.time + " level=" + event.level + " net=" + event
//                            .deviceOutputModel.netNumber);
                    EventFactory.disposeEvent(event);  // this event based on outdated information
                    // TODO hazard ?
                }
            } else {
                lastEvent = event;          // last event was null, nothing to compare and dispose
//                Log.i("accept", "time=" + event.time + " level=" + event.level + " net=" + event.deviceOutputModel.netNumber);
                propagateState(event);
                if (Settings.displayWireLevel) {
                    Event.displayEvent = true;
                    Event.changedNetsAndLevels.add(netNumber);
                    Event.changedNetsAndLevels.add(event.level.ordinal());
                }
            }
        } else {
//            System.out.println("Dropped no change event time=" + event.time + " level=" + State
//                    .Level.values()
//                    [event.level.ordinal()] + " old level=" + State.Level.values()
//                    [level.ordinal()] + " imp=" + State.Impedance.values()[event.impedance.ordinal()] +
//                    " old imp=" + State.Impedance.values()[impedance.ordinal()]);
//            System.out.flush();
//            Log.i("dropp", "xxxxxxxxxxxxxxx time=" +event.time +  " level=" + event.level + " net=" + event
//                    .deviceOutputModel.netNumber);
            EventFactory.disposeEvent(event); // nothing has been changed. this event must not be
            // stored as last event, because of hazard detection
        }
    }


}