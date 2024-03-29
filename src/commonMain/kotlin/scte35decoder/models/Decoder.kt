package scte35decoder.models

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

/**
 * A [SCTE35](https://www.scte.org/SCTEDocs/Standards/ANSI_SCTE%2035%202019r1.pdf) (2019r1)
 * binary message decoder in Kotlin.
 *
 * ### Kotlin:
 * ```
 * val data : ByteArray = ...
 * val decoder = Decoder(data)
 * val info : SpliceInfoSection = decoder.getSpliceInfoSection()
 * ```
 *
 * @constructor Initializes the decoder with the given data.
 */
@ExperimentalJsExport
@JsExport
class Decoder(private val data: ByteArray) {

    private val iterator: ByteIterator = data.iterator()
    private var currentByte: Byte = 0
    private var consumedBits: Int = 0

    /**
     * Parses the decoder's given data and returns the resulting [SpliceInfoSection].
     *
     * @throws IllegalStateException in case of a syntax error in the given binary message
     * @throws IllegalArgumentException in the unlikely case of a programmer error.
     */
    fun getSpliceInfoSection(): SpliceInfoSection {
        val info = SpliceInfoSection()

        info.tableId = getByteAsInt()
        info.sectionSyntaxIndicator = getBoolean()
        info.privateIndicator = getBoolean()
        skipBits(2)
        info.sectionLength = getBits(12).toInt()
        info.protocolVersion = getByteAsInt()
        info.encrypted = getBoolean()
        info.encryptionAlgorithm = EncryptionAlgorithm.withOrdinal(getBits(6).toInt())
        info.ptsTypeAdjustment = getBitsLong(33).toLong()
        info.cwIndex = getByteAsInt()
        info.tier = getBits(12).toInt()

        // Splice Command
        val spliceCommandLength = getBits(12).toInt()
        val spliceCommandType = getByteAsInt()

        info.command = when(spliceCommandType) {
            0x00 -> SpliceCommand.Null
            0x04 -> getSpliceCommandSchedule()
            0x05 -> getSpliceCommandInsert()
            0x06 -> getSpliceCommandTimeSignal()
            0x07 -> SpliceCommand.BandwidthReservation
            0xff -> getSpliceCommandPrivate(spliceCommandLength)
            else -> throw IllegalArgumentException("Unknown splice command $spliceCommandType.")
        }

        // Splice Descriptors
        val descriptorLoopLength = getBytes(2).toInt()
        info.descriptors = getSpliceDescriptors(descriptorLoopLength)

        // stuffing
        // if (encrypted) info.ecrc32 = getBytesLong(4).toLong() // TODO
        // TODO: two bytes too many in Section 14.1 test
        info.crc32 = getCRC32()

        return info
    }

    private fun getSpliceCommandSchedule(): SpliceCommand.Schedule {
        val spliceCount = getByteAsInt()
        val tmp = arrayOfNulls<SpliceEventSchedule?>(spliceCount)

        for (i in 0 until spliceCount) {
            val event = SpliceEventSchedule()
            event.id = getBytesLong(4).toLong()

            val cancel = getBoolean()
            event.cancel = cancel

            skipBits(7)

            if (!cancel) {
                event.outOfNetwork = getBoolean()

                val program = getBoolean()
                event.program = program

                val isBreak = getBoolean()

                skipBits(5)

                if (program) {
                    event.mode = SpliceMode.Program
                    val timeUTCType: UTCType = getBytesLong(4).toLong()
                    event.spliceTime = timeUTCType
                } else {
                    event.mode = SpliceMode.Component
                    val componentCount = getByteAsInt()
                    val components = arrayOfNulls<SpliceEventSchedule.Component?>(componentCount)

                    for (j in 0 until componentCount) {
                        val tag = getByteAsInt()
                        val time = getBytesLong(4).toLong()
                        components[j] = SpliceEventSchedule.Component(tag, time)
                    }

                    event.components = Array(componentCount) { idx -> components[idx]!! }
                }

                if (isBreak) {
                    event.breakDuration = getBreakDuration()
                }

                event.programId = getBytes(2).toInt()
                event.availNum = getByteAsInt()
                event.availsExpected = getByteAsInt()
            }

            tmp[i] = event
        }

        val events = Array(spliceCount) { idx -> tmp[idx]!! }

        return SpliceCommand.Schedule(events)
    }

    private fun getSpliceCommandInsert(): SpliceCommand.Insert {
        val event = SpliceEventInsert()
        event.id = getBytesLong(4).toLong()

        val cancel = getBoolean()
        event.cancel = cancel

        skipBits(7)

        if (!cancel) {
            event.outOfNetwork = getBoolean()

            val program = getBoolean()
            event.program = program

            val isBreak = getBoolean()
            val immediate = getBoolean()
            event.immediateSplice = immediate

            skipBits(4)

            event.mode = when {
                program -> SpliceMode.Program
                immediate -> SpliceMode.Immediate
                else -> SpliceMode.Component
            }

            if (program && !immediate) {
                event.spliceTime = getSpliceTime()
            }

            if (!program) {
                val componentCount = getByteAsInt()
                val components = arrayOfNulls<SpliceEventInsert.Component?>(componentCount)

                for (i in 0 until componentCount) {
                    val tag = getByteAsInt()
                    val time: SpliceTime? = if (!immediate) getSpliceTime() else null

                    components[i] = SpliceEventInsert.Component(tag, time)
                }

                event.components = Array(componentCount) { idx -> components[idx]!! }
            }

            if (isBreak) {
                event.breakDuration = getBreakDuration()
            }

            event.programId = getBytes(2).toInt()
            event.availNum = getByteAsInt()
            event.availsExpected = getByteAsInt()
        }

        return SpliceCommand.Insert(event)
    }

    private fun getSpliceCommandTimeSignal(): SpliceCommand.TimeSignal {
        val timeSignal = getSpliceTime()
        return SpliceCommand.TimeSignal(timeSignal)
    }

    private fun getSpliceCommandPrivate(bytes: Int): SpliceCommand.Private {
        if (bytes <= 0) throw IllegalArgumentException("Requested bytes=$bytes, bytes non-positive.")
        val id = getBytesLong(4).toLong()
        val byteArr = ByteArray(bytes - 4) { 0 }
        for (i in byteArr.indices) {
            byteArr[i] = getByte()
        }
        return SpliceCommand.Private(id, byteArr)
    }

    private fun getSpliceDescriptors(length: Int): Array<SpliceDescriptor> {
        println("$$$ splice_descriptor_loop_length=$length")
        if (length < 0) throw IllegalArgumentException("Requested length=$length < 0 descriptor bytes.")

        val descriptors = ArrayList<SpliceDescriptor>()
        var bits = length * 8

        while (bits > 0) {
            val start = consumedBits
            val tag = getByteAsInt()

            println("$$$ splice_descriptor_tag=$tag, bits=$bits")
            val descriptor: SpliceDescriptor = when(tag) {
                0x00 -> getSpliceDescriptorAvail(tag)
                0x01 -> getSpliceDescriptorDTMF(tag)
                0x02 -> getSpliceDescriptorSegmentation(tag)
                0x03 -> getSpliceDescriptorTime(tag)
                0x04 -> getSpliceDescriptorAudio(tag)
                else -> throw IllegalArgumentException("Unknown splice descriptor tag $tag.")
            }

            val end = consumedBits

            bits -= (end - start)

            if (bits < 0) throw IllegalArgumentException("Splice descriptor with tag $tag at bit consumedBits=$consumedBits was ${end-start} bits long, but only ${end-start+consumedBits} bits were available.")

            descriptors.add(descriptor)
        }

        return Array(descriptors.size) { idx -> descriptors[idx] }
    }

    private fun getSpliceDescriptorAvail(tag: Int): SpliceDescriptor.Avail {
        val length = getByteAsInt()
        val start = consumedBits
        val id = getBytes(4).toLong()
        val providerAvailIdentifier = getBytes(4).toLong()

        if ((consumedBits - start) / 8 != length) throw IllegalArgumentException("Expected to consume $length bytes, actually consumed ${(consumedBits - start) / 8}.")

        return SpliceDescriptor.Avail(tag, id, providerAvailIdentifier)
    }

    private fun getSpliceDescriptorDTMF(tag: Int): SpliceDescriptor.DTMF {
        val length = getByteAsInt()
        val start = consumedBits
        val id = getBytes(4).toLong()
        val preroll = getByteAsInt()
        val dtmfCount = getBits(2).toInt()
        skipBits(5)
        val dtmfChar = getString(dtmfCount)

        if ((consumedBits - start) / 8 != length) throw IllegalArgumentException("Expected to conumed $length bytes, actually consumed ${(consumedBits - start) / 8}.")

        return SpliceDescriptor.DTMF(tag, id, preroll, dtmfCount, dtmfChar)
    }

    private fun getSpliceDescriptorSegmentation(tag: Int): SpliceDescriptor.Segmentation {
        val splice = SpliceDescriptor.Segmentation()
        splice.tag = tag

        val length = getByteAsInt()
        val start = consumedBits

        splice.id = getBytes(4).toLong()
        splice.eventId = getBytes(4).toLong()

        val cancel = getBoolean()
        splice.cancel = cancel

        skipBits(7)

        if (!cancel) {
            val program = getBoolean()
            splice.program = program

            splice.mode = when(program) {
                true -> SpliceMode.Program
                false -> SpliceMode.Component
            }

            val durationFlag = getBoolean()
            val restricted = !getBoolean()

            splice.deliveryRestricted = restricted

            if (restricted) {
                splice.webDeliveryAllowed = getBoolean()
                splice.noRegionalBlackout = getBoolean()
                splice.archiveAllowed = getBoolean()
                splice.deviceRestrictions = SpliceDescriptor.Segmentation.DeviceRestrictions.withOrdinal(getBits(2).toInt())
            } else {
                skipBits(5)
            }

            if (!program) {
                val componentCount = getByteAsInt()
                val components = arrayOfNulls<SpliceDescriptor.Segmentation.Component?>(componentCount)

                for (i in 0 until componentCount) {
                    val component = SpliceDescriptor.Segmentation.Component()
                    component.tag = getByteAsInt()
                    skipBits(7)
                    component.ptsOffset = getBitsLong(33).toLong()
                    components[i] = component
                }

                splice.components = Array(componentCount) { idx -> components[idx]!! }
            }

            if (durationFlag) {
                splice.duration = getBytesLong(5).toLong()
            }

            val upidType = getByteAsInt()
            splice.upidType = upidType

            val upidLength = getByteAsInt()
            splice.upidLength = upidLength

            val upidBytes = getByteArray(upidLength)
            splice.upid = getUpids(upidType, upidBytes)

            val typeId = getByteAsInt()
            splice.segmentationType = SegmentationType.withId(typeId)
            splice.segmentNum = getByteAsInt()
            splice.segmentsExpected = getByteAsInt()

            val typeIdCanHaveSubSegments = typeId == 0x34 || typeId == 0x36 || typeId == 0x38 || typeId == 0x3a
            // TODO: contradicting data within spec and samples
            // Even though the typeId does expect sub segments they seem to not always be included within the descriptor_length
            if (typeIdCanHaveSubSegments && (consumedBits - start) / 8 < length) {
                splice.subSegmentNum = getByteAsInt()
                splice.subSegmentsExpected = getByteAsInt()
            }

            if ((consumedBits - start) / 8 != length) throw IllegalArgumentException("Expected to consume $length bytes, actually consumed ${(consumedBits - start) / 8}.")
        }

        return splice
    }

    private fun getUpids(type: Int, bytes: ByteArray): Array<UPID> {
        if (type != UPID.Type.MID.ordinal) {
            return Array(1) { UPID(type, bytes) }
        }

        val upids = ArrayList<UPID>()
        var start = 0

        while (start < bytes.size) {
            val upidType = bytes[start].toInt()
            start += 1

            val length = bytes[start].toInt()
            start += 1

            val upidBytes = bytes.copyOfRange(start, length + start)
            start += length

            upids.add(UPID(upidType, upidBytes))
        }

        return Array(upids.size) { idx -> upids[idx] }
    }

    private fun getSpliceDescriptorTime(tag: Int): SpliceDescriptor.Time {
        val length = getByteAsInt()
        val start = consumedBits
        val id = getBytes(4).toLong()
        val taiSeconds = getBytesLong(4).toLong()
        val taiNanoseconds = getBytesLong(4).toLong()
        val utcOffset = getBytes(2).toInt()
        if ((consumedBits - start) / 8 != length) throw IllegalArgumentException("Expected to consume $length bytes, actually consumed ${(consumedBits - start) / 8}.")
        return SpliceDescriptor.Time(tag, id, taiSeconds, taiNanoseconds, utcOffset)
    }

    private fun getSpliceDescriptorAudio(tag: Int): SpliceDescriptor.Audio {
        val length = getByteAsInt()
        val start = consumedBits
        val id = getBytes(4).toLong()
        val audioCount = getBits(4).toInt()

        skipBits(4)

        val tmp = arrayOfNulls<SpliceDescriptor.Audio.Component>(audioCount)

        for (i in 0 until audioCount) {
            val componentTag = getByteAsInt()
            val isoCode = getBytes(3).toInt()
            val mode = BitStreamMode.withOrdinal(getBits(3).toInt())
            val numChannels = getBits(4).toInt()
            val fullServiceAudio = getBoolean()
            tmp[i] = SpliceDescriptor.Audio.Component(componentTag, isoCode, mode, numChannels, fullServiceAudio)
        }

        if ((consumedBits - start) / 8 != length) throw IllegalArgumentException("Expected to consume $length bytes, actually consumed ${(consumedBits - start) / 8}.")

        val components = Array(audioCount) { idx -> tmp[idx]!! }

        return SpliceDescriptor.Audio(tag, id, components)
    }

    private fun getBreakDuration(): BreakDuration {
        val autoReturn = getBoolean()
        skipBits(6)
        val duration = getBitsLong(33).toLong()
        return BreakDuration(autoReturn, duration)
    }

    private fun getSpliceTime(): SpliceTime {
        val spliceTime = SpliceTime()

        if (getBoolean()) {
            skipBits(6)
            spliceTime.ptsTime = getBitsLong(33).toLong()
        } else {
            skipBits(7)
        }

        return spliceTime
    }

    private fun getCRC32(): Long {
        if (consumedBits > (data.size - 4) * 8) throw IllegalArgumentException("Requested crc32 (32 bits) but consumedBits=$consumedBits of ${data.size * 8} bits have already been consumed.")
        var crc32 = 0L
        for (i in 0 until 4) {
            crc32 = crc32.or(getByteAsLong().shl(8 * (3 - i)))
        }
        return crc32
    }

    private fun getBit(): Int {
        val remainder = consumedBits % 8 // where are we in the current byte

        if (remainder == 0) {
            currentByte = iterator.next()
        }

        val byteAsInt = currentByte.toInt() and 0xFF
        val mask = 1.shl(7 - remainder)
        val bit: Int = if (byteAsInt.and(mask) > 0) 1 else 0

        consumedBits++

        return bit
    }

    private fun getBitLong(): Long {
        val remainder = consumedBits % 8 // how many bits of the current byte

        if (remainder == 0) {
            currentByte = iterator.next()
        }

        val byteAsInt = currentByte.toInt() and 0xFF
        val mask = 1.shl(7 - remainder)
        val bit: Long = if (byteAsInt.and(mask) > 0) 1 else 0

        consumedBits++

        return bit
    }

    private fun getBits(nBits: Int): Int {
        if (nBits <= 0 || nBits > 32) throw IllegalArgumentException("Requested bBits=$nBits not in 0 < nBits <= 32.")
        var bits = 0
        for (i in 1..nBits) {
            bits = bits.shl(1).or(getBit())
        }
        return bits
    }

    private fun getBitsLong(nBits: Int): Long {
        if (nBits <= 0 || nBits > 64) throw IllegalArgumentException("Requested nBits=$nBits not in 0 < nBits <= 64.")
        var bits = 0L
        for (i in 1..nBits) {
            bits = bits.shl(1).or(getBitLong())
        }
        return bits
    }

    private fun getString(bytes: Int): String {
        if (bytes <= 0) throw IllegalArgumentException("Requested bytes=$bytes, bytes non-positive.")
        if (consumedBits.rem(8) != 0) throw IllegalArgumentException("Requested a string from off a byte boundary consumeBits=$consumedBits.")

        val stringBuilder = StringBuilder(bytes)
        for (i in 1..bytes) {
            stringBuilder.append(getByteAsInt().toChar())
        }

        return stringBuilder.toString()
    }

    private fun getBoolean(): Boolean {
        return getBit() == 1
    }

    private fun getByte(): Byte {
        if (consumedBits.rem(8) != 0) throw IllegalArgumentException("Requested byte from off a byte boundary.")
        currentByte = iterator.next(); consumedBits += 8
        return currentByte
    }

    private fun getByteAsLong() : Long {
        return getByteAsInt().toLong()
    }

    private fun getByteAsInt() : Int {
        return getByte().toInt() and 0xFF
    }

    private fun getByteArray(nBytes: Int): ByteArray {
        if (consumedBits.rem(8) != 0) throw IllegalArgumentException("Requested byte array from off of a byte boundary, consumedBits=$consumedBits.")

        val remaining = data.size - consumedBits / 8

        if (nBytes > remaining) throw IllegalArgumentException("Requested nBytes=$nBytes, but only $remaining remain.")

        val bytes = ByteArray(nBytes)

        for (i in 0 until nBytes) {
            bytes[i] = getByte()
        }

        return bytes
    }

    private fun getBytes(nBytes: Int): Int {
        println("$$$ Requested nBytes=$nBytes from consumedBits=$consumedBits")

        if (nBytes <= 0 || nBytes > 4) throw IllegalArgumentException("Requested nBytes=$nBytes, nBytes not in 1 .. 4")

        val offset = consumedBits.rem(8)
        if (offset != 0) throw IllegalArgumentException("Requested nBytes=$nBytes bytes from off of a byte boundary, offset=$offset.")

        var bits: Int = 0

        for (i in 1..nBytes) {
            currentByte = iterator.next(); consumedBits += 8
            bits = bits.shl(8).or(currentByte.toInt() and 0xFF)
        }

        return bits
    }

    private fun getBytesLong(nBytes: Int): Long {
        if (nBytes <= 0 || nBytes > 8) throw IllegalArgumentException("Requested nBytes=$nBytes, nBytes not in 1 .. 8.")
        if (consumedBits.rem(8) != 0) throw IllegalArgumentException("Requested nBytes=$nBytes from off of a byte boundary, consumedBits=$consumedBits.")

        var bits: Long = 0

        for (i in 1..nBytes) {
            currentByte = iterator.next(); consumedBits += 8
            bits = bits.shl(8) or (currentByte.toInt() and 0xFF).toLong()
        }

        return bits
    }

    private fun skipBits(nBits: Int) {
        for (i in 1..nBits) getBit()
    }
}