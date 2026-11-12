package com.logicbreadboard.simulator;

/**
 * Created by laci on 6/23/15.
 */
public class DFlipFlopModel extends FlipFlopModel {
    private boolean dIsInverted = false;
    private boolean cIsInverted = false;
    private boolean rIsInverted = false;
    private boolean sIsInverted = false;
    //    private State.Level cPrev;
//    private State.Level dPrev;
//    private State.Level sPrev;
//    private State.Level rPrev;
    private State.Level internal;
    private int lastSampleTime;

    public DFlipFlopModel() {
        super();
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
        dIsInverted = p.inverted;
        p = p.next;
        cIsInverted = p.inverted;
        p = p.next;
        rIsInverted = p.inverted;
        p = p.next;
        sIsInverted = p.inverted;
    }

    @Override
    void createNextEvent() {
        DeviceInputModel sio;
        DeviceInputModel rio;
        DeviceInputModel cio;
        DeviceInputModel dio;
        State.Level newQ = firstOutputModel.level;
        int newTime;
        DeviceInputModel io = firstInputModel;
        sio = io;
        io = (DeviceInputModel) io.next;
        rio = io;
        io = (DeviceInputModel) io.next;
        cio = io;
        io = (DeviceInputModel) io.next;
        dio = io;
        State.Level c = cio.level;
        State.Level cPrevLevel=cio.prevLevel;
        State.Level d = dio.level;
        State.Level r = rio.level;
        State.Level s = sio.level;
        // TODO: 8/13/15 check times
        if (cIsInverted) {
            c = State.negate(c);
            cPrevLevel=State.negate(cPrevLevel);
        }
        if (dIsInverted) {
            d = State.negate(d);
        }
        if (rIsInverted) {
            r = State.negate(r);
        }
        if (sIsInverted) {
            s = State.negate(s);
        }
        if (r == State.Level.high) {
            if (s == State.Level.low) {
                internal = State.Level.low;
                newQ = internal;
                newTime = Event.simulationTime + delays[T_CO];
                createEvent(newQ, State.negate(newQ), newTime);
            } else if (s == State.Level.high) {
                internal = State.Level.high;
                newQ = internal;
                newTime = Event.simulationTime + delays[T_CO];
                createEvent(newQ, newQ, newTime);
            } else {
                internal = State.Level.undefined;
                newQ = internal;
                newTime = Event.simulationTime + delays[T_CO];
                createEvent(newQ, newQ, newTime);
            }
        } else if (r == State.Level.low) {
            if (s == State.Level.low) {
//                State.Level newQ;
//                int newTime;
                if (cio.lastChange == Event.simulationTime) { //clock changed
                    if (c == State.Level.high) {
                        lastSampleTime = Event.simulationTime;
                        if (rio.lastChange + delays[T_RECOVERY] > lastSampleTime) {
                            timingError(rio, FaultViewer.FaultType.T_RECOVERY);
                        }
                        if (sio.lastChange + delays[T_RECOVERY] > lastSampleTime) {
                            timingError(sio, FaultViewer.FaultType.T_RECOVERY);
                        }
                        if (dio.lastChange + delays[T_SETUP] > lastSampleTime) {
                            timingError(dio, FaultViewer.FaultType.T_SETUP);
                        }
                        if (cPrevLevel == State.Level.low) {
                            internal = d;
                            newQ = dio.level;
                            newTime = Event.simulationTime + delays[T_CO];
                            createEvent(newQ, State.negate(newQ), newTime);
                        } else { // u -> h
                            if (d != internal) {
                                internal = State.Level.undefined;
                                newQ = internal;
                                newTime = Event.simulationTime + delays[T_CO];
                                createEvent(newQ, State.negate(newQ), newTime);
                            }
                        }
                    } else if (c == State.Level.undefined) {
                        if (cPrevLevel == State.Level.low) { // l -> u
                            if (d != internal) {
                                internal = State.Level.undefined;
                                newQ = internal;
                                newTime = Event.simulationTime + delays[T_CO];
                                createEvent(newQ, State.negate(newQ), newTime);
                            }
                        } else { // h -> u
                        }
                    } else { // u->l or h->l
                    }
                }
            } else if (s == State.Level.high) {
                internal = State.Level.high;
                newQ = internal;
                newTime = Event.simulationTime + delays[T_CO];
                createEvent(newQ, State.negate(newQ), newTime);
            } else {
                internal = State.Level.undefined;
                newQ = internal;
                newTime = Event.simulationTime + delays[T_CO];
                createEvent(newQ, newQ, newTime);
            }
        }
        if (dio.lastChange == Event.simulationTime) {
            if (lastSampleTime + delays[T_HOLD] > dio.lastChange) {
                timingError(dio, FaultViewer.FaultType.T_HOLD);
            }
        }
        if (sio.lastChange == Event.simulationTime && s == State.Level.low) {
            if (lastSampleTime + delays[T_REMOVAL] > sio.lastChange) {
                timingError(sio, FaultViewer.FaultType.T_REMOVAL);
            }
        }
        if (rio.lastChange == Event.simulationTime && r == State.Level.low) {
            if (lastSampleTime + delays[T_REMOVAL] > rio.lastChange) {
                timingError(rio, FaultViewer.FaultType.T_REMOVAL);
            }
        }
    }

    void reset() {
        super.reset();
        lastInputChangeTime = 0;
        State.Level initLevel;
        lastSampleTime = -Math.max(delays[T_SETUP], delays[T_RECOVERY]) - 2;
        if (Settings.ffInit == State.Level.random) {
            initLevel = RandomGen.getRandomLevel();
        } else {
            initLevel = Settings.ffInit;
        }
        internal=initLevel;
        if (initLevel != State.Level.undefined) {
//            firstOutputModel.level=initLevel;
//            firstOutputModel.next.level = State.negate(initLevel);

            createEvent(initLevel, State.negate(initLevel),0);
//            Event.simulationTime + delays[T_CO]);
        }
    }
}
