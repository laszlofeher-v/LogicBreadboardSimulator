package com.logicbreadboard.simulator;

/**
 * Created by laci on 8/10/15.
 */
public class DLatchModel extends FlipFlopModel {
    private boolean dIsInverted = false;
    private boolean eIsInverted = false;
    private int eTime;
    private int dTime;
    private State.Level ePrev;
    private State.Level dPrev;

    public DLatchModel() {
        super();
    }

    @Override
    public void setInverted(DeviceIOViewer first) {
        DeviceIOViewer d = first.next.next;
        dIsInverted = d.inverted;
        eIsInverted = d.next.inverted;
    }

    void reset() {
        super.reset();
        lastInputChangeTime = 0;
        State.Level initLevel;
        if (Settings.ffInit == State.Level.random) {
            initLevel = RandomGen.getRandomLevel();
        } else {
            initLevel = Settings.ffInit;
        }
        if (initLevel != State.Level.undefined) {
            firstOutputModel.level=initLevel;
            firstOutputModel.next.level = State.negate(initLevel);

//            createEvent(initLevel, State.negate(initLevel), Event.simulationTime + delays[T_CO]);
        }
    }

    @Override
    void createNextEvent() {
        State.Level newQ = firstOutputModel.level;
        int newTime;
        State.Level e = firstInputModel.level;
        State.Level d = firstInputModel.next.level;
        if (eIsInverted) {
            e = State.negate(e);
        }
        if (dIsInverted) {
            d = State.negate(d);
        }
        if (dPrev != firstInputModel.next.level) { //D changed
            if (e == State.Level.high) {
                newQ = d;
            // TODO: 8/13/15 delay
                newTime = Event.simulationTime + delays[T_CO];
                createEvent(newQ, State.negate(newQ), newTime);
            } else if (e == State.Level.undefined) {
                newQ = State.Level.undefined;
            // TODO: 8/13/15 delay
                newTime = Event.simulationTime + delays[T_CO];
                createEvent(newQ, State.negate(newQ), newTime);
            }
        } else if (e == State.Level.high) {
            newQ = d;
            // TODO: 8/13/15 delay
            newTime = Event.simulationTime + delays[T_CO];
            createEvent(newQ, State.negate(newQ), newTime);
        }
        ePrev = firstInputModel.level;
        dPrev = firstInputModel.next.level;
    }


}
