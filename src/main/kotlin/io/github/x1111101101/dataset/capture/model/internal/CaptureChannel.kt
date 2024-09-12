package io.github.x1111101101.dataset.capture.model.internal

import io.github.x1111101101.dataset.capture.model.public.Capture
import io.github.x1111101101.dataset.formatted
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.*
import kotlin.collections.ArrayList

class CaptureChannel(val id: Int) {

    val workers = ArrayList<Int>()
    private val _currentJob = MutableStateFlow<CaptureJob?>(null)
    val currentJob = _currentJob.asStateFlow()
    private val _lastUpdate = MutableStateFlow<CaptureJobUpdate?>(null)
    val lastUpdate = _lastUpdate.asStateFlow()

    fun startSession(sessionId: UUID, devices: List<Int>) {
        val job = CaptureJob(sessionId, devices)
        _currentJob.update {
            job
        }
        _lastUpdate.update { CaptureJobUpdate(System.currentTimeMillis(), job) }
    }

    fun addCapture(sessionId: UUID, workerId: Int, image: UUID) {
        val job = currentJob.value ?: throw IllegalStateException()
        if(job.captures.firstOrNull { it.workerId == workerId} != null) throw IllegalStateException()
        if(job.sessionId != sessionId) throw IllegalStateException()
        job.captures.add(Capture(workerId, image.toString(), time = Date(System.currentTimeMillis()).formatted()))
        _currentJob.update { it }
        _lastUpdate.update { CaptureJobUpdate(System.currentTimeMillis(), job) }
    }

    data class CaptureJobUpdate(val time: Long, val job: CaptureJob)

}