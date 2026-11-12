package com.logicbreadboard.simulator;

/**
 * Created on 5/11/16.
 */
public abstract class BBTactileModel extends DeviceModel {
    boolean isOn = false;

    abstract protected void on();

    abstract protected void off();
}
