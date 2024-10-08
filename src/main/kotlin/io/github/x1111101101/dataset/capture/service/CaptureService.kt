package io.github.x1111101101.dataset.capture.service

import io.github.x1111101101.dataset.capture.dao.CompleteCaptureDao
import io.github.x1111101101.dataset.capture.dao.ImageDao
import io.github.x1111101101.dataset.capture.dto.CaptureChannelStateResponse
import io.github.x1111101101.dataset.capture.dto.instruction.*
import io.github.x1111101101.dataset.capture.model.internal.CaptureChannel
import io.github.x1111101101.dataset.capture.model.internal.CaptureJob
import io.github.x1111101101.dataset.capture.model.public.CompleteCapture
import io.github.x1111101101.dataset.capture.util.toSha1String
import io.github.x1111101101.dataset.mainScope
import io.ktor.util.*
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.util.*
import kotlin.collections.HashMap

object CaptureService {

    private val channels = Array(2) { CaptureChannel(it + 1) }.associateBy { it.id }

    init {
        mainScope.launch {
            channels.values.forEach { channel ->
                launch {
                    channel.lastCompleteCapture.collect { completeCapture ->
                        saveCompleteCapture(completeCapture)
                    }
                }
            }
        }
    }

    fun getChannelInstructionStateFlow(channelId: Int) = channels[channelId]!!.currentInstruction

    fun startCaptureSession(request: CaptureStartRequest): CaptureStartResponse {
        val channelId = request.channelId
        val channel = channels[channelId] ?: return CaptureStartResponse(false, "")
        val sessionId = UUID.randomUUID()
        channel.startSession(sessionId, request.devices, request.snapshot)
        return CaptureStartResponse(true, sessionId.toString())
    }

    suspend fun allocateCapture(request: CaptureSavedReport): CaptureSaveResponse {
        //println("upload request")
        val channelId = request.channelId
        val channel = channels[channelId] ?: throw IllegalArgumentException()
        val imageId = UUID.randomUUID()
        channel.addCapture(UUID.fromString(request.captureSessionId), request.deviceId, imageId)
        return CaptureSaveResponse(imageId.toString())
    }

    fun captureSessionState(channelId: Int): CaptureChannelStateResponse {
        val channel = channels[channelId] ?: return CaptureChannelStateResponse(false, "", emptyMap())
        return fromJob(channel.currentJob.value)
    }

    suspend fun captureSessionStateAsFlow(channelId: Int): Flow<CaptureChannelStateResponse> {
        val channel = channels[channelId] ?: return flowOf(CaptureChannelStateResponse(false, "", emptyMap()))
        return channel.lastUpdate.map {
            fromJob(it?.job)
        }
    }

    private fun saveCompleteCapture(completeCapture: CompleteCapture) {
        println("SAVE COMPLETE: ${completeCapture}")
        CompleteCaptureDao.create(completeCapture)
    }

    private fun fromJob(job: CaptureJob?): CaptureChannelStateResponse {
        if (job == null) return CaptureChannelStateResponse(false, "", emptyMap())
        val workerMap = HashMap<Int, Boolean>()
        job.devices.forEach { workerId-> workerMap[workerId] = job.captures.firstOrNull { it.workerId == workerId } != null }
        return CaptureChannelStateResponse(
            true,
            job.sessionId.toString(),
            workerMap
        )
    }

    suspend fun forceUploadImage(imageId: UUID, sha1: String, imageBytes: ByteArray): WorkerCaptureUploadResponse {
        println("Force Upload: $imageId")
        val sha1Input = imageBytes.toSha1String()
        if(sha1 != sha1Input) {
            System.err.println("SHA 1 불일치")
            return WorkerCaptureUploadResponse(emptyList())
        }
        return withContext(mainScope.coroutineContext) {
            ImageDao.create(imageId, imageBytes)
            return@withContext WorkerCaptureUploadResponse(listOf(imageId.toString()))
        }
    }

    suspend fun uploadImages(request: WorkerCaptureUploadRequest): WorkerCaptureUploadResponse {
        return withContext(mainScope.coroutineContext) {
            val succeed = request.captures.map {
                async {
                    try {
                        val uuid = UUID.fromString(it.imageId)
                        //println("uploadImages: ${uuid}")
                        ImageDao.create(uuid, it.imageBase64.decodeBase64Bytes())
                        return@async it.imageId
                    } catch (e: Exception) {
                        return@async null
                    }
                }
            }.awaitAll().filterNotNull()
            return@withContext WorkerCaptureUploadResponse(succeed)
        }
    }

    fun startUpload(channelId: Int) {
        val channel = channels[channelId] ?: throw IllegalArgumentException()
        channel.startUploading()
    }

    fun getSnapshots(): List<UUID> {
        return CompleteCaptureDao.getAll()
    }

    fun getSnapshot(id: UUID): String? {
        return CompleteCaptureDao.getSnapshot(id)
    }

    fun getImage(id: UUID): ByteArray? {
        return ImageDao.get(id)
    }

}