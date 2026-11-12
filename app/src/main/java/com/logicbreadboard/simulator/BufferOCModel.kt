package com.logicbreadboard.simulator

class BufferOCModel : GateModel() {
    init {
        delays = intArrayOf(10)
    }

    override fun createNextEvent() {
        var newLevel = State.Level.undefined
        var newImpedance = State.Impedance.undefined
        when (firstInputModel.level) {
            State.Level.high -> newImpedance = State.Impedance.high
            State.Level.low -> {
                newImpedance = State.Impedance.low
                newLevel = State.Level.low
            }
            State.Level.undefined, null -> newImpedance = State.Impedance.undefined
            State.Level.random -> newImpedance = State.Impedance.undefined
        }
        addNextEvent(newLevel, newImpedance)
    }
}
