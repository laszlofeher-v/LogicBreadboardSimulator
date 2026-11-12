package com.logicbreadboard.simulator

import android.util.Log
import java.util.Comparator

/**
 * Created by laci on 7/29/15.
 */
internal class EventComparator : Comparator<Event> {
    override fun compare(e0: Event, e1: Event): Int {
        if (e0.time > e1.time) {
            return 1
        } else if (e0.time < e1.time) {
            return -1
        } else {
            if (e0.sn > e1.sn) {
                return 1
            } else if (e0.sn < e1.sn) {
                return -1
            }
        }
        Log.i("cmp event", " Cannot be equal")
        return 0
    }
}
