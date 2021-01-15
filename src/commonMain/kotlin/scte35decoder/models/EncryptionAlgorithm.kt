package scte35decoder.models

/**
 * Scte35 Encryption Algorithm
 */
enum class EncryptionAlgorithm {

    None,
    DES_ECB,
    DES_CBC,
    TRIPLEDES_EDE3_ECB,
    Reserved,
    Private;

    companion object {
        fun withOrdinal(ordinal: Int): EncryptionAlgorithm {
            return when(ordinal) {
                0 -> None
                1 -> DES_ECB
                2 -> DES_CBC
                3 -> TRIPLEDES_EDE3_ECB
                in 4..31 -> Reserved
                in 32..63 -> Private
                else -> throw IllegalArgumentException("No known encryption scheme $ordinal.")
            }
        }
    }

}