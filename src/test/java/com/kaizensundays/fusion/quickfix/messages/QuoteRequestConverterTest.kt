package com.kaizensundays.fusion.quickfix.messages

import org.junit.Test
import quickfix.field.BeginString
import quickfix.field.MsgType
import quickfix.field.NoLegs
import quickfix.field.NoRelatedSym
import quickfix.field.SenderCompID
import quickfix.field.Symbol
import quickfix.field.TargetCompID
import quickfix.field.TransactTime
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Created: Sunday 7/3/2022, 12:20 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class QuoteRequestConverterTest : GenericFixMessageConverterTestSupport() {

    val objs = arrayOf(
        factory.quoteRequest("ABNB", 2),
        factory.quoteRequest("AMZN", 2),
        factory.quoteRequest("UBER", 2),
    )

    @Test
    fun hasComponent() {
        assertTrue(dictionary.hasComponent("instrumentLeg"))
    }

    @Test
    fun fromObject() {

        objs.forEachIndexed { i, obj ->
            val msg = converter.fromObject(obj)

            assertEquals(objs[i].beginString, msg.header.getString(BeginString.FIELD))
            assertEquals(objs[i].msgType, msg.header.getString(MsgType.FIELD))
            assertEquals(objs[i].senderCompID, msg.header.getString(SenderCompID.FIELD))
            assertEquals(objs[i].targetCompID, msg.header.getString(TargetCompID.FIELD))

            assertEquals(objs[i].symbol, msg.getString(Symbol.FIELD))
            assertEquals(objs[i].noRelatedSym, msg.getInt(NoRelatedSym.FIELD))

            assertEquals(objs[i].instrumentLeg.size, msg.getInt(NoLegs.FIELD))

            assertEquals("2022-07-03T17:11:03", msg.getUtcTimeStamp(TransactTime.FIELD).toString())
        }

    }

}