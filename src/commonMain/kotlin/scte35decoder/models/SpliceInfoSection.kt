package scte35decoder.models

class SpliceInfoSection {

    var tableId: Int? = null
        internal set

    var sectionSyntaxIndicator: Boolean? = null
        internal set

    var privateIndicator: Boolean? = null
        internal set

    var sectionLength: Int? = null
        internal set

    var protocolVersion: Int? = null
        internal set

    var encrypted: Boolean? = null
        internal set

    var encryptionAlgorithm: EncryptionAlgorithm? = null
        internal set

    var ptsTypeAdjustment: PTSType? = null
        internal set

    var cwIndex: Int? = null
        internal set

    var tier: Int? = null
        internal set

    var command: SpliceCommand? = null
        internal set

    var descriptors: Array<SpliceDescriptor>? = null
        internal set

    var stuffing: Int? = null
        internal set

    var ecrc32: Long? = null
        internal set

    var crc32: Long? = null
        internal set

}