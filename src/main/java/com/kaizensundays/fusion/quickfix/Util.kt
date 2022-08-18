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

/*
inline fun Any.getValue(field: Field, set: (Any) -> Unit) {
    val value = field.get(this)
    if (value != null) {
        set(value)
    }
}
*/

/*
val setTransactTimeTag: SetTag = { tag, field, obj, dictionary ->

    val fixField = dictionary.tagToFieldMap()[tag]
    if ("UTCTIMESTAMP" != fixField?.type) {
        throw IllegalArgumentException()
    }

    obj.getValue(field) { value ->
        val timestamp = value as Long
        this.setUtcTimeStamp(tag, toLocalDateTime(timestamp))
    }
}
*/

/*
val setTransactTimeField: SetField = { field, tag, msg, dictionary ->

    val fixField = dictionary.tagToFieldMap()[tag]
    if ("UTCTIMESTAMP" != fixField?.type) {
        throw IllegalArgumentException()
    }

    if (msg.isSetField(tag) && !field.isFinal()) {
        val value = toEpochMilli(msg.getUtcTimeStamp(tag))
        field.set(this, value)
    }
}
*/

