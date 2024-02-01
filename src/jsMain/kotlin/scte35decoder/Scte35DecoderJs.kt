package scte35decoder

import io.ktor.util.InternalAPI
import io.ktor.util.decodeBase64Bytes
import scte35decoder.models.Decoder
import scte35decoder.models.SpliceInfoSection

@ExperimentalJsExport
@JsExport
actual object Scte35DecoderFactory {
    actual fun createScteDecoder(): Scte35Decoder = Scte35DecoderJs
    actual fun createB64Decoder(): Base64Decoder = Base64DecoderJs
}

@ExperimentalJsExport
@JsExport
object Base64DecoderJs: Base64Decoder {
    @OptIn(InternalAPI::class)
    override fun decode(str: String): ByteArray {
        return str.decodeBase64Bytes()
    }
}

@ExperimentalJsExport
@JsExport
object Scte35DecoderJs: Scte35Decoder {
    override fun decodeFromB64(b64String: String, b64Decoder: Base64Decoder): SpliceInfoSection {
        val data = b64Decoder.decode(b64String)
        val decoder = Decoder(data)
        return decoder.getSpliceInfoSection()
    }
}