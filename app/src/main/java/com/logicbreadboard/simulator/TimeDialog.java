package com.logicbreadboard.simulator;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by laci on 9/16/15.
 */
public class TimeDialog {
    enum Dirs {UP, DOWN}

    protected static final int MAX_REPEAT_DELAY_MILLIS = 250;
    protected static final int MIN_REPEAT_DELAY_MILLIS = 5;
    private static int[] min = new int[32];

    static void swTime(int[] delayArray, int delayIndext, Dirs dir, TextView textView) {
        if (dir == Dirs.UP) {
            delayArray[delayIndext]++;
        } else {
            if (delayArray[delayIndext] > min[delayIndext]) {
                delayArray[delayIndext]--;
            }
        }
        textView.setText(Integer.toString(delayArray[delayIndext]));
    }

    static void setChangeListener(final EditText et, final int[] delayArray, final int pos) {
        et.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable editable) {
                int savedTime = delayArray[pos];
                int newTime;
//                Editable t = editable.getText();
                String ns = et.getText().toString();
                try {
                    newTime = Integer.parseInt(ns);
                } catch (NumberFormatException e) {
                    newTime = savedTime;
                    et.setText(Integer.toString(newTime));
                }
                if (newTime < min[pos]) {
                    newTime = min[pos];
                    et.setText(Integer.toString(newTime));
                }
                delayArray[pos] = newTime;
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                for (int i = start; i < s.toString().length(); i++) {
                    if (!Character.isDigit(s.charAt(i))) {
                        et.setText("");
                    }
                }
            }
        });
    }

    static void setListeners(Button button, final EditText textEdit, final int[] delayArray, final
    int delayIndex, final Dirs dir, int min) {
        TimeDialog.min[delayIndex] = min;
        setChangeListener(textEdit, delayArray, delayIndex);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                swTime(delayArray, delayIndex, dir, textEdit);
            }

        });
        button.setOnTouchListener(new View.OnTouchListener() {
            Handler mHandler;
            int count = 0;
            Runnable mAction = new Runnable() {
                @Override
                public void run() {
                    count++;
                    int delay = MAX_REPEAT_DELAY_MILLIS - count * 10;
                    if (delay < MIN_REPEAT_DELAY_MILLIS) {
                        delay = MIN_REPEAT_DELAY_MILLIS;
                    }
                    swTime(delayArray, delayIndex, dir, textEdit);
                    mHandler.postDelayed(this, delay);
                }
            };

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (mHandler != null) {
                            return true;
                        }
                        mHandler = new Handler();
                        mHandler.postDelayed(mAction, MAX_REPEAT_DELAY_MILLIS);
                        break;
                    case MotionEvent.ACTION_UP:
                        count = 0;
                        if (mHandler == null) {
                            return true;
                        }
                        mHandler.removeCallbacks(mAction);
                        mHandler = null;
                        break;
                }
                return false;
            }
        });
    }

}
