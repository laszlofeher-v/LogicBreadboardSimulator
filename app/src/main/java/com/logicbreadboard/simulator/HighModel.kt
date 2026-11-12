package com.logicbreadboard.simulator

class HighModel : DeviceModel() {
    @JvmField
    var level: Boolean = false

    override fun reset() {
        super.reset()
        val newEvent = EventFactory.newEvent(0)
        newEvent.sourceTime = newEvent.time
        newEvent.deviceOutputModel = firstOutputModel
        newEvent.level = State.Level.high
        newEvent.impedance = State.Impedance.low
        Event.queue.add(newEvent)
    }
}
