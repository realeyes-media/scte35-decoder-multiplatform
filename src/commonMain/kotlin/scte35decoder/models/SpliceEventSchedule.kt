package scte35decoder.models

/**
 * splice_schedule()
 */
class SpliceEventSchedule: SpliceEvent() {

    data class Component(val tag: Int, val timeType: UTCType)

    // Program Splice Mode
    var spliceTime: Long? = null
        internal set

    // Component Splice Mode
    var components: Array<Component>? = null
        internal set

}