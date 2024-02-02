package scte35decoder

import scte35decoder.models.Decoder
import scte35decoder.models.SpliceInfoSection

actual object Scte35DecoderFactory {
    actual fun createScteDecoder(): Scte35Decoder = Scte35DecoderAndroid
    actual fun createB64Decoder(): Base64Decoder = Base64DecoderAndroid
}

object Base64DecoderAndroid: Base64Decoder {
    override fun decode(str: String): ByteArray {
        return java.util.Base64.getDecoder().decode(str)
    }
}

object Scte35DecoderAndroid: Scte35Decoder  {
    override fun decodeFromB64(b64String: String, b64Decoder: Base64Decoder): SpliceInfoSection {
        val data = b64Decoder.decode(b64String)
        val decoder = Decoder(data)
        return decoder.getSpliceInfoSection()
    }
}

class Scte35DecoderTestsAndroid: Scte35DecoderTests()