package com.logicbreadboard.simulator;

import android.util.Log;

/**
 * Created on 2/16/15.
 */
public class DeviceIOModel {
    static int newPath = 1;
    DeviceIOModel connected;
    //    Event lastEvent; // Reset adds a reset event to each DeviceIOModel groups ?
    State.Level level;
    State.Impedance impedance;
    DeviceModel model;
    DeviceIOModel next;

    DeviceIOModel(DeviceModel model) {
        level = State.Level.undefined;
        connected = this;
        this.model = model;
    }

    public boolean isOutputModel() {
        return false;
    }

    public void setNetNumber(int netNumber) {
    }

    public boolean isOc() {
        return false;
    }

    public void reset() {
        level = State.Level.undefined;
    }

    public void reset(State.Level initLevel) {
        level = initLevel;
    }

    boolean isConnected(DeviceIOModel net) {
        if (net == null) {
            return false;
        }
        DeviceIOModel n = net.connected;
        while (n != net) {
            if (n == this) {
                return true;
            }
            n = n.connected;
        }
        return this == net;
    }

    void connectTo(DeviceIOModel net) {
        if (isConnected(net)) {
            return;
        }
        DeviceIOModel n = net;
        while (n.connected != net) {
            n = n.connected;
        }
        n.connected = this.connected;
        this.connected = net;
    }

    void disConnect() {
        DeviceIOModel n = this;
        while (n.connected != this) {
            n = n.connected;
        }
        n.connected = this.connected;
        this.connected = this;
    }

    DeviceIOModel findDriver(int path) {
        State.Impedance driverStrength = State.Impedance.high;
        DeviceIOModel deviceIoModel = connected;
        DeviceIOModel foundWeak = null;
        DeviceIOModel foundUndefined = null;
        while (deviceIoModel != this) {
            driverStrength = deviceIoModel.isDriver(path);
            switch (driverStrength) {
                case low:
                    return deviceIoModel;
                case undefined:
                    foundUndefined = deviceIoModel;
                    break;
                case weak:
                    foundWeak = deviceIoModel;
                    break;
                default:
            }
            deviceIoModel = deviceIoModel.connected;
        }
        if (foundUndefined != null) {
            return foundUndefined;
        }
        if (foundWeak != null) {
            return foundWeak;
        }
        return null;
    }

    public State.Impedance isDriver(int path) {
        return impedance;
    }

    void propagateState(Event event) {
        DeviceIOModel driver;
        // TODO: 10/13/15  remove deviceIoModel because this==deviceIoModel
        DeviceIOModel deviceIoModel = event.deviceOutputModel;
        deviceIoModel.level = event.level;
        deviceIoModel.impedance = event.impedance;
        if (event.impedance == State.Impedance.high) {
            driver = findDriver(newPath++);
            if (driver != null) {
                event.level = driver.getDriverLevel();
                event.impedance = State.Impedance.low;
                deviceIoModel.level = driver.getDriverLevel();
            }
        } else if (event.impedance == State.Impedance.weak) {
            driver = findDriver(newPath++);
            if (driver != null && (driver.impedance == State.Impedance.low || driver.impedance == State.Impedance.undefined)) {
                event.level = driver.getDriverLevel();
                event.impedance = State.Impedance.low;
                deviceIoModel.level = driver.getDriverLevel();
            }

        }
        deviceIoModel = deviceIoModel.connected;
        while (deviceIoModel != event.deviceOutputModel) {
            deviceIoModel = deviceIoModel.setValue(event);
        }
    }

    public DeviceIOModel setValue(Event event) {
        if (event.impedance == State.Impedance.low) {
            if (this.impedance == State.Impedance.high) {
                this.level = event.level;
                return this.connected;
            } else if (this.impedance == State.Impedance.weak) {
                this.level = event.level;
                return this.connected;
            } else { // this.impedance is low or undefined
                if (this.level == State.Level.undefined || this.level != event.level) {
                    ShortCircuitFault.addCandidate(this);
                    return this.connected;
                }
            }
        } else if (event.impedance == State.Impedance.weak) {
            if (this.impedance == State.Impedance.high) {
                this.level = event.level;
                return this.connected;
            } else if (this.impedance == State.Impedance.weak) {
                if (this.level != event.level) {
                    this.level = State.Level.undefined;
                } else {
                    this.level = event.level;
                }
                return this.connected;
            } else if (this.impedance == State.Impedance.low || this.impedance == State.Impedance.undefined) {
                Log.i("DEVICEIOMODEL_SETVALUE", "Error: badly selected driver");
            } else { // this.impedance is undefined
                if (this.level == State.Level.undefined || this.level != event.level) {
                    ShortCircuitFault.addCandidate(this);
                    return this.connected;
                }
            }
        } else if (event.impedance == State.Impedance.undefined) {
            if (this.impedance == State.Impedance.high) {
                this.level = event.level;
                return this.connected;
            } else {
                if (this.level == State.Level.undefined || this.level != event.level) {
                    ShortCircuitFault.addCandidate(this);
                    return this.connected;
                }
            }
        }
        this.level = event.level;
        return this.connected;
    }

    boolean getinvertedFlag() {
        return false;
    }

    void setInvertedFlag(boolean inverted) {
    }

    public boolean bidirectionalFault() {
        return false;
    }

    public State.Level getDriverLevel() {
        return level;
    }
}
