package scte35decoder.models

/**
 * A class for material general to the spec. Un-instantiable.
 */
sealed class SCTE35 {

    companion object {
        /**
         * The SCTE35 program clock frequency, 90 KHz.
         */
        const val ClockFrequency = 90000L
    }

}