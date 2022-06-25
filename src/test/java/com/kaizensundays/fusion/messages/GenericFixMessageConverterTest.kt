package com.kaizensundays.fusion.messages

import com.kaizensundays.fusion.quickfix.messages.GenericFixMessageConverter
import org.junit.Test
import quickfix.field.OrderQty
import quickfix.field.SenderCompID
import quickfix.field.Side
import quickfix.field.Symbol
import quickfix.field.TargetCompID
import kotlin.test.assertEquals

/**
 * Created: Saturday 6/25/2022, 12:32 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class GenericFixMessageConverterTest {

    val factory = TestMessageFactory()

    val converter = GenericFixMessageConverter()

    val objs = arrayOf(
        factory.newOrderSingle(Side.BUY, 100.0, "ABNB"),
        factory.newOrderSingle(Side.SELL, 300.0, "AMZN"),
        factory.newOrderSingle(Side.BUY, 700.0, "UBER"),
    )

    @Test
    fun fromObject() {

        objs.forEachIndexed { i, obj ->
            val msg = converter.fromObject(obj)

            assertEquals(objs[i].senderCompID, msg.getString(SenderCompID.FIELD))
            assertEquals(objs[i].targetCompID, msg.getString(TargetCompID.FIELD))

            assertEquals(objs[i].side, msg.getChar(Side.FIELD))
            assertEquals(objs[i].orderQty, msg.getDouble(OrderQty.FIELD))
            assertEquals(objs[i].symbol, msg.getString(Symbol.FIELD))
        }

    }

}