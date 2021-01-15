package scte35decoder.utils

fun String.hexStringToUByteArray(): UByteArray {
    return this.chunked(2).mapNotNull { try { it.toInt(16).toUByte() } catch(e: Exception) { null } }.toUByteArray()
}