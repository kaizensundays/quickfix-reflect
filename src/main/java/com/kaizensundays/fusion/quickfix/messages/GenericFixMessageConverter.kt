package com.kaizensundays.fusion.quickfix.messages

import com.kaizensundays.fusion.quickfix.firstCharToUpper
import quickfix.Message
import quickfix.field.OrderQty
import quickfix.field.Symbol

/**
 * Created: Saturday 6/25/2022, 12:32 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class GenericFixMessageConverter : ObjectConverter<Message, FixMessage> {

    override fun fromObject(obj: FixMessage): Message {

        val fieldMap = obj.javaClass.fields.map { f -> f.name.firstCharToUpper() to f }.toMap()

        val msg = Message()

        val orderQty = fieldMap["OrderQty"]?.get(obj) as Double
        msg.setDouble(OrderQty.FIELD, orderQty)

        val symbol = fieldMap["Symbol"]?.get(obj) as String
        msg.setString(Symbol.FIELD, symbol)

        return msg
    }

    override fun toObject(obj: Message): FixMessage {
        return NewOrderSingle()
    }

}