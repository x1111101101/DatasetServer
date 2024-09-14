package io.github.x1111101101.dataset.capture.model.internal

import com.google.common.collect.Sets
import io.github.x1111101101.dataset.capture.model.public.Capture
import io.github.x1111101101.dataset.capture.model.public.CaptureSnapshot
import java.time.LocalDateTime
import java.util.*

class CaptureJob(
    val sessionId: UUID,
    val devices: List<Int>,
    val snapshot: CaptureSnapshot
) {

    val createTime = System.currentTimeMillis()
    val captures = Sets.newConcurrentHashSet<Capture>()



}