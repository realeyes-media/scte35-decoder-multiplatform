package scte35decoder

import android.util.Base64
import scte35decoder.models.Decoder
import scte35decoder.models.SpliceInfoSection
import scte35decoder.utils.hexStringToUByteArray

actual object Scte35DecoderFactory {
    actual fun createScteDecoder(): Scte35Decoder = Scte35DecoderAndroid
    actual fun createB64Decoder(): Base64Decoder = Base64DecoderAndroid
}

object Base64DecoderAndroid: Base64Decoder {
    override fun decode(str: String): ByteArray {
        return Base64.decode(str, Base64.DEFAULT)
    }
}

object Scte35DecoderAndroid: Scte35Decoder  {
    override fun decodeFromB64(b64String: String, b64Decoder: Base64Decoder): SpliceInfoSection {
        val data = b64Decoder.decode(b64String).asUByteArray()
        val decoder = Decoder(data)
        return decoder.getSpliceInfoSection()
    }
}