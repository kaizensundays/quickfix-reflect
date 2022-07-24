package com.kaizensundays.fusion.quickfix

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

/**
 * Created: Saturday 6/25/2022, 12:49 PM Eastern Time
 *
 * @author Sergey Chuykov
 */

fun String.firstCharToUpper() = replaceFirstChar { c -> c.uppercase() }

fun toLocalDateTime(timestamp: Long): LocalDateTime {
    val inst = Instant.ofEpochMilli(timestamp)
    return LocalDateTime.ofEpochSecond(inst.epochSecond, inst.nano, ZoneOffset.UTC)
}


fun toEpochMilli(ldt: LocalDateTime): Long {
    return ldt.toInstant(ZoneOffset.UTC).toEpochMilli()
}
