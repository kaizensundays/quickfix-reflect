package com.kaizensundays.fusion.quickfix.messages

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
import kotlin.test.assertTrue

/**
 * Created: Saturday 6/25/2022, 12:32 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class NewOrderSingleConverterTest : GenericFixMessageConverterTestSupport() {

    val objs = arrayOf(
        factory.newOrderSingle(Side.BUY, 100.0, "ABNB"),
        factory.newOrderSingle(Side.SELL, 300.0, "AMZN"),
        factory.newOrderSingle(Side.BUY, 700.0, "UBER"),
    )

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


    @Test
    fun toObject() {

        objs.forEachIndexed { i, _ ->
            val msg = converter.fromObject(objs[i])

            val obj = converter.toObject(msg)

            assertTrue(obj is NewOrderSingle)

            with(msg) {
                assertEquals(header.getString(BeginString.FIELD), obj.beginString)
                assertEquals(header.getString(MsgType.FIELD), obj.msgType)
                assertEquals(header.getString(SenderCompID.FIELD), obj.senderCompID)
                assertEquals(header.getString(TargetCompID.FIELD), obj.targetCompID)

                assertEquals(getChar(Side.FIELD), obj.side)
                assertEquals(getDouble(OrderQty.FIELD), obj.orderQty)
                assertEquals(getString(Symbol.FIELD), obj.symbol)
            }
        }

    }


}