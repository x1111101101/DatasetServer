package io.github.x1111101101.dataset.capture.dto.instruction

import kotlinx.serialization.Serializable

@Serializable
data class WorkerCaptureUploadRequest(val workerId: Int, val captures: List<WorkerCapture>) {

}

@Serializable
data class WorkerCapture(val imageId: String, val imageBase64: String)