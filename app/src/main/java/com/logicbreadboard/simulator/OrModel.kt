package com.logicbreadboard.simulator

class OrModel : GateModel() {
    override fun createNextEvent() {
        var newOutput = State.Level.low
        var input = firstInputModel
        do {
            if (input.level == State.Level.high) {
                newOutput = State.Level.high
                break
            } else if (input.level == State.Level.undefined) {
                newOutput = State.Level.undefined
            }
            input = input.next as? DeviceInputModel
        } while (input != null)
        addNextEvent(newOutput)
    }
}
