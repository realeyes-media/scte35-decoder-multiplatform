package scte35decoder.models

/**
 * splice_time()
 *
 * @property ptsTime If time_specified_flag is 0 then ptsTime is null.
 */
class SpliceTime {

    var ptsTime: PTSType? = null
        internal set

}