package com.kaizensundays.fusion.quickfix.messages

import com.kaizensundays.fusion.quickfix.firstCharToUpper
import com.kaizensundays.fusion.quickfix.toEpochMilli
import quickfix.FieldMap
import quickfix.Message
import quickfix.field.MsgType
import java.lang.reflect.Field
import java.lang.reflect.Modifier

/**
 * Created: Saturday 7/30/2022, 12:57 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class ToObject(private val dictionary: FixDictionary) {

    private val msgTypeToJavaTypeMap: Map<*, () -> FixMessage> = mapOf(
        MsgType.ORDER_SINGLE to { NewOrderSingle() },
        MsgType.QUOTE_REQUEST to { QuoteRequest() }
    )

    private fun tag(fieldName: String): Int? {
        val field = dictionary.nameToFieldMap()[fieldName.firstCharToUpper()]
        return field?.number?.toInt()
    }

    fun fixType(tag: Int): String {
        val field = dictionary.tagToFieldMap()[tag]
        return if (field != null) field.type else "?"
    }

    private fun Field.isList() = this.type.equals(List::class.java)

    private fun Field.isFinal() = Modifier.isFinal(this.modifiers)

    private val setCharField: SetField = { field, tag, msg ->
        if (msg.isSetField(tag)) {
            val value = msg.getChar(tag)
            if (!field.isFinal()) {
                field.set(this, value)
            }
        }
    }

    private val setStringField: SetField = { field, tag, msg ->
        if (msg.isSetField(tag)) {
            val value = msg.getString(tag)
            if (!field.isFinal()) {
                field.set(this, value)
            }
        }
    }

    private val setIntField: SetField = { field, tag, msg ->
        if (msg.isSetField(tag)) {
            val value = msg.getInt(tag)
            if (!field.isFinal()) {
                field.set(this, value)
            }
        }
    }

    private val setLongField: SetField = { field, tag, msg ->
        if (msg.isSetField(tag) && !field.isFinal()) {
            when (fixType(tag)) {
                "UTCTIMESTAMP" -> {
                    val value = toEpochMilli(msg.getUtcTimeStamp(tag))
                    field.set(this, value)
                }
                "MONTHYEAR" -> {
                    val value = msg.getInt(tag).toLong()
                    field.set(this, value)
                }
            }
        }
    }

    private val setDoubleField: SetField = { field, tag, msg ->
        if (msg.isSetField(tag)) {
            val value = msg.getDouble(tag)
            if (!field.isFinal()) {
                field.set(this, value)
            }
        }
    }

    private val setFieldMap: Map<Class<*>, SetField> = mapOf(
        Character::class.java to setCharField,
        String::class.java to setStringField,
        Integer::class.java to setIntField,
        java.lang.Long::class.java to setLongField,
        java.lang.Double::class.java to setDoubleField,
    )

    fun set(source: FieldMap, field: Field, target: Any) {
        val tag = tag(field.name)
        if (tag != null && !field.isList()) {
            val setField = setFieldMap[field.type]
            if (setField != null) {
                target.setField(field, tag, source)
            }
        }
    }

    fun set(source: FieldMap, type: Class<*>, target: Any) {
        type.declaredFields.forEach { field -> set(source, field, target) }
    }

    fun toObject(msg: Message): FixMessage {

        val obj = msgTypeToJavaTypeMap[msg.header.getString(MsgType.FIELD)]?.invoke() ?: throw IllegalStateException()

        set(msg.header, FixMessage::class.java, obj)


        return obj
    }

}