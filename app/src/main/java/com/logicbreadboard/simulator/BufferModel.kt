package com.logicbreadboard.simulator

class BufferModel : GateModel() {
    override fun createNextEvent() {
        addNextEvent(firstInputModel.level)
    }
}
