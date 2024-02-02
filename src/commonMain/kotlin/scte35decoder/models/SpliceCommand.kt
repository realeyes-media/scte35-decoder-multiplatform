package scte35decoder.models

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

/**
 * splice_command()
 */
@ExperimentalJsExport
@JsExport
sealed class SpliceCommand {

    /**
     * splice_null()
     */
    object Null: SpliceCommand()

    /**
     * splice_schedule()
     */
    data class Schedule(val events: Array<SpliceEventSchedule>): SpliceCommand() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as Schedule

            if (!events.contentEquals(other.events)) return false

            return true
        }

        override fun hashCode(): Int {
            return events.contentHashCode()
        }
    }

    /**
     * splice_insert()
     */
    data class Insert(val event: SpliceEventInsert): SpliceCommand()

    /**
     * time_signal()
     */
    data class TimeSignal(val timeSignal: SpliceTime): SpliceCommand()

    /**
     * bandwidth_reservation()
     */
    object BandwidthReservation: SpliceCommand()

    /**
     * private_command()
     */
    data class Private(val id: Long, val bytes: ByteArray): SpliceCommand() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as Private

            if (id != other.id) return false
            if (!bytes.contentEquals(other.bytes)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = id.hashCode()
            result = 31 * result + bytes.contentHashCode()
            return result
        }
    }

}