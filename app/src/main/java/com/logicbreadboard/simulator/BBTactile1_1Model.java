package com.logicbreadboard.simulator;

import android.app.Activity;
import android.app.Dialog;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created on 5/10/16.
 */
public class BBTactile1_1Model extends BBTactileModel {

    BBTactile1_1Model() {
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
//        if (firstOutputModel.impedance != State.Impedance.high) {
//            DeviceIOModel driver = firstOutputModel.findDriver(DeviceIOModel.newPath++);
//            Event newEvent = EventFactory.newEvent(lastInputChangeTime + delays[0]);
//            newEvent.sourceTime = Event.simulationTime;
//            newEvent.deviceOutputModel = firstOutputModel;
//            if (driver != null) {
//                newEvent.level = driver.level;
//            } else {
//                newEvent.level = State.Level.undefined;
//            }
//            newEvent.impedance = State.Impedance.high;
//            Event.queue.add(newEvent);
//        }
//        if (firstOutputModel.next.impedance != State.Impedance.high) {
//            DeviceIOModel driver = firstOutputModel.next.findDriver(DeviceIOModel.newPath++);
//            Event newEvent = EventFactory.newEvent(lastInputChangeTime + delays[0]);
//            newEvent.sourceTime = Event.simulationTime;
//            newEvent.deviceOutputModel = (DeviceOutputModel) firstOutputModel.next;
//            if (driver != null) {
//                newEvent.level = driver.level;
//            } else {
//                newEvent.level = State.Level.undefined;
//            }
//            newEvent.impedance = State.Impedance.high;
//            Event.queue.add(newEvent);
//        }
//        isOn = false;
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

    private void createNewEvent(DeviceIOModel driver0, DeviceOutputModel omUpper) {
        Event newEvent;
        newEvent = EventFactory.newEvent(Event.simulationTime + delays[0]);
        newEvent.sourceTime = Event.simulationTime;
        newEvent.level = driver0.level; // driver level will appear on the other output
        newEvent.impedance = driver0.impedance;
        newEvent.deviceOutputModel = omUpper;
        Event.queue.add(newEvent);
    }

    protected void onRT() {
        Event newEvent;
        DeviceIOModel driver0;
        int distance0;
        DeviceIOModel driver1;
        int distance1;
        DeviceBidirectionIOModel.pathLength = 0;
        DeviceOutputModel om0 = firstOutputModel;
        DeviceOutputModel om1 = (DeviceOutputModel) firstOutputModel.next;
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

    protected void off() {
        DeviceOutputModel omLower = firstOutputModel;
        DeviceOutputModel omUpper = (DeviceOutputModel) firstOutputModel.next;
//        if (omLower.impedance != State.Impedance.high) {
        DeviceIOModel driver = omLower.findDriver(DeviceIOModel.newPath++);
        Event newEvent = EventFactory.newEvent(lastInputChangeTime + delays[0]);
        newEvent.sourceTime = Event.simulationTime;
        newEvent.deviceOutputModel = omLower;
        if (driver != null) {
            newEvent.level = driver.level;
        } else {
            newEvent.level = State.Level.undefined;
        }
        newEvent.impedance = State.Impedance.high;
        Event.buttonEventQueue.add(newEvent);
//        }
//        if (omUpper.impedance != State.Impedance.high) {
        driver = omUpper.findDriver(DeviceIOModel.newPath++);
        newEvent = EventFactory.newEvent(lastInputChangeTime + delays[0]);
        newEvent.sourceTime = Event.simulationTime;
        newEvent.deviceOutputModel = (DeviceOutputModel) omUpper;
        if (driver != null) {
            newEvent.level = driver.level;
        } else {
            newEvent.level = State.Level.undefined;
        }
        newEvent.impedance = State.Impedance.high;
        Event.buttonEventQueue.add(newEvent);
//        }
        isOn = false;
    }

    protected void on() {
        Event newEvent;
        DeviceIOModel driver0;
        int distance0;
        DeviceIOModel driver1;
        int distance1;
        DeviceBidirectionIOModel.pathLength = 0;
        DeviceOutputModel om0 = firstOutputModel;
        DeviceOutputModel om1 = (DeviceOutputModel) firstOutputModel.next;
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
                    FaultViewer.newFaultViewer(om0, FaultViewer.FaultType.SHORT_CIRCUIT);
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
        isOn = true;
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
