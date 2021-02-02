package scte35decoder

import scte35decoder.models.Decoder
import scte35decoder.models.SpliceInfoSection
import scte35decoder.utils.hexStringToUByteArray
import kotlin.js.JsName

interface Scte35Decoder {
    @JsName("decodeFromB64")
    fun decodeFromB64(b64String: String, b64Decoder: Base64Decoder): SpliceInfoSection

    @JsName("decodeFromHex")
    fun decodeFromHex(hexString: String): SpliceInfoSection {
        val data = hexString.hexStringToUByteArray()
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