package com.logicbreadboard.simulator

class NorModel : GateModel() {
    override fun createNextEvent() {
        var newOutput = State.Level.high
        var input = firstInputModel
        do {
            if (input.level == State.Level.high) {
                newOutput = State.Level.low
                break
            } else if (input.level == State.Level.undefined) {
                newOutput = State.Level.undefined
            }
            input = input.next as? DeviceInputModel
        } while (input != null)
        addNextEvent(newOutput)
    }
}
