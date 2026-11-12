package com.logicbreadboard.simulator

/**
 * Created on 2/16/15.
 */
open class Msg {
    @JvmField
    var timeStamp: Int

    init {
        timeStamp = globalTimeStamp++
    }

    companion object {
        @JvmField
        var globalTimeStamp: Int = 0
    }
}
