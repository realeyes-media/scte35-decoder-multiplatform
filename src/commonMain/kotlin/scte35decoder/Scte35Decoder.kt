package scte35decoder

import scte35decoder.models.Decoder
import scte35decoder.models.SpliceInfoSection
import scte35decoder.utils.hexStringToUByteArray

interface Scte35Decoder {
    fun decodeFromB64(b64String: String, b64Decoder: Base64Decoder): SpliceInfoSection

    fun decodeFromHex(hexString: String): SpliceInfoSection {
        val data = hexString.hexStringToUByteArray()
        val decoder = Decoder(data)
        return decoder.getSpliceInfoSection()
    }
}

interface Base64Decoder {
    fun decode(str: String): ByteArray
}

expect object Scte35DecoderFactory {
    fun createScteDecoder(): Scte35Decoder
    fun createB64Decoder(): Base64Decoder
}