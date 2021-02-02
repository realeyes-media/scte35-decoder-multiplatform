package scte35decoder.models

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

/**
 * splice_time()
 *
 * @property ptsTime If time_specified_flag is 0 then ptsTime is null.
 */
@ExperimentalJsExport
@JsExport
class SpliceTime {

    var ptsTime: PTSType? = null
        internal set

}