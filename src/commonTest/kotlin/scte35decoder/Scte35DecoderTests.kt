package scte35decoder

import kotlin.test.Test
import kotlin.test.assertEquals

abstract class Scte35DecoderTests {

    @Test
    fun correctly_parses_ContentIdentification_scte_message_from_base64_string() {
        val b64Decoder = Scte35DecoderFactory.createB64Decoder()
        val scteDecoder = Scte35DecoderFactory.createScteDecoder()
        val info = scteDecoder.decodeFromB64("/DA1AAAAAAAAAP/wBQb/SMG+pgAfAh1DVUVJAAAAAX+/AQ5FUDAzMjU2ODEyMDAyNwEBATMCzNc=", b64Decoder)

        assertEquals(0xFC, info.tableId)
    }

    @Test
    fun correctly_parses_ContentIdentification_scte_message_from_hex_string() {
        val scteDecoder = Scte35DecoderFactory.createScteDecoder()
        val info = scteDecoder.decodeFromHex("0xFC303500000000000000FFF00506FF48C1BEA6001F021D43554549000000017FBF010E45503033323536383132303032370101013302CCD7")

        assertEquals(0xFC, info.tableId)
    }

}