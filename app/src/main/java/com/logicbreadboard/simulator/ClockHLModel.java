package com.logicbreadboard.simulator;

/**
 * Created on 10/22/15.
 */
public class ClockHLModel extends ClockLHModel {
    @Override
    void reset() {
        super.reset();
//        firstOutputModel.level= State.Level.low;
        Event newEvent = EventFactory.newEvent(0);
        newEvent.sourceTime = newEvent.time;
//        newEvent.source = this.firstOutputModel;
        newEvent.deviceOutputModel = firstOutputModel;
        newEvent.level = State.Level.high;
        newEvent.impedance = State.Impedance.low;
        Event.queue.add(newEvent);
        newEvent = EventFactory.newEvent(delays[START_TIME_INDEX]);
        newEvent.sourceTime = newEvent.time;
//        newEvent.source = this.firstOutputModel;
        newEvent.deviceOutputModel = firstOutputModel;
        newEvent.level = State.Level.low;
        newEvent.impedance = State.Impedance.low;
        Event.queue.add(newEvent);
    }

    @Override
    void createNextEvent() {

    }

    @Override
    public void outputChanged(Event event) {
//        Log.i("outputChanged", "time=" + event.time + " level=" + event.level + " net=" + firstOutputModel
//                .netNumber);
        if (event.time == 0 && event.level == State.Level.high) {
            return; // this is the very first reset event
        }
        if (event.time > delays[END_TIME_INDEX]) {
            return;
        }
        Event newEvent = EventFactory.newEvent(event.time + delays[PERIOD_TIME_INDEX]);
        newEvent.sourceTime = newEvent.time;
//        newEvent.source = this.firstOutputModel;
        newEvent.deviceOutputModel = firstOutputModel;
        newEvent.impedance = State.Impedance.low;
        if (event.level == State.Level.high) {
            newEvent.level = State.Level.low;
        } else {
            newEvent.level = State.Level.high;
        }
        Event.queue.add(newEvent);
    }

}
