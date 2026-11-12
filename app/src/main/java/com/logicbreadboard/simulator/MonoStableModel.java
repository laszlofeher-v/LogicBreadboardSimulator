package com.logicbreadboard.simulator;

import android.app.Activity;
import android.app.Dialog;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by laci on 8/15/15.
 */
public class MonoStableModel extends FlipFlopModel {
    private boolean tIsInverted = false;
    private State.Level internal;
    private State.Level tPrev;

    public MonoStableModel() {
        super();
        delays = new int[2];
        delays[0] = 10;
        delays[1] = 100;

        internal = State.Level.low;
    }

//    public void init(int numberOfIos) {
//        while (numberOfIos > 2) {
//            DeviceInputModel im = new DeviceInputModel(this);
//            im.next = firstInputModel;
//            firstInputModel = im;
//            numberOfIos--;
//        }
//        firstOutputModel = new DeviceOutputModel(this);
//        firstOutputModel.next = new DeviceOutputModel(this);
//    }

    public void setInverted(DeviceIOViewer first) {
        DeviceIOViewer p = first.next.next;
        tIsInverted = p.inverted;
    }

    @Override
    void createNextEvent() {
        State.Level newQ;
        int newTime;
        DeviceIOModel tio = firstInputModel;
        State.Level t = tio.level;
        // TODO: 8/13/15 check times
        if (tIsInverted) {
            t = State.negate(t);
        }
        if (t == State.Level.high && internal == State.Level.low) {
            internal = t;
            newQ = t;
            newTime = Event.simulationTime + delays[T_CO];
            createEvent(newQ, State.negate(newQ), newTime);
            newQ = State.negate(newQ);
            newTime = Event.simulationTime + delays[T_CO] + delays[T_WIDTH];
            lastInputChangeTime++; // prevent event dropping.
            createEvent(newQ, State.negate(newQ), newTime);
        } else if (t == State.Level.low) {
            internal = t;
        }
        tPrev = t;
    }

    void reset() {
        super.reset();
        internal = State.Level.low;
        int newTime = Event.simulationTime + 1;
        State.Level newQ = State.Level.undefined;
        createEvent(newQ, State.negate(newQ), newTime);
    }

    @Override
    public void showDelayPopup(Activity activity) {
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.monostabil_delay_popup_menu);
        dialog.setTitle("Set timing");
        Button tcoUp = (Button) dialog.findViewById(R.id.tcoUp);
        Button tcoDown = (Button) dialog.findViewById(R.id.tcoDown);
        Button twidthUp = (Button) dialog.findViewById(R.id.twidthup);
        Button twidthDown = (Button) dialog.findViewById(R.id.twidthdown);
        final EditText tcoEditText = (EditText) dialog.findViewById(R.id.tco);
        final EditText twidthEditText = (EditText) dialog.findViewById(R.id.twidth);

        tcoEditText.setText(Integer.toString(delays[T_CO]));
        twidthEditText.setText(Integer.toString(delays[T_WIDTH]));

        TimeDialog.setListeners(tcoUp, tcoEditText, delays, T_CO, TimeDialog.Dirs.UP, 1);
        TimeDialog.setListeners(tcoDown, tcoEditText, delays, T_CO, TimeDialog.Dirs.DOWN, 1);
        TimeDialog.setListeners(twidthUp, twidthEditText, delays, T_SETUP, TimeDialog.Dirs.UP, 1);
        TimeDialog.setListeners(twidthDown, twidthEditText, delays, T_SETUP, TimeDialog.Dirs
                .DOWN, 1);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.show();
        dialog.getWindow().setAttributes(lp);

//        dialog.show();
    }

}
