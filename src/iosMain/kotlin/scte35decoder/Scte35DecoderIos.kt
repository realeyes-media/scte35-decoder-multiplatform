package scte35decoder

import io.ktor.util.InternalAPI
import io.ktor.util.decodeBase64Bytes
import scte35decoder.models.Decoder
import scte35decoder.models.SpliceInfoSection

actual object Scte35DecoderFactory {
    actual fun createScteDecoder(): Scte35Decoder = Scte35DecoderIos
    actual fun createB64Decoder(): Base64Decoder = Base64DecoderIOS
}

object Base64DecoderIOS: Base64Decoder {
    @OptIn(InternalAPI::class)
    override fun decode(str: String): ByteArray {
        return str.decodeBase64Bytes()
    }
}
object Scte35DecoderIos : Scte35Decoder {
    override fun decodeFromB64(b64String: String, b64Decoder: Base64Decoder): SpliceInfoSection {
        val data = b64Decoder.decode(b64String)
        val decoder = Decoder(data)
        return decoder.getSpliceInfoSection()
    }
}