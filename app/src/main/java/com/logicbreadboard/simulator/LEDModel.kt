package com.logicbreadboard.simulator

class LEDModel : DeviceModel() {
    override fun createNextEvent() {
        Event.displayEvent = true
    }
}
