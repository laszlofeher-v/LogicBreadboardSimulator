package com.logicbreadboard.simulator;

import android.app.Activity;
import android.app.Dialog;
import android.widget.Button;
import android.widget.EditText;

import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 * Created on 3/12/16.
 */
public class BBDIPSwModel extends DeviceModel {

    boolean[] isOn;

    BBDIPSwModel() {
        super();
        delays = new int[1];
        delays[0] = 1;
    }

    @Override
    public void init(int numberOfInputs, int numberOfOutputs) {
        isOn = new boolean[numberOfOutputs / 2];
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

    private DeviceOutputModel getDeviceOutputModel(int pos) {
        DeviceOutputModel om = firstOutputModel;
        for (int i = 0; i < pos; i++) {
            om = (DeviceOutputModel) om.next;
        }
        return om;
    }

    protected void offRT(int pos) {
        // Do nothing. ports are set
//        DeviceOutputModel omLower = getDeviceOutputModel(pos);
//        DeviceOutputModel omUpper = getDeviceOutputModel((isOn.length * 2 - pos - 1));
//        if (omLower.impedance != State.Impedance.high) {
//            DeviceIOModel driver = omLower.findDriver(DeviceIOModel.newPath++);
//            Event newEvent = EventFactory.newEvent(lastInputChangeTime + delays[0]);
//            newEvent.sourceTime = Event.simulationTime;
//            newEvent.deviceOutputModel = omLower;
//            if (driver != null) {
//                newEvent.level = driver.level;
//            } else {
//                newEvent.level = State.Level.undefined;
//            }
//            newEvent.impedance = State.Impedance.high;
//            Event.queue.add(newEvent);
//        }
//        if (omUpper.impedance != State.Impedance.high) {
//            DeviceIOModel driver = omUpper.findDriver(DeviceIOModel.newPath++);
//            Event newEvent = EventFactory.newEvent(lastInputChangeTime + delays[0]);
//            newEvent.sourceTime = Event.simulationTime;
//            newEvent.deviceOutputModel = (DeviceOutputModel) omUpper;
//            if (driver != null) {
//                newEvent.level = driver.level;
//            } else {
//                newEvent.level = State.Level.undefined;
//            }
//            newEvent.impedance = State.Impedance.high;
//            Event.queue.add(newEvent);
//        }
//        isOn[pos] = false;
    }

    private void createNewEvent(DeviceIOModel driver0, DeviceOutputModel omUpper) {
        Event newEvent;
        newEvent = EventFactory.newEvent(Event.simulationTime + delays[0]);
        newEvent.sourceTime = Event.simulationTime;
        newEvent.level = driver0.level; // driver level will appear on the other output
        newEvent.impedance = driver0.impedance;
        newEvent.deviceOutputModel = omUpper;
        Event.queue.add(newEvent);
    }


    protected void onRT(int pos) {
        Event newEvent;
        DeviceIOModel driver0;
        int distance0;
        DeviceIOModel driver1;
        int distance1;
        DeviceBidirectionIOModel.pathLength = 0;
        DeviceOutputModel om0 = getDeviceOutputModel(pos);
        DeviceOutputModel om1 = getDeviceOutputModel((isOn.length * 2 - pos - 1));
        driver0 = om0.findDriver(DeviceIOModel.newPath++); //search for low
        distance0 = DeviceBidirectionIOModel.pathLength;
        DeviceBidirectionIOModel.pathLength = 0;
        driver1 = om1.findDriver(DeviceIOModel.newPath++); //impedance drivers
        distance1 = DeviceBidirectionIOModel.pathLength;
        if (driver0 == null && driver1 == null) {
            createHighImpEvent(om0);
            createHighImpEvent(om1);
        } else if (driver0 != null && driver1 == null) {
            createNewEvent(driver0, om1);
        } else if (driver0 == null && driver1 != null) {
            createNewEvent(driver1, om0);
        } else if (driver0 != null && driver1 != null) {
            if (driver0.impedance == State.Impedance.low && driver1.impedance == State.Impedance.low) {
                if (driver0.level == driver1.level) {
                    om0.impedance = State.Impedance.low;
                    om1.impedance = State.Impedance.low;
                } else if (driver0.impedance == State.Impedance.low && driver1.impedance == State.Impedance.weak) {
                    createNewEvent(driver0, om1);
                } else if (driver0.impedance == State.Impedance.weak && driver1.impedance == State.Impedance.low) {
                    createNewEvent(driver1, om0);
                }
            } else if (driver0.impedance == State.Impedance.low && driver1.impedance == State.Impedance.weak) {
                createNewEvent(driver0, om1);
            } else if (driver0.impedance == State.Impedance.weak && driver1.impedance == State.Impedance.low) {
                createNewEvent(driver1, om0);
            } else if (driver0.impedance == State.Impedance.undefined && driver1.impedance == State.Impedance.low) {
                if (driver0.level == driver1.level) {
                    createNewEvent(driver1, om0);
                } else {
                    FaultViewer.newFaultViewer(om0, FaultViewer.FaultType.SHORT_CIRCUIT);
                }
            } else if (driver0.impedance == State.Impedance.low && driver1.impedance == State.Impedance.undefined) {
                if (driver0.level == driver1.level) {
                    createNewEvent(driver0, om1);
                } else {
                    FaultViewer.newFaultViewer(om0, FaultViewer.FaultType.SHORT_CIRCUIT);
                }
            } else if (driver0.impedance == State.Impedance.undefined && driver1.impedance == State.Impedance.weak) {
                if (driver0.level == driver1.level) {
                    createNewEvent(driver0, om1);
                } else {
                    createNewEvent(driver0, om1);
                }
            } else if (driver0.impedance == State.Impedance.weak && driver1.impedance == State.Impedance.undefined) {
                if (driver0.level == driver1.level) {
                    createNewEvent(driver1, om0);
                } else {
                    createNewEvent(driver1, om0);
                }
            }
        }
    }

    private void createHighImpEvent(DeviceOutputModel om0) {
        Event newEvent;
        newEvent = EventFactory.newEvent(Event.simulationTime + delays[0]);
        newEvent.sourceTime = Event.simulationTime;
        newEvent.level = State.Level.undefined; // driver level will appear on the other output
        newEvent.impedance = State.Impedance.high;
        newEvent.deviceOutputModel = om0;
        Event.queue.add(newEvent);
    }

    // called from ui
    protected void off(int pos) {
        DeviceOutputModel omLower = getDeviceOutputModel(pos);
        DeviceOutputModel omUpper = getDeviceOutputModel((isOn.length * 2 - pos - 1));
        Event newEvent = EventFactory.newEvent(lastInputChangeTime + delays[0]);
        newEvent.sourceTime = Event.simulationTime;
        newEvent.deviceOutputModel = omLower;
        newEvent.level = State.Level.undefined;
        newEvent.impedance = State.Impedance.high;
        Event.buttonEventQueue.add(newEvent);
        newEvent = EventFactory.newEvent(lastInputChangeTime + delays[0]);
        newEvent.sourceTime = Event.simulationTime;
        newEvent.deviceOutputModel = (DeviceOutputModel) omUpper;
        newEvent.level = State.Level.undefined;
        newEvent.impedance = State.Impedance.high;
        Event.buttonEventQueue.add(newEvent);
        isOn[pos] = false;
    }

    // called from ui
    protected void on(int pos) {
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
        DeviceOutputModel om0 = getDeviceOutputModel(pos);
        DeviceOutputModel om1 = getDeviceOutputModel((isOn.length * 2 - pos - 1));
        driver0 = om0.findDriver(DeviceIOModel.newPath++); //search for low
        distance0 = DeviceBidirectionIOModel.pathLength;
        DeviceBidirectionIOModel.pathLength = 0;
        driver1 = om1.findDriver(DeviceIOModel.newPath++); //impedance drivers
        distance1 = DeviceBidirectionIOModel.pathLength;
        if (driver0 == null && driver1 == null) {
            om0.impedance = State.Impedance.high;
            om1.impedance = State.Impedance.high;
        } else if (driver0 != null && driver1 == null) {
            createNewButtonEvent(driver0, om1);
        } else if (driver0 == null && driver1 != null) {
            createNewButtonEvent(driver1, om0);
        } else if (driver0 != null && driver1 != null) {
            if (driver0.impedance == State.Impedance.low && driver1.impedance == State.Impedance.low) {
                if (driver0.level == driver1.level) {
                    om0.impedance = State.Impedance.low;
                    om1.impedance = State.Impedance.low;
                } else if (driver0.impedance == State.Impedance.low && driver1.impedance == State.Impedance.weak) {
                    createNewButtonEvent(driver0, om1);
                } else if (driver0.impedance == State.Impedance.weak && driver1.impedance == State.Impedance.low) {
                    createNewButtonEvent(driver1, om0);
                }
            } else if (driver0.impedance == State.Impedance.low && driver1.impedance == State.Impedance.weak) {
                createNewButtonEvent(driver0, om1);
            } else if (driver0.impedance == State.Impedance.weak && driver1.impedance == State.Impedance.low) {
                createNewButtonEvent(driver1, om0);
            } else if (driver0.impedance == State.Impedance.undefined && driver1.impedance == State.Impedance.low) {
                if (driver0.level == driver1.level) {
                    createNewButtonEvent(driver1, om0);
                } else {
                    FaultViewer.newFaultViewer(om0, FaultViewer.FaultType.SHORT_CIRCUIT);
                }
            } else if (driver0.impedance == State.Impedance.low && driver1.impedance == State.Impedance.undefined) {
                if (driver0.level == driver1.level) {
                    createNewButtonEvent(driver0, om1);
                } else {
                    FaultViewer.newFaultViewer(firstOutputModel, FaultViewer.FaultType.SHORT_CIRCUIT);
                }
            } else if (driver0.impedance == State.Impedance.undefined && driver1.impedance == State.Impedance.weak) {
                if (driver0.level == driver1.level) {
                    createNewButtonEvent(driver0, om1);
                } else {
                    createNewButtonEvent(driver0, om1);
                }
            } else if (driver0.impedance == State.Impedance.weak && driver1.impedance == State.Impedance.undefined) {
                if (driver0.level == driver1.level) {
                    createNewButtonEvent(driver1, om0);
                } else {
                    createNewButtonEvent(driver1, om0);
                }
            }
        }
        isOn[pos] = true;
    }

    private void createNewButtonEvent(DeviceIOModel driver0, DeviceOutputModel omUpper) {
        Event newEvent;
        newEvent = EventFactory.newEvent(Event.simulationTime + delays[0]);
        newEvent.sourceTime = Event.simulationTime;
        newEvent.level = driver0.level; // driver level will appear on the other output
        newEvent.impedance = driver0.impedance;
        newEvent.deviceOutputModel = omUpper;
        Event.buttonEventQueue.add(newEvent);
    }

    @Override
    void reset() {
        super.reset();
        for (int pos = 0; pos < isOn.length; pos++) {
            if (isOn[pos]) {
                onRT(pos);
            } else {
                offRT(pos);
            }
        }
    }

    @Override
    void createNextEvent() {
// TODO: 6/12/16 ignore switches with no event
        for (int pos = 0; pos < isOn.length; pos++) {
            if (isOn[pos]) {
                onRT(pos);
            } else {
                offRT(pos);
            }
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

    public void save(DataOutputStream outputStream) throws java.io.IOException {
        for (int pos = 0; pos < isOn.length; pos++) {
            outputStream.writeBoolean(isOn[pos]);
        }
    }

    public void load(DataInputStream inputStream) throws java.io.IOException {
        for (int pos = 0; pos < isOn.length; pos++) {
            isOn[pos] = inputStream.readBoolean();
        }
    }
}
