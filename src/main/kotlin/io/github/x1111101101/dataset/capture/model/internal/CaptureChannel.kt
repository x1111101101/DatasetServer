package io.github.x1111101101.dataset.capture.model.internal

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

class CaptureChannel(val id: Int) {

    val workers = ArrayList<Int>()
    private val _currentJob = MutableStateFlow<CaptureJob?>(null)
    val currentJob = _currentJob.asStateFlow()

    fun startSession(sessionId: UUID, devices: List<Int>) {
        _currentJob.update {
            CaptureJob(sessionId, devices)
        }
    }

}