package scte35decoder.models

/**
 * segmentation_upid_type
 */
class UPID(val t: Int, val bytes: ByteArray) {

    enum class Type(val length: Int, val description: String) {

        None(0, "Not Used"),
        User(-1, "User Defined"), // -1 variable length
        ISCI(8, "ISCI (Deprecated)"),
        AdID(12, "Ad-ID"),
        UMID(32, "Unique Material Identifier (SMPTE 330)"),
        ISANDeprecated(8, "ISAN (ISO-15706) (Deprecated)"),
        ISAN(12, "ISAN (ISO-16706-2)"),
        TID(12, "Tribune Media System Program Id"),
        TI(8, "AiringId (TurnerId)"),
        ADI(-1, "CableLabs Metadata Id"),
        EIDR(12, "EIDR"),
        ATSC(-1, "ATSC Content Identifier"),
        MPU(-1, "Managed Private UPID"),
        MID(-1, "Multiple UPID"),
        ADS(-1, "ADS Information"),
        URI(-1, "URI (RFC 3986)"),
        Reserved(-1, "Reserved"); // 0x10 - 0xFF

        companion object {
            fun withOrdinal(o: Int): Type {
                return when (o) {
                    None.ordinal -> None
                    User.ordinal -> User
                    ISCI.ordinal -> ISCI
                    AdID.ordinal -> AdID
                    UMID.ordinal -> UMID
                    ISANDeprecated.ordinal -> ISANDeprecated
                    ISAN.ordinal -> ISAN
                    TID.ordinal -> TID
                    TI.ordinal -> TI
                    ADI.ordinal -> ADI
                    EIDR.ordinal -> EIDR
                    ATSC.ordinal -> ATSC
                    MPU.ordinal -> MPU
                    MID.ordinal -> MID
                    ADS.ordinal -> ADS
                    URI.ordinal -> URI
                    in Reserved.ordinal..0xff -> Reserved
                    else -> throw IllegalArgumentException("Unknown UPID type 0x$o")
                }
            }
        }
    }

    private val type: Type

    init {
        type = Type.withOrdinal(t)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as UPID

        if (t != other.t) return false
        if (bytes.size != other.bytes.size) return false

        return (bytes contentEquals other.bytes)
    }

    override fun hashCode(): Int {
        var result = t
        for (i in bytes.indices) {
            result = 31 * result + bytes.contentHashCode()
        }
        return result
    }
}