package com.logicbreadboard.simulator

/**
 * Created on 2/16/15.
 */
object EventFactory {
    @JvmStatic
    var unusedList: Event? = null

    @JvmStatic
    fun newEvent(time: Int): Event {
        val e: Event
        val unused = unusedList
        if (unused == null) {
            e = Event(time)
        } else {
            e = unused
            e.time = time
            unusedList = unused.next
        }
        e.sn = Event.snCounter++
        return e
    }

    @JvmStatic
    fun disposeEvent(event: Event?) {
        if (event != null) {
            event.next = unusedList
            unusedList = event
        }
    }
}
