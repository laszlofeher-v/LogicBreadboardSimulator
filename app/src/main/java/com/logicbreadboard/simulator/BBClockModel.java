package com.logicbreadboard.simulator;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created on 5/7/16.
 */
public class BBClockModel extends DeviceModel {
    static final int START_TIME_INDEX = 0;
    static final int END_TIME_INDEX = 1;
    static final int PERIOD_TIME_INDEX = 2;
    static final int DISABLED = 1;
    static final int L_TO_H = 2;
    static final int H_TO_L = 4;
    int state;

    public BBClockModel() {
        super();
        delays = new int[3];
        delays[START_TIME_INDEX] = 0;
        delays[END_TIME_INDEX] = 10000;
        delays[PERIOD_TIME_INDEX] = 100;
        state = DISABLED;
    }

    @Override
    void reset() {
        super.reset();
        State.Level firstLevel = State.Level.undefined;
        Event newEvent = EventFactory.newEvent(0);
        if ((state & DISABLED) != 0) {
            return;
        } else if ((state & L_TO_H) != 0) {
            firstLevel = State.Level.low;
        } else if ((state & H_TO_L) != 0) {
            firstLevel = State.Level.high;
        }
//        firstOutputModel.level= State.Level.low;
        newEvent.sourceTime = newEvent.time;
//        newEvent.source = this.firstOutputModel;
        newEvent.deviceOutputModel = firstOutputModel;
        newEvent.level = firstLevel;
        newEvent.impedance = State.Impedance.low;
        Event.queue.add(newEvent);
        newEvent = EventFactory.newEvent(delays[START_TIME_INDEX]);
        newEvent.sourceTime = newEvent.time;
//        newEvent.source = this.firstOutputModel;
        newEvent.deviceOutputModel = firstOutputModel;
        newEvent.level = State.negate(firstLevel);
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
        State.Level firstLevel = State.Level.undefined;
        if ((state & DISABLED) != 0) {
            return;
        } else if ((state & L_TO_H) != 0) {
            firstLevel = State.Level.low;
        } else if ((state & H_TO_L) != 0) {
            firstLevel = State.Level.high;
        }
        if (event.time == 0 && event.level == firstLevel) {
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

    protected String setDelay(int index, int t) {
        String str = null;
        if (t < 0) {
            str = new String("Wrong time parameter:" + t + " changed to 0.");
            t = 0;
        }
        delays[index] = t;
        return str;
    }

    @Override
    public void showDelayPopup(final Activity activity) {
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.clock_setting);
        dialog.setTitle("Set clock");
        dialog.setOnDismissListener(new Dialog.OnDismissListener() {
            public void onDismiss(final DialogInterface dialog) {
                ((MainActivity) activity).editorViewer.invalidate();
            }
        });
        Button startUp = (Button) dialog.findViewById(R.id.startUp);
        Button startDown = (Button) dialog.findViewById(R.id.startDown);
        Button endUp = (Button) dialog.findViewById(R.id.endUp);
        Button endtDown = (Button) dialog.findViewById(R.id.endDown);
        Button countUp = (Button) dialog.findViewById(R.id.countUp);
        Button countDown = (Button) dialog.findViewById(R.id.countDown);
        final EditText startEditText = (EditText) dialog.findViewById(R.id.startTime);
        final EditText endEditText = (EditText) dialog.findViewById(R.id.endTime);
        final EditText cycleTime = (EditText) dialog.findViewById(R.id.cycleTime);

        startEditText.setText(Integer.toString(delays[START_TIME_INDEX]));
        endEditText.setText(Integer.toString(delays[END_TIME_INDEX]));
        cycleTime.setText(Integer.toString(delays[PERIOD_TIME_INDEX]));

        TimeDialog.setListeners(startUp, startEditText, delays, START_TIME_INDEX, TimeDialog
                .Dirs.UP, 0);
        TimeDialog.setListeners(startDown, startEditText, delays, START_TIME_INDEX, TimeDialog
                .Dirs.DOWN, 0);
        TimeDialog.setListeners(endUp, endEditText, delays, END_TIME_INDEX, TimeDialog.Dirs.UP, 0);
        TimeDialog.setListeners(endtDown, endEditText, delays, END_TIME_INDEX, TimeDialog.Dirs
                .DOWN, 0);
        TimeDialog.setListeners(countUp, cycleTime, delays, PERIOD_TIME_INDEX, TimeDialog.Dirs
                .UP, 1);
        TimeDialog.setListeners(countDown, cycleTime, delays, PERIOD_TIME_INDEX, TimeDialog.Dirs
                .DOWN, 1);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    public void setHighImp() {
    }

    public void setLH() {
    }

    public void setHL() {
    }


}
