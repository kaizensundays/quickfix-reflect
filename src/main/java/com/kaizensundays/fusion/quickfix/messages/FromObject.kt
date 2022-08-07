package com.kaizensundays.fusion.quickfix.messages

import com.kaizensundays.fusion.quickfix.firstCharToUpper
import com.kaizensundays.fusion.quickfix.toLocalDateTime
import quickfix.FieldMap
import quickfix.Message
import java.lang.reflect.Field

/**
 * Created: Saturday 7/30/2022, 12:55 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class FromObject(private val dictionary: FixDictionary) {

    private inline fun Any.getValue(field: Field, set: (Any) -> Unit) {
        val value = field.get(this)
        if (value != null) {
            set(value)
        }
    }

    private val setCharTag: SetTag = { tag, field, obj ->
        obj.getValue(field) { value -> this.setChar(tag, value as Char) }
    }

    private val setStringTag: SetTag = { tag, field, obj ->
        obj.getValue(field) { value -> this.setString(tag, value as String) }
    }

    private val setIntTag: SetTag = { tag, field, obj ->
        obj.getValue(field) { value -> this.setInt(tag, value as Int) }
    }

    fun fixType(tag: Int): String {
        val field = dictionary.tagToFieldMap()[tag]
        return if (field != null) field.type else "?"
    }

    val setLongTag: SetTag = { tag, field, obj ->
        obj.getValue(field) { value ->
            when (fixType(tag)) {
                "UTCTIMESTAMP" -> {
                    val timestamp = value as Long
                    this.setUtcTimeStamp(tag, toLocalDateTime(timestamp))
                }
                "MONTHYEAR" -> {
                    this.setInt(tag, (value as Long).toInt())
                }
                "INT" -> {
                    this.setInt(tag, (value as Long).toInt())
                }
            }
        }
    }

    val setDoubleTag: SetTag = { tag, field, obj ->
        obj.getValue(field) { value -> this.setDouble(tag, value as Double) }
    }

    private val setTransactTimeTag: SetTag = { tag, field, obj ->
        obj.getValue(field) { value ->
            val type = fixType(tag)
            if ("UTCTIMESTAMP" != type) {
                throw IllegalArgumentException()
            }
            val timestamp = value as Long
            this.setUtcTimeStamp(tag, toLocalDateTime(timestamp))
        }
    }

    private val setTagByFieldNameMap: Map<String, SetTag> = mapOf(
        "TransactTime" to setTransactTimeTag
    )

    private val setTagMap: Map<Class<*>, SetTag> = mapOf(
        Character::class.java to setCharTag,
        String::class.java to setStringTag,
        Integer::class.java to setIntTag,
        java.lang.Long::class.java to setLongTag,
        java.lang.Double::class.java to setDoubleTag,
    )

    private fun Field.isList() = this.type.equals(List::class.java)

    private fun listCopyTo(field: Field, obj: Any, target: FieldMap) {
        val list = field.get(obj)
        if (list is List<*>) {
            list.filterIsInstance<FixGroup>().forEach { fixGroup ->
                val group = fixGroup.create()
                if (!target.isSetField(group.fieldTag)) {
                    target.setInt(group.fieldTag, list.size)
                }
                val fields = fixGroup.javaClass.declaredFields
                fields.forEach { field ->
                    fieldCopyTo(field, fixGroup, group)
                }
                target.addGroup(group)
            }
        }
    }

    fun fieldCopyTo(field: Field, obj: Any, target: FieldMap) {
        val value = field.get(obj)
        if (value != null) {
            val tag = dictionary.tag(field.name)
            if (tag != null && !field.isList()) {
                var setTag = setTagByFieldNameMap[field.name.firstCharToUpper()]
                if (setTag != null) {
                    target.setTag(tag, field, obj)
                } else {
                    setTag = setTagMap[field.type]
                    if (setTag != null) {
                        target.setTag(tag, field, obj)
                    }
                }
            } else if (field.isList()) {
                listCopyTo(field, obj, target)
            }
        }
    }

    fun fromObject(obj: FixMessage): Message {
        val msg = Message()

        FixMessage::class.java.declaredFields.forEach { field ->
            fieldCopyTo(field, obj, msg.header)
        }

        obj.javaClass.declaredFields.forEach { field ->
            fieldCopyTo(field, obj, msg)
        }

        return msg
    }

}