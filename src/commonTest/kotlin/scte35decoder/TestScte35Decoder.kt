package scte35decoder

import scte35decoder.models.Decoder
import scte35decoder.models.SpliceInfoSection
import scte35decoder.utils.hexStringToByteArray
import kotlin.js.JsName

/**
 * Allows for Android to mock its Base64 decoder for unit testing
 */
interface Scte35Decoder {
    @JsName("decodeFromB64")
    fun decodeFromB64(b64String: String, b64Decoder: Base64Decoder): SpliceInfoSection

    @JsName("decodeFromHex")
    fun decodeFromHex(hexString: String): SpliceInfoSection {
        val data = hexString.hexStringToByteArray()
        val decoder = Decoder(data)
        return decoder.getSpliceInfoSection()
    }
}

interface Base64Decoder {
    @JsName("decode")
    fun decode(str: String): ByteArray
}

expect object Scte35DecoderFactory {
    @JsName("createScteDecoder")
    fun createScteDecoder(): Scte35Decoder

    @JsName("createB64Decoder")
    fun createB64Decoder(): Base64Decoder
}