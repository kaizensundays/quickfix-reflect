package com.kaizensundays.fusion.quickfix.messages

import quickfix.Group

/**
 * Created: Sunday 7/31/2022, 1:27 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
interface FixGroup {

    fun create(): Group

}