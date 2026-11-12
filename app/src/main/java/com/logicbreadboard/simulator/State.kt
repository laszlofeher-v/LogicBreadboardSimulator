package com.logicbreadboard.simulator

/**
 * Created on 2/16/15.
 */
class State {
    enum class Level {
        low, undefined, high, random
    }

    enum class Impedance {
        low, weak, high, undefined
    }

    companion object {
        @JvmStatic
        fun negate(level: Level): Level {
            return when (level) {
                Level.low -> Level.high
                Level.high -> Level.low
                Level.undefined -> Level.undefined
                Level.random -> Level.random
            }
        }
    }
}
