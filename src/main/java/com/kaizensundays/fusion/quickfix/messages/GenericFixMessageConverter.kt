package com.kaizensundays.fusion.quickfix.messages

import com.kaizensundays.fusion.quickfix.firstCharToUpper
import com.kaizensundays.fusion.quickfix.toLocalDateTime
import quickfix.FieldMap
import quickfix.Message
import quickfix.field.MaturityMonthYear
import quickfix.field.TransactTime
import java.lang.reflect.Field

/**
 * Created: Saturday 6/25/2022, 12:32 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class GenericFixMessageConverter(private val dictionary: FixDictionary) : ObjectConverter<Message, FixMessage> {

    val tagToTypeMap = mapOf(
        MaturityMonthYear.FIELD to "INT",
        TransactTime.FIELD to "UTCTIMESTAMP",
    )

    private fun tag(fieldName: String): Int? {
        val field = dictionary.nameToFieldMap()[fieldName.firstCharToUpper()]
        if (field != null) {
            return field.number.toInt()
        }
        return null
    }

    fun FieldMap.set(field: Field, obj: FixMessage) {

        val tag = tag(field.name)
        if (tag != null) {
            when (field.type) {
                Character::class.java -> {
                    this.setChar(tag, field.get(obj) as Char)
                }
                String::class.java -> {
                    this.setString(tag, field.get(obj) as String)
                }
                java.lang.Double::class.java -> {
                    this.setDouble(tag, field.get(obj) as Double)
                }
                java.lang.Long::class.java -> {
                    val fixType = tagToTypeMap[tag]
                    when (fixType) {
                        "UTCTIMESTAMP" -> {
                            val timestamp = field.get(obj) as Long
                            this.setUtcTimeStamp(tag, toLocalDateTime(timestamp))
                        }
                        "INT" -> {
                            this.setInt(tag, (field.get(obj) as Long).toInt())
                        }
                    }
                }
            }
        }
    }

    fun FieldMap.setFields(type: Class<*>, obj: FixMessage) {

        val fieldMap = type.declaredFields.map { f -> f.name.firstCharToUpper() to f }.toMap()

        val names = fieldMap.map { entry -> entry.key }

        names.forEach { name ->

            val f = fieldMap[name]
            if (f != null) {
                this.set(f, obj)
            }
        }

    }

    override fun fromObject(obj: FixMessage): Message {

        val msg = Message()

        msg.header.setFields(FixMessage::class.java, obj)

        msg.setFields(obj.javaClass, obj)

        return msg
    }

    override fun toObject(obj: Message): FixMessage {
        return NewOrderSingle()
    }

}