package com.logicbreadboard.simulator;

/**
 * Created by laci on 8/10/15.
 */
public class JKFlipFlopModel extends FlipFlopModel {
    private boolean jIsInverted = false;
    private boolean kIsInverted = false;
    private boolean cIsInverted = false;
    private boolean rIsInverted = false;
    private boolean sIsInverted = false;
    //    private State.Level cPrev;
//    private State.Level jPrev;
//    private State.Level kPrev;
//    private State.Level sPrev;
//    private State.Level rPrev;
    private State.Level internal;
    private int lastSampleTime;


    public JKFlipFlopModel() {
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
        cIsInverted = p.inverted;
        p = p.next;
        jIsInverted = p.inverted;
        p = p.next;
        kIsInverted = p.inverted;
        p = p.next;
        rIsInverted = p.inverted;
        p = p.next;
        sIsInverted = p.inverted;
    }

    @Override
    void createNextEvent() {
        DeviceInputModel sio;
        DeviceInputModel rio;
        DeviceInputModel kio;
        DeviceInputModel jio;
        DeviceInputModel cio;
        State.Level newQ = firstOutputModel.level;
        int newTime;
        DeviceInputModel io = firstInputModel;
        sio = io;
        io = (DeviceInputModel) io.next;
        rio = io;
        io = (DeviceInputModel) io.next;
        kio = io;
        io = (DeviceInputModel) io.next;
        jio = io;
        io = (DeviceInputModel) io.next;
        cio = io;
        State.Level c = cio.level;
        State.Level cPrevLevel = cio.prevLevel;
        State.Level k = kio.level;
        State.Level j = jio.level;
        State.Level r = rio.level;
        State.Level s = sio.level;
        if (cIsInverted) {
            c = State.negate(c);
            cPrevLevel = State.negate(cPrevLevel);
        }
        if (jIsInverted) {
            j = State.negate(j);
        }
        if (kIsInverted) {
            k = State.negate(k);
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
//                sample(c, j, k);
                State.Level d = internal;
                if (j == State.Level.low) {
                    if (k == State.Level.low) {
                        //hold
                        d = internal;
                    } else if (k == State.Level.high) {
                        d = State.Level.low;
                    } else { // k=u
                        if (internal != State.Level.low) {
                            d = State.Level.undefined;
                        }
                    }
                } else if (j == State.Level.high) {
                    if (k == State.Level.low) {
                        d = State.Level.high;
                    } else if (k == State.Level.high) {
                        d = State.negate(internal);
                    } else { // k=u
                        d = State.Level.undefined;
                    }
                } else { //j is undefined
                    d = State.Level.undefined;
                }
                if (cio.lastChange == Event.simulationTime) { //clock changed
                    if (c == State.Level.high) {
                        lastSampleTime = Event.simulationTime;
                        if (rio.lastChange + delays[T_RECOVERY] > lastSampleTime) {
                            timingError(rio, FaultViewer.FaultType.T_RECOVERY);
                        }
                        if (sio.lastChange + delays[T_RECOVERY] > lastSampleTime) {
                            timingError(sio, FaultViewer.FaultType.T_RECOVERY);
                        }
                        if (jio.lastChange + delays[T_SETUP] > lastSampleTime) {
                            timingError(jio, FaultViewer.FaultType.T_SETUP);
                        }
                        if (kio.lastChange + delays[T_SETUP] > lastSampleTime) {
                            timingError(kio, FaultViewer.FaultType.T_SETUP);
                        }
                        if (cPrevLevel== State.Level.low) {
                            internal = d;
                            newQ = d;
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
        if (jio.lastChange == Event.simulationTime) {
            if (lastSampleTime + delays[T_HOLD] > jio.lastChange) {
                timingError(jio, FaultViewer.FaultType.T_HOLD);
            }
        }
        if (kio.lastChange == Event.simulationTime) {
            if (lastSampleTime + delays[T_HOLD] > kio.lastChange) {
                timingError(kio, FaultViewer.FaultType.T_HOLD);
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
        lastSampleTime = -Math.max(delays[T_SETUP], delays[T_RECOVERY]) - 2;
        State.Level initLevel;
        if (Settings.ffInit == State.Level.random) {
            initLevel = RandomGen.getRandomLevel();
        } else {
            initLevel = Settings.ffInit;
        }
        internal = initLevel;
        if (initLevel != State.Level.undefined) {
//            firstOutputModel.level = initLevel;
//            firstOutputModel.next.level = State.negate(initLevel);
            createEvent(initLevel, State.negate(initLevel), 0);
            //Event.simulationTime +  delays[T_CO]);
        }
    }
}