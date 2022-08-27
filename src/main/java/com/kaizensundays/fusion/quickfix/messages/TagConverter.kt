package com.kaizensundays.fusion.quickfix.messages

import quickfix.FieldMap
import java.lang.reflect.Field

/**
 * Created: Saturday 8/13/2022, 12:36 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
interface TagConverter {

    fun getTagName(): String

    fun setTag(source: Any, field: Field, target: FieldMap, tag: Int)

    fun setField(source: FieldMap, tag: Int, target: Any, field: Field)

}