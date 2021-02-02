package scte35decoder.models

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

@ExperimentalJsExport
@JsExport
data class BreakDuration(val autoReturn: Boolean, val duration: PTSType)