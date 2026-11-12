package com.logicbreadboard.simulator;

import android.app.Activity;
import android.app.Dialog;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by laci on 9/1/15.
 */
public class MuxModel extends DeviceModel {
    final static int UNDEFINED_SELECTION = 100000; // more than the number of inputs of any real mux

    final static int T_DATA = 0;
    final static int T_SEL = 1;
//    final static int T_STR = 2;


    MuxModel() {
        super();
        delays = new int[1];
        delays[0] = 10;
    }

    @Override
    public void showDelayPopup(Activity activity) {
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.delay_popup_menu);
        dialog.setTitle("Set MUX delay");
        Button tcoUp = (Button) dialog.findViewById(R.id.buttonUp);
        Button tcoDown = (Button) dialog.findViewById(R.id.buttonDown);
        final EditText tcoEditText = (EditText) dialog.findViewById(R.id.delay0);
        tcoEditText.setText(Integer.toString(delays[0]));
        TimeDialog.setListeners(tcoUp, tcoEditText, delays, 0, TimeDialog.Dirs.UP,1);
        TimeDialog.setListeners(tcoDown, tcoEditText, delays, 0, TimeDialog.Dirs.DOWN,1);
        dialog.show();
    }

}
