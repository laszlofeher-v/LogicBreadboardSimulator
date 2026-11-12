package com.logicbreadboard.simulator;

import android.app.Activity;
import android.app.Dialog;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created on 5/12/16.
 */
public class BBTactile2_2Model extends BBTactileModel {
    BBTactile2_2Model() {
        super();
        delays = new int[1];
        delays[0] = 1;
    }

    @Override
    public void init(int numberOfInputs, int numberOfOutputs) {
        while (numberOfInputs > 0) {
            DeviceInputModel im = new DeviceInputModel(this);
            im.next = firstInputModel;
            firstInputModel = im;
            numberOfInputs--;
        }
        while (numberOfOutputs > 0) {
            DeviceBidirectionIOModel om = new DeviceBidirectionIOModel(this);
            om.next = firstOutputModel;
            firstOutputModel = om;
            numberOfOutputs--;
        }
    }

    protected void offRT() {
        if (firstOutputModel.impedance != State.Impedance.high) {
            DeviceIOModel driver = firstOutputModel.findDriver(DeviceIOModel.newPath++);
            Event newEvent = EventFactory.newEvent(lastInputChangeTime + delays[0]);
            newEvent.sourceTime = Event.simulationTime;
            newEvent.deviceOutputModel = firstOutputModel;
            if (driver != null) {
                newEvent.level = driver.level;
            } else {
                newEvent.level = State.Level.undefined;
            }
            newEvent.impedance = State.Impedance.high;
            Event.queue.add(newEvent);
        }
        if (firstOutputModel.next.impedance != State.Impedance.high) {
            DeviceIOModel driver = firstOutputModel.next.findDriver(DeviceIOModel.newPath++);
            Event newEvent = EventFactory.newEvent(lastInputChangeTime + delays[0]);
            newEvent.sourceTime = Event.simulationTime;
            newEvent.deviceOutputModel = (DeviceOutputModel) firstOutputModel.next;
            if (driver != null) {
                newEvent.level = driver.level;
            } else {
                newEvent.level = State.Level.undefined;
            }
            newEvent.impedance = State.Impedance.high;
            Event.queue.add(newEvent);
        }
        isOn = false;
    }

    protected void onRT() {
//        if (firstOutputModel.impedance == State.Impedance.low && firstOutputModel.level ==
//                firstOutputModel.next.level) {
//            return;
//        }
        Event newEvent;
        DeviceIOModel driver0;
        int distance0;
        DeviceIOModel driver1;
        int distance1;
        DeviceBidirectionIOModel.pathLength = 0;
        driver0 = firstOutputModel.findDriver(DeviceIOModel.newPath++); //search for low
        distance0 = DeviceBidirectionIOModel.pathLength;
        DeviceBidirectionIOModel.pathLength = 0;
        driver1 = firstOutputModel.next.findDriver(DeviceIOModel.newPath++); //impedance drivers
        distance1 = DeviceBidirectionIOModel.pathLength;
        if (driver0 != null && ((distance0 <= distance1) || driver1 == null)) {
            if (firstOutputModel.level != driver0.level) {
                newEvent = EventFactory.newEvent(Event.simulationTime + delays[0]);
                newEvent.sourceTime = Event.simulationTime;
                newEvent.level = driver0.level; // driver level will appear on the other output
                newEvent.impedance = State.Impedance.low;
                newEvent.deviceOutputModel = (DeviceOutputModel) firstOutputModel.next;
                Event.queue.add(newEvent);
            }
        } else if (driver1 != null) { // found
            if (firstOutputModel.next.level != driver1.level) {
                newEvent = EventFactory.newEvent(Event.simulationTime + delays[0]);
                newEvent.sourceTime = Event.simulationTime;
                newEvent.level = driver1.level; // driver level will appear on the other output
                newEvent.impedance = State.Impedance.low;
                newEvent.deviceOutputModel = (DeviceOutputModel) firstOutputModel;
                Event.queue.add(newEvent);
            }
        } else { // not found, set high impedance and undefined level
            if (firstOutputModel.level != State.Level.undefined) {
                newEvent = EventFactory.newEvent(Event.simulationTime + delays[0]);
                newEvent.level = State.Level.undefined;
                newEvent.sourceTime = Event.simulationTime;
                newEvent.impedance = State.Impedance.high;
                newEvent.deviceOutputModel = (DeviceOutputModel) firstOutputModel;
                Event.queue.add(newEvent);
            }
            if (firstOutputModel.next.level != State.Level.undefined) {
                newEvent = EventFactory.newEvent(lastInputChangeTime + delays[0]);
                newEvent.sourceTime = Event.simulationTime;
                newEvent.level = State.Level.undefined;
                newEvent.impedance = State.Impedance.high;
                newEvent.deviceOutputModel = (DeviceOutputModel) firstOutputModel.next;
                Event.queue.add(newEvent);
            }
        }
        isOn = true;
    }

    protected void off() {
        if (firstOutputModel.impedance != State.Impedance.high) {
            DeviceIOModel driver = firstOutputModel.findDriver(DeviceIOModel.newPath++);
            Event newEvent = EventFactory.newEvent(lastInputChangeTime + delays[0]);
            newEvent.sourceTime = Event.simulationTime;
            newEvent.deviceOutputModel = firstOutputModel;
            if (driver != null) {
                newEvent.level = driver.level;
            } else {
                newEvent.level = State.Level.undefined;
            }
            newEvent.impedance = State.Impedance.high;
            Event.buttonEventQueue.add(newEvent);
        }
        if (firstOutputModel.next.impedance != State.Impedance.high) {
            DeviceIOModel driver = firstOutputModel.next.findDriver(DeviceIOModel.newPath++);
            Event newEvent = EventFactory.newEvent(lastInputChangeTime + delays[0]);
            newEvent.sourceTime = Event.simulationTime;
            newEvent.deviceOutputModel = (DeviceOutputModel) firstOutputModel.next;
            if (driver != null) {
                newEvent.level = driver.level;
            } else {
                newEvent.level = State.Level.undefined;
            }
            newEvent.impedance = State.Impedance.high;
            Event.buttonEventQueue.add(newEvent);
        }
        isOn = false;
    }

    protected void on() {
//        if (firstOutputModel.impedance == State.Impedance.low && firstOutputModel.level ==
//                firstOutputModel.next.level) {
//            return;
//        }
        Event newEvent;
        DeviceIOModel driver0;
        int distance0;
        DeviceIOModel driver1;
        int distance1;
        DeviceBidirectionIOModel.pathLength = 0;
        driver0 = firstOutputModel.findDriver(DeviceIOModel.newPath++); //search for low
        distance0 = DeviceBidirectionIOModel.pathLength;
        DeviceBidirectionIOModel.pathLength = 0;
        driver1 = firstOutputModel.next.findDriver(DeviceIOModel.newPath++); //impedance drivers
        distance1 = DeviceBidirectionIOModel.pathLength;
        if (driver0 != null && ((distance0 <= distance1) || driver1 == null)) {
            newEvent = EventFactory.newEvent(Event.simulationTime + delays[0]);
            newEvent.sourceTime = Event.simulationTime;
            newEvent.level = driver0.level; // driver level will appear on the other output
            newEvent.impedance = State.Impedance.low;
            newEvent.deviceOutputModel = (DeviceOutputModel) firstOutputModel.next;
            Event.buttonEventQueue.add(newEvent);
        } else if (driver1 != null) { // found
            newEvent = EventFactory.newEvent(Event.simulationTime + delays[0]);
            newEvent.sourceTime = Event.simulationTime;
            newEvent.level = driver1.level; // driver level will appear on the other output
            newEvent.impedance = State.Impedance.low;
            newEvent.deviceOutputModel = (DeviceOutputModel) firstOutputModel;
            Event.buttonEventQueue.add(newEvent);
        } else { // not found, set high impedance and undefined level
            newEvent = EventFactory.newEvent(Event.simulationTime + delays[0]);
            newEvent.level = State.Level.undefined;
            newEvent.sourceTime = Event.simulationTime;
            newEvent.impedance = State.Impedance.high;
            newEvent.deviceOutputModel = (DeviceOutputModel) firstOutputModel;
            Event.buttonEventQueue.add(newEvent);
            newEvent = EventFactory.newEvent(lastInputChangeTime + delays[0]);
            newEvent.sourceTime = Event.simulationTime;
            newEvent.level = State.Level.undefined;
            newEvent.impedance = State.Impedance.high;
            newEvent.deviceOutputModel = (DeviceOutputModel) firstOutputModel.next;
            Event.buttonEventQueue.add(newEvent);
        }
        isOn = true;
    }

    @Override
    void reset() {
        super.reset();
        offRT();
    }

    @Override
    void createNextEvent() {
        int time = lastInputChangeTime + delays[0];
        if (isOn) {
//            System.out.println("NMOS will off: " + time);
            onRT();
        } else {
            offRT();
        }
    }

    @Override
    public void showDelayPopup(Activity activity) {
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.delay_popup_menu);
        dialog.setTitle("Set Transistor delay");
        Button tcoUp = (Button) dialog.findViewById(R.id.buttonUp);
        Button tcoDown = (Button) dialog.findViewById(R.id.buttonDown);
        final EditText tcoEditText = (EditText) dialog.findViewById(R.id.delay0);
        tcoEditText.setText(Integer.toString(delays[0]));
        TimeDialog.setListeners(tcoUp, tcoEditText, delays, 0, TimeDialog.Dirs.UP, 1);
        TimeDialog.setListeners(tcoDown, tcoEditText, delays, 0, TimeDialog.Dirs.DOWN, 1);
        dialog.show();
    }

}
