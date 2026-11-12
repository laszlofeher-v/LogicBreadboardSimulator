package com.logicbreadboard.simulator

class LowModel : DeviceModel() {
    @JvmField
    var level: Boolean = false

    override fun reset() {
        super.reset()
        val newEvent = EventFactory.newEvent(0)
        newEvent.sourceTime = newEvent.time
        newEvent.deviceOutputModel = firstOutputModel
        newEvent.level = State.Level.low
        newEvent.impedance = State.Impedance.low
        Event.queue.add(newEvent)
    }
}
