package com.kaizensundays.fusion.quickfix

import com.kaizensundays.fusion.quickfix.messages.SetField
import com.kaizensundays.fusion.quickfix.messages.SetTag
import java.lang.reflect.Field
import java.lang.reflect.Modifier
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

inline fun Any.getValue(field: Field, set: (Any) -> Unit) {
    val value = field.get(this)
    if (value != null) {
        set(value)
    }
}

val setTransactTimeTag: SetTag = { tag, field, obj, dictionary ->
    obj.getValue(field) { value ->
        val fixField = dictionary.tagToFieldMap()[tag]
        if (fixField != null) {
            if ("UTCTIMESTAMP" != fixField.type) {
                throw IllegalArgumentException()
            }
            val timestamp = value as Long
            this.setUtcTimeStamp(tag, toLocalDateTime(timestamp))
        }
    }
}

fun Field.isFinal() = Modifier.isFinal(this.modifiers)

val setTransactTimeField: SetField = { field, tag, msg ->
    if (msg.isSetField(tag) && !field.isFinal()) {
        val value = toEpochMilli(msg.getUtcTimeStamp(tag))
        field.set(this, value)
    }
}

