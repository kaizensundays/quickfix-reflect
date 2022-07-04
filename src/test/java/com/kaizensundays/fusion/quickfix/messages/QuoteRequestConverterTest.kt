package com.kaizensundays.fusion.quickfix.messages

import org.junit.Test
import quickfix.field.BeginString
import quickfix.field.LegSymbol
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
        factory.quoteRequest(
            "ABNB", 2,
            arrayOf(InstrumentLeg("ABNB.1"), InstrumentLeg("ABNB.2"))
        ),
        factory.quoteRequest(
            "AMZN", 2,
            arrayOf(InstrumentLeg("AMZN.1"), InstrumentLeg("AMZN.2"))
        ),
        factory.quoteRequest(
            "UBER", 2,
            arrayOf(InstrumentLeg("UBER.1"), InstrumentLeg("UBER.2"))
        ),
    )

    @Test
    fun hasComponent() {
        assertTrue(dictionary.hasComponent("instrumentLeg"))
    }

    @Test
    fun fromObject() {

        objs.forEachIndexed { _, obj ->
            val msg = converter.fromObject(obj)

            with(msg) {
                assertEquals(obj.beginString, header.getString(BeginString.FIELD))
                assertEquals(obj.msgType, header.getString(MsgType.FIELD))
                assertEquals(obj.senderCompID, header.getString(SenderCompID.FIELD))
                assertEquals(obj.targetCompID, header.getString(TargetCompID.FIELD))

                assertEquals(obj.symbol, getString(Symbol.FIELD))
                assertEquals(obj.noRelatedSym, getInt(NoRelatedSym.FIELD))

                assertEquals(obj.instrumentLeg.size, getInt(NoLegs.FIELD))

                assertEquals("2022-07-03T17:11:03", getUtcTimeStamp(TransactTime.FIELD).toString())

                val groups = msg.getGroups(NoLegs.FIELD)

                groups.forEachIndexed { i, group ->
                    assertEquals(obj.instrumentLeg[i].legSymbol, group.getString(LegSymbol.FIELD))
                }
            }

        }

    }

}