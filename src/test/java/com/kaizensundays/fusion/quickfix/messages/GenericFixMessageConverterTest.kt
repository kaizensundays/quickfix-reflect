package com.kaizensundays.fusion.quickfix.messages

import org.junit.Test
import kotlin.test.assertEquals

/**
 * Created: Saturday 6/25/2022, 12:33 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class GenericFixMessageConverterTest {

    @Test
    fun firstCharToUpper() {

        arrayOf("A-z", "Java", "Kotlin", "Side", "Symbol", "OrderQty")
            .zip(arrayOf("a-z", "java", "kotlin", "side", "symbol", "orderQty"))
            .forEach { (expected, s) ->
                assertEquals(expected, s.firstCharToUpper())
            }
    }

}