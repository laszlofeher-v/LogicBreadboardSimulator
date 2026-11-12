package com.logicbreadboard.simulator

import java.util.Comparator
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.PriorityBlockingQueue

/**
 * Created on 2/16/15.
 */
class Event(@JvmField var time: Int) {
    @JvmField
    var sourceTime: Int = 0

    @JvmField
    var sn: Int = 0

    @JvmField
    var deviceOutputModel: DeviceOutputModel? = null

    @JvmField
    var level: State.Level? = null

    @JvmField
    var impedance: State.Impedance? = null

    @JvmField
    var next: Event? = null

    companion object {
        private const val INITIAL_CAPACITY = 100

        @JvmField
        var displayEvent: Boolean = false

        @JvmField
        var spikeWidth: IntArray = intArrayOf(0)

        @JvmField
        var comparator: Comparator<Event> = EventComparator()

        @JvmField
        var queue: PriorityBlockingQueue<Event> = PriorityBlockingQueue(INITIAL_CAPACITY, comparator)

        @JvmField
        var buttonEventQueue: BlockingQueue<Event> = LinkedBlockingQueue()

        @JvmField
        var changedNetsAndLevels: BlockingQueue<Int> = LinkedBlockingQueue()

        @JvmField
        var simulationTime: Int = 0

        @JvmField
        var snCounter: Int = 0

        @Synchronized
        @JvmStatic
        fun clear() {
            clearQueue()
            simulationTime = 0
            snCounter = 0
        }

        @JvmStatic
        fun clearQueue() {
            val iterQueue = queue.iterator()
            while (iterQueue.hasNext()) {
                val e = iterQueue.next()
                EventFactory.disposeEvent(e)
            }
            queue.clear()

            val iterButton = buttonEventQueue.iterator()
            while (iterButton.hasNext()) {
                val e = iterButton.next()
                EventFactory.disposeEvent(e)
            }
            buttonEventQueue.clear()
            displayEvent = false
            changedNetsAndLevels.clear()
        }

        @JvmStatic
        fun loadButtonEvents() {
            var event = buttonEventQueue.poll()
            while (event != null) {
                if (event.time < simulationTime + 1) {
                    event.time = simulationTime + 1
                }
                event.sourceTime = event.time
                queue.add(event)
                event = buttonEventQueue.poll()
            }
        }
    }
}
