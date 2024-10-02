package io.github.x1111101101.dataset.capture.model.internal

import io.github.x1111101101.dataset.capture.dto.CaptureChannelStateResponse
import io.github.x1111101101.dataset.capture.dto.instruction.CaptureInstructionResponse
import io.github.x1111101101.dataset.capture.dto.instruction.CaptureUploadInstructionResponse
import io.github.x1111101101.dataset.capture.model.public.Capture
import io.github.x1111101101.dataset.capture.model.public.CaptureSnapshot
import io.github.x1111101101.dataset.capture.model.public.CompleteCapture
import io.github.x1111101101.dataset.formatted
import io.github.x1111101101.dataset.mainScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*

class CaptureChannel(val id: Int) {

    private var lastUploadInstruction = 0L
    private val _currentJob = MutableStateFlow<CaptureJob?>(null)
    private val _lastUpdate = MutableStateFlow<CaptureJobUpdate?>(null)
    private val _lastCompleteCapture = Channel<CompleteCapture>()
    private val _currentInstruction = MutableStateFlow(CaptureInstructionResponse(CaptureUploadInstructionResponse(0)))

    // Instruction that will be sent to workers
    val currentInstruction = _currentInstruction.asStateFlow()
    val currentJob = _currentJob.asStateFlow()
    val lastUpdate = _lastUpdate.asStateFlow()
    val lastCompleteCapture = _lastCompleteCapture.receiveAsFlow()

    init {
        mainScope.launch {
            uploadInstructionLoop()
        }
    }

    private suspend fun uploadInstructionLoop() {
        while(true) {
            delay(800)
            _currentInstruction.update {
                CaptureInstructionResponse(CaptureUploadInstructionResponse(lastUploadInstruction))
            }
            delay(800)
            val job = this.currentJob.value ?: continue
            val instruction = CaptureInstructionResponse(CaptureChannelStateResponse(true, job.sessionId.toString(), job.devices.map { it to false }.toMap()))
            _currentInstruction.update { instruction }
        }
    }

    fun startSession(sessionId: UUID, devices: List<Int>, snapshot: CaptureSnapshot) {
        val job = CaptureJob(sessionId, devices, snapshot)
        _currentJob.update { job }
        _lastUpdate.update { CaptureJobUpdate(System.currentTimeMillis(), job) }
        val instruction = CaptureInstructionResponse(CaptureChannelStateResponse(true, job.sessionId.toString(), job.devices.map { it to false }.toMap()))
        _currentInstruction.update { instruction }
    }

    fun startUploading() {
        lastUploadInstruction = System.currentTimeMillis()
        _currentInstruction.update {
            CaptureInstructionResponse(CaptureUploadInstructionResponse(lastUploadInstruction))
        }
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