package com.logicbreadboard.simulator;

import android.app.Activity;
import android.app.Dialog;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by laci on 7/29/15.
 */
public class LogicalButtonModel extends DeviceModel {
    State.Level level;
    State.Level initialLevel;

    public LogicalButtonModel() {
        super();
        delays = new int[1];
        delays[0] = 10;
    }

//    public void init(int numberOfIos) {
//        firstOutputModel = new DeviceOutputModel(this);
//    }

    @Override
    void reset() {
        super.reset();
        level = initialLevel;
        Event newEvent = EventFactory.newEvent(0);
        newEvent.sourceTime = newEvent.time;
//        newEvent.source = this.firstOutputModel;
        newEvent.deviceOutputModel = firstOutputModel;
        newEvent.level = initialLevel;
        newEvent.impedance = State.Impedance.low;
        Event.queue.add(newEvent);
    }

    public void changeLevel() {
        level = State.negate(level);
        Event newEvent = EventFactory.newEvent(Event.simulationTime + delays[0]);
//        newEvent.source = this.firstOutputModel;
        //   newEvent.time = Event.buttonEventTime++;
        newEvent.sourceTime = newEvent.time;
        newEvent.level = level;
        newEvent.deviceOutputModel = firstOutputModel;
        newEvent.impedance = State.Impedance.low;
        Event.buttonEventQueue.add(newEvent);
    }

    public void setLevel(State.Level newLevel) {
        level = newLevel;
        Event newEvent = EventFactory.newEvent(Event.simulationTime + delays[0]);
//        newEvent.source = this.firstOutputModel;
        //   newEvent.time = Event.buttonEventTime++;
        newEvent.sourceTime = newEvent.time;
        newEvent.level = level;
        newEvent.deviceOutputModel = firstOutputModel;
        newEvent.impedance = State.Impedance.low;
        Event.buttonEventQueue.add(newEvent);
    }

    @Override
    public void showDelayPopup(Activity activity) {
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.delay_popup_menu);
        dialog.setTitle("Set switch delay");
        Button tcoUp = (Button) dialog.findViewById(R.id.buttonUp);
        Button tcoDown = (Button) dialog.findViewById(R.id.buttonDown);
        final EditText tcoEditText = (EditText) dialog.findViewById(R.id.delay0);

        tcoEditText.setText(Integer.toString(delays[0]));

        TimeDialog.setListeners(tcoUp, tcoEditText, delays, 0, TimeDialog.Dirs.UP,1);
        TimeDialog.setListeners(tcoDown, tcoEditText, delays, 0, TimeDialog.Dirs.DOWN,1);
        dialog.show();
    }

}
