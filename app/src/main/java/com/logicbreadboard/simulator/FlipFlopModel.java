package com.logicbreadboard.simulator;

import android.app.Activity;
import android.app.Dialog;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import java.util.Random;

/**
 * Created by laci on 6/23/15.
 */
public class FlipFlopModel extends DeviceModel {
    enum Times {TCO, TSETUP, THOLD, TRECOVERY, TREMOVAL, TWIDTH}

    enum Dirs {UP, DOWN}

    final static int T_CO = 0;
    final static int T_SETUP = 1;
    final static int T_HOLD = 2;
    final static int T_RECOVERY = 3;
    final static int T_REMOVAL = 4;
    static final int T_WIDTH = 1;

    FlipFlopModel() {
        super();
        delays = new int[5];
        delays[0] = 10;
        delays[1] = 2;
        delays[2] = 2;
        delays[3] = 2;
        delays[4] = 2;
    }

    @Override
    public void showDelayPopup(Activity activity) {
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.flip_flop_delay_popup_menu);
        dialog.setTitle("Set FF timing");
        Button tcoUp = (Button) dialog.findViewById(R.id.tcoUp);
        Button tcoDown = (Button) dialog.findViewById(R.id.tcoDown);
        Button tsetupUp = (Button) dialog.findViewById(R.id.tsetupUp);
        Button tsetupDown = (Button) dialog.findViewById(R.id.tsetupDown);
        Button tholdUp = (Button) dialog.findViewById(R.id.tholdUp);
        Button tholdDown = (Button) dialog.findViewById(R.id.tholdDown);
        Button trecoveryUp = (Button) dialog.findViewById(R.id.trecoveryUp);
        Button trecoveryDown = (Button) dialog.findViewById(R.id.trecoveryDown);
        Button tremovalUp = (Button) dialog.findViewById(R.id.tremovalUp);
        Button tremovalDown = (Button) dialog.findViewById(R.id.tremovalDown);
        final EditText tcoEditText = (EditText) dialog.findViewById(R.id.tco);
        final EditText setupEditText = (EditText) dialog.findViewById(R.id.tsetup);
        final EditText holdEditText = (EditText) dialog.findViewById(R.id.thold);
        final EditText recoveryEditText = (EditText) dialog.findViewById(R.id.trecovery);
        final EditText removalEditText = (EditText) dialog.findViewById(R.id.tremoval);

        tcoEditText.setText(Integer.toString(delays[T_CO]));
        setupEditText.setText(Integer.toString(delays[T_SETUP]));
        holdEditText.setText(Integer.toString(delays[T_HOLD]));
        recoveryEditText.setText(Integer.toString(delays[T_RECOVERY]));
        removalEditText.setText(Integer.toString(delays[T_REMOVAL]));

        TimeDialog.setListeners(tcoUp, tcoEditText, delays, T_CO, TimeDialog.Dirs.UP, 1);
        TimeDialog.setListeners(tcoDown, tcoEditText, delays, T_CO, TimeDialog.Dirs.DOWN, 1);
        TimeDialog.setListeners(tsetupUp, setupEditText, delays, T_SETUP, TimeDialog.Dirs.UP, 1);
        TimeDialog.setListeners(tsetupDown, setupEditText, delays, T_SETUP, TimeDialog.Dirs.DOWN, 1);
        TimeDialog.setListeners(tholdUp, holdEditText, delays, T_HOLD, TimeDialog.Dirs.UP, 1);
        TimeDialog.setListeners(tholdDown, holdEditText, delays, T_HOLD, TimeDialog.Dirs.DOWN, 1);
        TimeDialog.setListeners(trecoveryUp, recoveryEditText, delays, T_RECOVERY, TimeDialog.Dirs.UP, 1);
        TimeDialog.setListeners(trecoveryDown, recoveryEditText, delays, T_RECOVERY, TimeDialog.Dirs.DOWN, 1);
        TimeDialog.setListeners(tremovalUp, removalEditText, delays, T_REMOVAL, TimeDialog.Dirs.UP, 1);
        TimeDialog.setListeners(tremovalDown, removalEditText, delays, T_REMOVAL, TimeDialog.Dirs.DOWN, 1);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.show();
        dialog.getWindow().setAttributes(lp);

//        dialog.show();
    }

    protected void createEvent(State.Level newQ, State.Level newNQ, int newTime) {
        Event newEvent;
        newEvent = EventFactory.newEvent(newTime);
//        newEvent.source = firstOutputModel;
        newEvent.sourceTime = lastInputChangeTime;
        newEvent.level = newQ;
        newEvent.impedance = State.Impedance.low;
        newEvent.deviceOutputModel = firstOutputModel;
        Event.queue.add(newEvent);

        newEvent = EventFactory.newEvent(newTime);
//        newEvent.source = firstOutputModel.next;
        newEvent.sourceTime = lastInputChangeTime;
        if (!powered.isPowered) {
            newQ = State.Level.undefined;
        }
        newEvent.level = newNQ;
        newEvent.impedance = State.Impedance.low;
        newEvent.deviceOutputModel = (DeviceOutputModel) firstOutputModel.next;
        Event.queue.add(newEvent);
    }

    void timingError(DeviceIOModel io, FaultViewer.FaultType timing) {
        if (((timing == FaultViewer.FaultType.SHORT_CIRCUIT) ||
                (timing == FaultViewer.FaultType.SPIKE)) ||
                (Settings.ff_check && ((timing == FaultViewer.FaultType.T_SETUP) ||
                        (timing == FaultViewer.FaultType.T_HOLD) ||
                        (timing == FaultViewer.FaultType.T_RECOVERY) ||
                        (timing == FaultViewer.FaultType.T_REMOVAL)))) {
            FaultViewer.newFaultViewer(io, timing);
        }
    }

    void reset() {
        super.reset();
        lastInputChangeTime = 0;
    }

    protected static class RandomGen {
        static Random randomGenerator = new Random();

        public static State.Level getRandomLevel() {
            int randomInt = randomGenerator.nextInt(2);
            if (randomInt == 0) {
                return State.Level.low;
            } else {
                return State.Level.high;
            }
        }
    }

}
