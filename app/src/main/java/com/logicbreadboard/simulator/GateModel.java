package com.logicbreadboard.simulator;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

public class GateModel extends DeviceModel {

    public GateModel() {
        super();
        delays = new int[1];
        delays[0] = 10;
    }

    void addNextEvent(State.Level newOutput) {
        Event newEvent;
        newEvent = EventFactory.newEvent(lastInputChangeTime + delays[0]);
//        newEvent.source = this.firstOutputModel;
        newEvent.sourceTime = lastInputChangeTime;
        if (!powered.isPowered) {
            newOutput = State.Level.undefined;
        }
        newEvent.level = newOutput;
        newEvent.impedance = State.Impedance.low;
        newEvent.deviceOutputModel = firstOutputModel;
        Event.queue.add(newEvent);
    }

    void addNextEvent(State.Level newOutput, State.Impedance newImpedance) {
        Event newEvent;
        newEvent = EventFactory.newEvent(lastInputChangeTime + delays[0]);
//        newEvent.source = this.firstOutputModel;
        newEvent.sourceTime = lastInputChangeTime;
        newEvent.level = newOutput;
        newEvent.impedance = newImpedance;
        newEvent.deviceOutputModel = firstOutputModel;
        Event.queue.add(newEvent);
    }

    public DeviceIOModel plusOneInput() {
        DeviceIOModel im = firstInputModel;
        while (im.next != null) {
            im = im.next;
        }
        im.next = new DeviceInputModel(this);
        return im.next;
    }

    @Override
    public void removeInput(DeviceIOModel toBeRemoved) {
        DeviceIOModel iom = firstInputModel;
        if (iom == toBeRemoved) {
            firstInputModel = (DeviceInputModel) firstInputModel.next;
        } else {
            while (iom.next != toBeRemoved) {
                iom = iom.next;
            }
            iom.next = toBeRemoved.next;
        }
    }
//
//    public void init(int numberOfIos) {
//        while (numberOfIos > 1) {
//            DeviceInputModel im = new DeviceInputModel(this);
//            im.next = firstInputModel;
//            firstInputModel = im;
//            numberOfIos--;
//        }
//        firstOutputModel = new DeviceOutputModel(this);
//    }

    @Override
    public void showDelayPopup(final Activity activity) {
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.delay_popup_menu);
        dialog.setTitle("Set GATE delay");
        Button tcoUp = (Button) dialog.findViewById(R.id.buttonUp);
        Button tcoDown = (Button) dialog.findViewById(R.id.buttonDown);
        final EditText tcoEditText = (EditText) dialog.findViewById(R.id.delay0);
        tcoEditText.setText(Integer.toString(delays[0]));
        TimeDialog.setListeners(tcoUp, tcoEditText, delays, 0, TimeDialog.Dirs.UP,1);
        TimeDialog.setListeners(tcoDown, tcoEditText, delays, 0, TimeDialog.Dirs.DOWN,1);
        dialog.setOnDismissListener(new Dialog.OnDismissListener() {
            public void onDismiss(final DialogInterface dialog) {
                Log.i("showDelayPopup","dismissed");
                ((MainActivity) activity).editorViewer.model.selections.setAllTime(delays);
                ((MainActivity) activity).editorViewer.invalidate();
            }
        });
        dialog.show();
    }
}