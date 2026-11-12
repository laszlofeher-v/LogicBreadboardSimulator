package com.logicbreadboard.simulator;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created on 2/16/15.
 */
public class DeviceModel extends Model {

    //    static DeviceModel firstWithChangedInput;
//    static DeviceModel registrationListGuard;
//
//    static {
//        registrationListGuard = new DeviceModel();
//        firstWithChangedInput = registrationListGuard;
//    }
    static LinkedBlockingQueue<DeviceModel> registrationList = new LinkedBlockingQueue<>();

    //    DeviceModel nextWithChangedInput;
    DeviceInputModel firstInputModel;
    DeviceOutputModel firstOutputModel;
    int lastInputChangeTime;

    DeviceModel() {
        super();
    }

    public static void destroyRegistrationList() {
        registrationList.clear();
//        while (firstWithChangedInput != registrationListGuard) {
//            DeviceModel model = firstWithChangedInput;
//            firstWithChangedInput = firstWithChangedInput.nextWithChangedInput;
//            model.nextWithChangedInput = null;
//        }
    }

    public static void registrateModelForChange(DeviceModel model, int time) {
//        if (event.time > Event.simulationTime) { // do not set event.time
//            handleRegistratedModels();
//            Event.simulationTime = event.time;
//            Event.loadButtonEvents();
//        }
        model.lastInputChangeTime = time;
        if (!registrationList.contains(model)) {
            registrationList.add(model);
        }
//        if (model.nextWithChangedInput != null) {  //already registrated
//            return;
//        }
//        model.nextWithChangedInput = firstWithChangedInput;
//        firstWithChangedInput = model;
    }

    public static void handleRegistratedModels() {
//        System.out.println("ShortCircuitFault.checkCandidates");
        ShortCircuitFault.checkCandidates();
        while (true) {
            DeviceModel model = registrationList.poll();
            if (model == null) {
                break;
            }
            model.createNextEvent();
        }

//
//        while (firstWithChangedInput != registrationListGuard) {
//            DeviceModel model = firstWithChangedInput;
//            firstWithChangedInput = firstWithChangedInput.nextWithChangedInput;
//            model.nextWithChangedInput = null;
//            model.createNextEvent();
//        }
    }

    @Override
    public void init(int numberOfInputs, int numberOfOutputs) {
        while (numberOfInputs > 0) {
            DeviceInputModel im = new DeviceInputModel(this);
            im.next = firstInputModel;
            firstInputModel = im;
            numberOfInputs--;
        }
        while (numberOfOutputs > 0) {
            DeviceOutputModel om = new DeviceOutputModel(this);
            om.next = firstOutputModel;
            firstOutputModel = om;
            numberOfOutputs--;
        }
    }
//
//    void load() {
//
//    }
//
//    void save() {
//
//    }

    void reset() {
        lastInputChangeTime = 0;
        DeviceIOModel v = firstInputModel;
        while (v != null) {
            v.reset();
            v = v.next;
        }
        v = firstOutputModel;
        while (v != null) {
            v.reset();
            v = v.next;
        }
    }

    void reset(State.Level initLevel) {
        lastInputChangeTime = 0;
        DeviceIOModel v = firstInputModel;
        while (v != null) {
            v.reset(initLevel);
            v = v.next;
        }
        v = firstOutputModel;
        while (v != null) {
            v.reset(initLevel);
            v = v.next;
        }
    }

    void createNextEvent() {
    }

    public void addAt(int index, DeviceInputModel im) {
        if (index == 0) {
            im.next = firstInputModel;
            firstInputModel = im;
        } else {
            DeviceIOModel ioModel = firstInputModel;
            while (ioModel != null && index > 0) {
                ioModel = ioModel.next;
                index--;
            }
            im.next = ioModel.next;
            ioModel.next = im;
        }
    }

    @Override
    public boolean findNodeMsgHandler(FindNodeMsg msg) {
        return true; // the device has DeviceIOModel
    }

    public boolean bidirectionalFault() {
        DeviceIOModel driver = firstOutputModel.findDriver(DeviceIOModel.newPath++);
        if (driver != null) {
            State.Level level = driver.level;
            driver = firstOutputModel.next.findDriver(DeviceIOModel.newPath++);
            if (driver != null) {
                if (level != driver.level) {
                    return false;
                }
            }
        }
        return true;
    }

}