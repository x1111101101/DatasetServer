package io.github.x1111101101.dataset.capture.model.internal

import io.github.x1111101101.dataset.capture.model.public.Capture
import java.time.LocalDateTime
import java.util.*

class CaptureJob(
    val sessionId: UUID,
    val devices: List<Int>
) {

    val createTime = LocalDateTime.now()
    val captures = ArrayList<Capture>()



}