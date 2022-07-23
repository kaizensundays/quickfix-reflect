package com.kaizensundays.fusion.quickfix

import org.junit.Test
import java.time.LocalDateTime

/**
 * Created: Sunday 6/26/2022, 1:07 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class UtilTest {

    @Test
    fun toLocalDateTime() {

        val ts = System.currentTimeMillis()

        toLocalDateTime(ts)

    }

    @Test
    fun toEpochMilli() {

        toEpochMilli(LocalDateTime.of(2022, 7, 3, 17, 11, 3))

    }

}