package scte35decoder.models

enum class BitStreamMode {

    CompleteMain,
    MusicAndEffects,
    VisuallyImpaired,
    HearingImpaired,
    Dialogue,
    Commentary,
    Emergency,
    VoiceOver,
    Karaoke;

    companion object {
        fun withOrdinal(o: Int): BitStreamMode {
            return when (o) {
                0 -> CompleteMain
                1 -> MusicAndEffects
                2 -> VisuallyImpaired
                3 -> HearingImpaired
                4 -> Dialogue
                5 -> Commentary
                6 -> Emergency
                7 -> VoiceOver
                8 -> Karaoke
                else -> throw IllegalArgumentException("Unknown bit stream mode $0.")
            }
        }
    }
}