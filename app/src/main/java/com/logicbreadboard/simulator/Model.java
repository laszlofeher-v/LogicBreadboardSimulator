package com.logicbreadboard.simulator;

import android.app.Activity;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created on 12/18/14.
 */
public class Model {
    int[] delays;

    Model() {

    }

    void reset() {
    }

    Powered powered;

    public void connectPower(Powered powered) {
        this.powered = powered;
    }


    void createNextEvent() {
    }

    void init(int[] net, int[] delay) {

    }

    public boolean isWire() {
        return false;
    }

    void init(int numberOfInputs, int numberOfOutputs) {

    }

    public boolean findNodeMsgHandler(FindNodeMsg msg) {
        return false; // not processed
    }

    public void delete() {

    }

    public boolean isOc() {
        return false;
    }

    public void showDelayPopup(Activity activity) {
    }

    public void writeDelays(DataOutputStream outputStream) throws IOException {
        outputStream.writeInt(delays.length);
        for (int i = 0; i < delays.length; i++) {
            outputStream.writeInt(delays[i]);
        }
    }

    protected String setDelay(int index, int t) {
        String str = null;
        if (t <= 0) {
            str = new String("Wrong delay parameter:" + t + " changed to 1.");
            t = 1;
        }
        delays[index] = t;
        return str;
    }

    public String readDelays(DataInputStream inputStream) throws IOException {
        String str = null;
        int numberOfDelays = inputStream.readInt();
//        int  numberOfDelays = delays.length;
        if (numberOfDelays != delays.length) {
            str = new String("Expected number of delay parameter(s):" + delays.length + ". Read:" + numberOfDelays);
            if (numberOfDelays > delays.length && numberOfDelays < 16) {
                delays = new int[numberOfDelays];
            } else {
                return new String("Wrong delay in input file ");
            }
        }
        int i = 0;
        while (i < numberOfDelays) {
            int t = inputStream.readInt();
            String s = setDelay(i, t);
            if (str == null && s != null) {
                str = s;
            }
            i++;
        }
        while (i < delays.length) {   //when fewer delay parameter are in the file
            delays[i] = 1;
            i++;
        }
        return str;
    }

    public void removeInput(DeviceIOModel toBeRemoved) {
    }

    public void setInverted(DeviceIOViewer first) {
    }

    public void outputChanged(Event event) {
    }

    public int getNumberOfPorts() {
        return 0;
    }

    class FindNodeMsg extends Msg {
    }
}
