package scte35decoder.models

/**
 * segmentation_type_id()
 */
enum class SegmentationType(val id: Int) {

    NotIndicated(0x00),
    ContentIdentification(0x01),
    ProgramStart(0x10),
    ProgramEnd(0x11),
    ProgramEarlyTermination(0x12),
    ProgramBreakAway(0x13),
    ProgramResumption(0x14),
    ProgramRunoverPlanned(0x15),
    ProgramRunoverUnplanned(0x16),
    ProgramOverlapStart(0x17),
    ProgramBlackoutOverride(0x18),
    ProgramStartInProgress(0x19),
    ChapterStart(0x20),
    ChapterEnd(0x21),
    BreakStart(0x22),
    BreakEnd(0x23),
    OpeningCreditStart(0x24),
    OpeningCreditEnd(0x25),
    ClosingCreditStart(0x26),
    ClosingCreditEnd(0x27),
    ProviderAdvertisementStart(0x30),
    ProviderAdvertisementEnd(0x31),
    DistributorAdvertisementStart(0x32),
    DistributorAdvertisementEnd(0x33),
    ProviderPlacementOpportunityStart(0x34),
    ProviderPlacementOpportunityEnd(0x35),
    DistributorPlacementOpportunityStart(0x36),
    DistributorPlacementOpportunityEnd(0x37),
    ProviderOverlayPlacementOpportunityStart(0x38),
    ProviderOverlayPlacementOpportunityEnd(0x39),
    DistributorOverlayPlacementOpportunityStart(0x3A),
    DistributorOverlayPlacementOpportunityEnd(0x3B),
    UnscheduledEventStart(0x40),
    UnscheduledEventEnd(0x41),
    NetworkStart(0x50),
    NetworkEnd(0x51);

    companion object {
        fun withId(id: Int): SegmentationType {
            return when(id) {
                NotIndicated.id -> NotIndicated
                ContentIdentification.id -> ContentIdentification
                ProgramStart.id -> ProgramStart
                ProgramEnd.id -> ProgramEnd
                ProgramEarlyTermination.id -> ProgramEarlyTermination
                ProgramBreakAway.id -> ProgramBreakAway
                ProgramResumption.id -> ProgramResumption
                ProgramRunoverPlanned.id -> ProgramRunoverPlanned
                ProgramRunoverUnplanned.id -> ProgramRunoverUnplanned
                ProgramOverlapStart.id -> ProgramOverlapStart
                ProgramBlackoutOverride.id -> ProgramBlackoutOverride
                ProgramStartInProgress.id -> ProgramStartInProgress
                ChapterStart.id -> ChapterStart
                ChapterEnd.id -> ChapterEnd
                BreakStart.id -> BreakStart
                BreakEnd.id -> BreakEnd
                OpeningCreditStart.id -> OpeningCreditStart
                OpeningCreditEnd.id -> OpeningCreditEnd
                ClosingCreditStart.id -> ClosingCreditStart
                ClosingCreditEnd.id -> ClosingCreditEnd
                ProviderAdvertisementStart.id -> ProviderAdvertisementStart
                ProviderAdvertisementEnd.id -> ProviderAdvertisementEnd
                DistributorAdvertisementStart.id -> DistributorAdvertisementStart
                DistributorAdvertisementEnd.id -> DistributorAdvertisementEnd
                ProviderPlacementOpportunityStart.id -> ProviderPlacementOpportunityStart
                ProviderPlacementOpportunityEnd.id -> ProviderPlacementOpportunityEnd
                DistributorPlacementOpportunityStart.id -> DistributorPlacementOpportunityStart
                DistributorPlacementOpportunityEnd.id -> DistributorPlacementOpportunityEnd
                ProviderOverlayPlacementOpportunityStart.id -> ProviderOverlayPlacementOpportunityStart
                ProviderOverlayPlacementOpportunityEnd.id -> ProviderOverlayPlacementOpportunityEnd
                DistributorOverlayPlacementOpportunityStart.id -> DistributorOverlayPlacementOpportunityStart
                DistributorOverlayPlacementOpportunityEnd.id -> DistributorOverlayPlacementOpportunityEnd
                UnscheduledEventStart.id -> UnscheduledEventStart
                UnscheduledEventEnd.id -> UnscheduledEventEnd
                NetworkStart.id -> NetworkStart
                NetworkEnd.id -> NetworkEnd
                else -> throw IllegalArgumentException("Unknown SegmentationType id 0x$id")
            }
        }
    }

}