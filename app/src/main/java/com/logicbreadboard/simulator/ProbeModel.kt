package com.logicbreadboard.simulator

import android.app.Activity
import android.app.Dialog
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText

class ProbeModel : LabelModel() {
    @JvmField
    var last: Int = 0

    @JvmField
    var time: IntArray = IntArray(100)

    @JvmField
    var level: IntArray = IntArray(100)

    init {
        delays = IntArray(3)
        delays[START_TIME_INDEX] = 0
        delays[END_TIME_INDEX] = 10000
        delays[MAX_COUNT_INDEX] = 100
    }

    override fun reset() {
        super.reset()
        val count = delays[MAX_COUNT_INDEX]
        try {
            time = IntArray(count)
            level = IntArray(count)
        } catch (e: OutOfMemoryError) {
            last = 0
            level[last] = State.Level.undefined.ordinal
            time[last++] = delays[START_TIME_INDEX]
            return
        }
        last = 0
        level[last] = State.Level.undefined.ordinal
        time[last++] = delays[START_TIME_INDEX]
    }

    override fun createNextEvent() {
        if (firstInputModel.level == null) {
            firstInputModel.level = State.Level.undefined
        }
        if (level[last - 1] != firstInputModel.level.ordinal) {
            if (Settings.displayWireLevel) {
                Event.displayEvent = true
                Event.changedNetsAndLevels.add(iov.netNumber)
                Event.changedNetsAndLevels.add(iov.model.level.ordinal)
            }
            if (last < time.size) {
                if (Event.simulationTime >= delays[START_TIME_INDEX]) {
                    if (Event.simulationTime <= delays[END_TIME_INDEX]) {
                        if (last < delays[MAX_COUNT_INDEX]) {
                            level[last] = firstInputModel.level.ordinal
                            time[last++] = Event.simulationTime
                        }
                    }
                }
            }
        }
    }

    override fun setDelay(index: Int, t: Int): String? {
        var str: String? = null
        var validT = t
        if (validT < 0) {
            str = "Wrong delay parameter:$validT changed to 0."
            validT = 0
        }
        delays[index] = validT
        return str
    }

    override fun showDelayPopup(activity: Activity) {
        val dialog = Dialog(activity)
        dialog.setContentView(R.layout.probe_setting)
        dialog.setTitle("Set probe")
        val startUp = dialog.findViewById<Button>(R.id.startUp)
        val startDown = dialog.findViewById<Button>(R.id.startDown)
        val endUp = dialog.findViewById<Button>(R.id.endUp)
        val endtDown = dialog.findViewById<Button>(R.id.endDown)
        val countUp = dialog.findViewById<Button>(R.id.countUp)
        val countDown = dialog.findViewById<Button>(R.id.countDown)
        val startEditText = dialog.findViewById<EditText>(R.id.startTime)
        val endEditText = dialog.findViewById<EditText>(R.id.endTime)
        val countView = dialog.findViewById<EditText>(R.id.count)

        startEditText.setText(delays[START_TIME_INDEX].toString())
        endEditText.setText(delays[END_TIME_INDEX].toString())
        countView.setText(delays[MAX_COUNT_INDEX].toString())

        TimeDialog.setListeners(startUp, startEditText, delays, START_TIME_INDEX, TimeDialog.Dirs.UP, 0)
        TimeDialog.setListeners(startDown, startEditText, delays, START_TIME_INDEX, TimeDialog.Dirs.DOWN, 0)
        TimeDialog.setListeners(endUp, endEditText, delays, END_TIME_INDEX, TimeDialog.Dirs.UP, 0)
        TimeDialog.setListeners(endtDown, endEditText, delays, END_TIME_INDEX, TimeDialog.Dirs.DOWN, 0)
        TimeDialog.setListeners(countUp, countView, delays, MAX_COUNT_INDEX, TimeDialog.Dirs.UP, 0)
        TimeDialog.setListeners(countDown, countView, delays, MAX_COUNT_INDEX, TimeDialog.Dirs.DOWN, 0)

        val window = dialog.window
        if (window != null) {
            val lp = WindowManager.LayoutParams()
            lp.copyFrom(window.attributes)
            lp.width = WindowManager.LayoutParams.MATCH_PARENT
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT
            dialog.show()
            window.attributes = lp
        } else {
            dialog.show()
        }
    }

    companion object {
        const val START_TIME_INDEX = 0
        const val END_TIME_INDEX = 1
        const val MAX_COUNT_INDEX = 2
    }
}
