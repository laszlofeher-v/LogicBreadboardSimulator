package com.logicbreadboard.simulator

class InverterModel : GateModel() {
    override fun createNextEvent() {
        addNextEvent(State.negate(firstInputModel.level))
    }
}
