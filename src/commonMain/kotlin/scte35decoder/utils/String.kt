package scte35decoder.utils

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

@ExperimentalJsExport
@JsExport
fun String.hexStringToByteArray(): ByteArray {
    return this.chunked(2).mapNotNull { try { it.toInt(16).toByte() } catch(e: Exception) { null } }.toByteArray()
}