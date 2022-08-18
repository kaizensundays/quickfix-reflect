package com.kaizensundays.fusion.quickfix.messages

import org.junit.Test
import java.time.LocalDateTime
import kotlin.test.assertEquals

/**
 * Created: Saturday 8/13/2022, 12:39 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class TransactTimeConverterTest : GenericFixMessageConverterTestSupport() {

    val obj = TransactTimeConverter(dictionary)

    @Test
    fun convert() {

        arrayOf(
            "2021-03-01T21:01:17",
            "2022-07-03T17:11:03",
            "2023-11-07T07:31:01",
        ).zip(
            arrayOf(
                LocalDateTime.of(2021, 3, 1, 21, 1, 17),
                LocalDateTime.of(2022, 7, 3, 17, 11, 3),
                LocalDateTime.of(2023, 11, 7, 7, 31, 1),
            )
        ).forEach { (exp, date) ->

            val ts = obj.toEpochMilli(date)

            val ldt = obj.toLocalDateTime(ts)

            assertEquals(exp, ldt.toString())
        }

    }

}