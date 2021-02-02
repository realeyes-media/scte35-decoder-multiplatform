package scte35decoder.models

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

/**
 * splice_schedule()
 */
@ExperimentalJsExport
@JsExport
class SpliceEventSchedule: SpliceEvent() {

    data class Component(val tag: Int, val timeType: UTCType)

    // Program Splice Mode
    var spliceTime: Long? = null
        internal set

    // Component Splice Mode
    var components: Array<Component>? = null
        internal set

}