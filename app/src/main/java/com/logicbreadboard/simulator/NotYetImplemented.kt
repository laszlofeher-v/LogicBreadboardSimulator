package com.logicbreadboard.simulator

/**
 * Created on 12/8/15.
 */
class NotYetImplemented(var str1: String?) : Exception(str1) {
    override fun toString(): String {
        return "Output String = $str1"
    }
}
