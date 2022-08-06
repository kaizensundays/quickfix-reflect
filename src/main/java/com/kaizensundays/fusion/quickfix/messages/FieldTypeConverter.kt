package com.kaizensundays.fusion.quickfix.messages

import javafx.util.Pair

/**
 * Created: Saturday 8/6/2022, 1:26 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
interface FieldTypeConverter {

    fun converters(): Map<Pair<Class<*>, Class<*>>, SetTag>

}