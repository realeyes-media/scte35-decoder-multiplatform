package scte35decoder.models

/**
 * The mode of the various [SpliceEvent]s. Not explicit in the spec.
 */
enum class SpliceMode {

    Program,
    Component,
    Immediate

}