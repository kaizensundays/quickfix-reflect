package com.kaizensundays.fusion.quickfix.messages

import com.kaizensundays.fusion.quickfix.getValue
import com.kaizensundays.fusion.quickfix.isFinal
import com.kaizensundays.fusion.quickfix.toEpochMilli
import com.kaizensundays.fusion.quickfix.toLocalDateTime
import quickfix.FieldMap
import java.lang.reflect.Field

/**
 * Created: Saturday 8/13/2022, 12:37 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class TransactTimeConverter(private val dictionary: FixDictionary) : TagConverter {

    override fun getTagName(): String {
        return "TransactTime"
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