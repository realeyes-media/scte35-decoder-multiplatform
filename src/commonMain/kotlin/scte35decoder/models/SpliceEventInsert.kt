package scte35decoder.models

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

/**
 * splice_insert()
 */
@ExperimentalJsExport
@JsExport
class SpliceEventInsert: SpliceEvent() {

    data class Component(val tag: Int, val time: SpliceTime?)

    var immediateSplice: Boolean? = null // TODO: deprecate if mode is working properly

    // Program Splice Mode
    var spliceTime: SpliceTime? = null
        internal set

    // Component or Immediate Splice Mode
    var components: Array<Component>? = null
        internal set

}