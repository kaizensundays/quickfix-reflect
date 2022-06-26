package com.kaizensundays.fusion.quickfix.messages

import com.kaizensundays.fusion.quickfix.firstCharToUpper
import quickfix.FieldMap
import quickfix.Message
import quickfix.field.MsgType
import quickfix.field.OrderQty
import quickfix.field.SenderCompID
import quickfix.field.Side
import quickfix.field.Symbol
import quickfix.field.TargetCompID
import java.lang.reflect.Field

/**
 * Created: Saturday 6/25/2022, 12:32 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class GenericFixMessageConverter : ObjectConverter<Message, FixMessage> {

    val nameToTagMap = mapOf(
        "MsgType" to MsgType.FIELD,
        "SenderCompID" to SenderCompID.FIELD,
        "TargetCompID" to TargetCompID.FIELD,
        "Side" to Side.FIELD,
        "OrderQty" to OrderQty.FIELD,
        "Symbol" to Symbol.FIELD,
    )

    fun FieldMap.set(field: Field, obj: FixMessage) {

        val tag = nameToTagMap[field.name.firstCharToUpper()]
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
            }
        }
    }

    override fun fromObject(obj: FixMessage): Message {

        val msg = Message()

        // header
        var fieldMap = FixMessage::class.java.declaredFields.map { f -> f.name.firstCharToUpper() to f }.toMap()

        var names = fieldMap.map { entry -> entry.key }

        names.forEach { name ->

            val f = fieldMap[name]
            if (f != null) {
                msg.header.set(f, obj)
            }
        }

        // message body
        fieldMap = obj.javaClass.declaredFields.map { f -> f.name.firstCharToUpper() to f }.toMap()

        names = fieldMap.map { entry -> entry.key }

        names.forEach { name ->

            val f = fieldMap[name]
            if (f != null) {
                msg.set(f, obj)
            }
        }

        return msg
    }

    override fun toObject(obj: Message): FixMessage {
        return NewOrderSingle()
    }

}