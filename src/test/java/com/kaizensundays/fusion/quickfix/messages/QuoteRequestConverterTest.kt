package com.kaizensundays.fusion.quickfix.messages

import org.junit.Test
import quickfix.field.BeginString
import quickfix.field.LegProduct
import quickfix.field.LegSymbol
import quickfix.field.MsgType
import quickfix.field.NoLegs
import quickfix.field.NoRelatedSym
import quickfix.field.QuoteType
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
            "ABNB",
            arrayOf(
                QuoteRequest.NoRelatedSym(
                    QuoteType.INDICATIVE, arrayOf(
                        QuoteRequest.NoRelatedSym.NoLegs("ABNB.1", 1),
                        QuoteRequest.NoRelatedSym.NoLegs("ABNB.2", 3),
                    )
                )
            )
        ),
        factory.quoteRequest(
            "AMZN",
            arrayOf(
                QuoteRequest.NoRelatedSym(
                    QuoteType.INDICATIVE,
                    arrayOf(
                        QuoteRequest.NoRelatedSym.NoLegs("AMZN.1", 1),
                        QuoteRequest.NoRelatedSym.NoLegs("AMZN.2", 3),
                    )
                )
            )
        ),
        factory.quoteRequest(
            "UBER",
            arrayOf(
                QuoteRequest.NoRelatedSym(
                    QuoteType.INDICATIVE,
                    arrayOf(
                        QuoteRequest.NoRelatedSym.NoLegs("UBER.1", 1),
                        QuoteRequest.NoRelatedSym.NoLegs("UBER.2", 3),
                    )
                )
            )
        ),
    )

    @Test
    fun hasComponent() {
        assertTrue(dictionary.hasComponent("instrumentLeg"))
    }

    @Test
    fun fromObject() {

        objs.forEach { obj ->
            val msg = converter.fromObject(obj)

            with(msg) {
                assertEquals(obj.beginString, header.getString(BeginString.FIELD))
                assertEquals(obj.msgType, header.getString(MsgType.FIELD))
                assertEquals(obj.senderCompID, header.getString(SenderCompID.FIELD))
                assertEquals(obj.targetCompID, header.getString(TargetCompID.FIELD))

                assertEquals(obj.symbol, getString(Symbol.FIELD))
                assertEquals(obj.noRelatedSym, getInt(NoRelatedSym.FIELD))

                assertEquals("2022-07-03T17:11:03", getUtcTimeStamp(TransactTime.FIELD).toString())

                assertEquals(obj.noRelatedSymGroup.size, getInt(NoRelatedSym.FIELD))
                val groups = msg.getGroups(NoRelatedSym.FIELD)
                groups.forEachIndexed { i, group ->
                    assertEquals(obj.noRelatedSymGroup[i].quoteType, group.getInt(QuoteType.FIELD))

                    assertEquals(obj.noRelatedSymGroup[i].noLegsGroup.size, group.getInt(NoLegs.FIELD))
                    val noLegs = group.getGroups(NoLegs.FIELD)
                    assertEquals(obj.noRelatedSymGroup[i].noLegsGroup.size, noLegs.size)

                    noLegs.forEachIndexed { j, noLeg ->
                        val noLegsGroup = obj.noRelatedSymGroup[i].noLegsGroup[j]
                        assertEquals(noLegsGroup.legSymbol, noLeg.getString(LegSymbol.FIELD))
                        assertEquals(noLegsGroup.legProduct, noLeg.getInt(LegProduct.FIELD))
                    }
                }
            }

        }

    }

    @Test
    fun toObject() {

        objs.forEachIndexed { i, _ ->
            val msg = converter.fromObject(objs[i])

            val obj = converter.toObject(msg)

            assertTrue(obj is QuoteRequest)

            with(msg) {
                assertEquals(header.getString(BeginString.FIELD), obj.beginString)
                assertEquals(header.getString(MsgType.FIELD), obj.msgType)
                assertEquals(header.getString(SenderCompID.FIELD), obj.senderCompID)
                assertEquals(header.getString(TargetCompID.FIELD), obj.targetCompID)

                assertEquals(obj.symbol, getString(Symbol.FIELD))
                assertEquals(obj.noRelatedSym, getInt(NoRelatedSym.FIELD))

                assertEquals(1656868263000, obj.transactTime)
            }
        }

    }

}