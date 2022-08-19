package com.kaizensundays.fusion.quickfix.messages

import quickfix.FieldMap
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

/**
 * Created: Saturday 8/13/2022, 12:37 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class TransactTimeConverter(private val dictionary: FixDictionary) : TagConverter {

    override fun getTagName(): String {
        return "TransactTime"
    }

    private inline fun Any.getValue(field: Field, set: (Any) -> Unit) {
        val value = field.get(this)
        if (value != null) {
            set(value)
        }
    }

    fun toLocalDateTime(timestamp: Long): LocalDateTime {
        val inst = Instant.ofEpochMilli(timestamp)
        return LocalDateTime.ofEpochSecond(inst.epochSecond, inst.nano, ZoneOffset.UTC)
    }


    fun toEpochMilli(ldt: LocalDateTime): Long {
        return ldt.toInstant(ZoneOffset.UTC).toEpochMilli()
    }

    private fun validate(tag: Int) {
        val fixField = dictionary.tagToFieldMap()[tag]
        if ("UTCTIMESTAMP" != fixField?.type) {
            throw IllegalArgumentException()
        }
    }

    override fun setTag(source: Any, field: Field, target: FieldMap, tag: Int) {

        validate(tag)

        source.getValue(field) { value ->
            val timestamp = value as Long
            target.setUtcTimeStamp(tag, toLocalDateTime(timestamp))
        }
    }

    override fun setField(source: FieldMap, tag: Int, target: Any, field: Field) {

        validate(tag)

        if (source.isSetField(tag) && !field.isFinal()) {
            val value = toEpochMilli(source.getUtcTimeStamp(tag))
            field.set(target, value)
        }

    }

}