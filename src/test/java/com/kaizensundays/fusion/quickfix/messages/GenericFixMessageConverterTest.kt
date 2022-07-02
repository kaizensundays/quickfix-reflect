package com.kaizensundays.fusion.quickfix.messages

import org.junit.Before
import org.junit.Test
import quickfix.field.BeginString
import quickfix.field.MaturityMonthYear
import quickfix.field.MsgType
import quickfix.field.OrderQty
import quickfix.field.SenderCompID
import quickfix.field.Side
import quickfix.field.Symbol
import quickfix.field.TargetCompID
import quickfix.field.TransactTime
import kotlin.test.assertEquals

/**
 * Created: Saturday 6/25/2022, 12:32 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class GenericFixMessageConverterTest {

    val factory = TestMessageFactory()

    val dictionary = FixDictionary("FIX44.xml")

    val converter = GenericFixMessageConverter(dictionary)

    val objs = arrayOf(
        factory.newOrderSingle(Side.BUY, 100.0, "ABNB"),
        factory.newOrderSingle(Side.SELL, 300.0, "AMZN"),
        factory.newOrderSingle(Side.BUY, 700.0, "UBER"),
    )

    @Before
    fun before() {
        dictionary.init()
    }

    @Test
    fun fromObject() {

        objs.forEachIndexed { i, obj ->
            val msg = converter.fromObject(obj)

            assertEquals(objs[i].beginString, msg.header.getString(BeginString.FIELD))
            assertEquals(objs[i].msgType, msg.header.getString(MsgType.FIELD))
            assertEquals(objs[i].senderCompID, msg.header.getString(SenderCompID.FIELD))
            assertEquals(objs[i].targetCompID, msg.header.getString(TargetCompID.FIELD))

            assertEquals(objs[i].side, msg.getChar(Side.FIELD))
            assertEquals(objs[i].orderQty, msg.getDouble(OrderQty.FIELD))
            assertEquals(objs[i].symbol, msg.getString(Symbol.FIELD))

            assertEquals(objs[i].maturityMonthYear.toInt(), msg.getInt(MaturityMonthYear.FIELD))
            assertEquals("2022-07-03T17:11:03", msg.getUtcTimeStamp(TransactTime.FIELD).toString())
        }

    }

}