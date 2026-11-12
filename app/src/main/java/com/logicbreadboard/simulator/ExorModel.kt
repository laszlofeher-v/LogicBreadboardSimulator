package com.logicbreadboard.simulator

class ExorModel : GateModel() {
    override fun createNextEvent() {
        val l0 = firstInputModel.level
        val l1 = firstInputModel.next.level
        val newLevel: State.Level = if (l0 == State.Level.undefined || l1 == State.Level.undefined) {
            State.Level.undefined
        } else if (l0 != l1) {
            State.Level.high
        } else {
            State.Level.low
        }
        addNextEvent(newLevel)
    }
}
