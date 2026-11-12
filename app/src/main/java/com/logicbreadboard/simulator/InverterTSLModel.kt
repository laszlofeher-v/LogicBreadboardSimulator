package com.logicbreadboard.simulator

class InverterTSLModel : GateModel() {
    override fun createNextEvent() {
        var newLevel = State.Level.undefined
        var newImpedance = State.Impedance.undefined
        when (firstInputModel.level) {
            State.Level.high -> newImpedance = State.Impedance.high
            State.Level.low -> {
                newImpedance = State.Impedance.low
                newLevel = State.negate(firstInputModel.next.level)
            }
            State.Level.undefined, null -> newImpedance = State.Impedance.undefined
            State.Level.random -> newImpedance = State.Impedance.undefined
        }
        addNextEvent(newLevel, newImpedance)
    }
}
