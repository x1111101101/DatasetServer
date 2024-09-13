package io.github.x1111101101.dataset.capture.model.internal

import io.github.x1111101101.dataset.capture.model.public.Capture
import io.github.x1111101101.dataset.capture.model.public.CaptureSnapshot
import io.github.x1111101101.dataset.capture.model.public.CompleteCapture
import io.github.x1111101101.dataset.formatted
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import java.util.*
import kotlin.collections.ArrayList

class CaptureChannel(val id: Int) {

    val workers = ArrayList<Int>()
    private val _currentJob = MutableStateFlow<CaptureJob?>(null)
    val currentJob = _currentJob.asStateFlow()
    private val _lastUpdate = MutableStateFlow<CaptureJobUpdate?>(null)
    val lastUpdate = _lastUpdate.asStateFlow()
    private val _lastCompleteCapture = Channel<CompleteCapture>()
    val lastCompleteCapture = _lastCompleteCapture.receiveAsFlow()

    fun startSession(sessionId: UUID, devices: List<Int>, snapshot: CaptureSnapshot) {
        val job = CaptureJob(sessionId, devices, snapshot)
        _currentJob.update {
            job
        }
        _lastUpdate.update { CaptureJobUpdate(System.currentTimeMillis(), job) }
    }

    suspend fun addCapture(sessionId: UUID, workerId: Int, image: UUID) {
        val job = currentJob.value ?: throw IllegalStateException()
        if(job.captures.firstOrNull { it.workerId == workerId} != null) throw IllegalStateException()
        if(job.sessionId != sessionId) throw IllegalStateException()
        if(System.currentTimeMillis() - job.createTime > 10000) throw IllegalStateException()
        job.captures.add(Capture(workerId, image.toString(), time = Date(System.currentTimeMillis()).formatted()))
        _currentJob.update { it }
        _lastUpdate.update { CaptureJobUpdate(System.currentTimeMillis(), job) }
        val left = job.devices - job.captures.map { it.workerId }.toSet()
        if(left.isNotEmpty()) return
        val completeCapture = CompleteCapture(snapshot = job.snapshot, captures = job.captures.toList())
        _lastCompleteCapture.send(completeCapture)
        _currentJob.update { null }
    }

    data class CaptureJobUpdate(val time: Long, val job: CaptureJob)

}