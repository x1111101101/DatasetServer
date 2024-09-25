package io.github.x1111101101.dataset.capture.dto.instruction

import io.github.x1111101101.dataset.capture.dto.CaptureChannelStateResponse
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
class CaptureInstructionResponse private constructor(
    val type: InstructionType,
    val contentJson: String
) {
    constructor(response: CaptureUploadInstructionResponse): this(InstructionType.StartUpload, Json.encodeToString(response))
    constructor(response: CaptureChannelStateResponse): this(InstructionType.ChannelState, Json.encodeToString(response))
}

enum class InstructionType {
    ChannelState, StartUpload
}

