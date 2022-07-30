package com.kaizensundays.fusion.quickfix.messages

import quickfix.Message
import java.lang.reflect.Field


/**
 * Created: Saturday 7/30/2022, 12:55 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class FromObject {

    private fun Field.isList() = this.type.equals(List::class.java)

    fun walk(obj: Any, fieldAction: (field: Field) -> Unit) {

        val fields = obj.javaClass.declaredFields

        fields.forEach { f ->
            if (f.isList()) {
                val list = f.get(obj) as MutableList<Any>
                list.forEach { e ->
                    walk(e, fieldAction)
                }
            } else {
                fieldAction.invoke(f)
            }
        }

    }

    fun fromObject(obj: FixMessage): Message {
        val msg = Message()

        return msg
    }

}