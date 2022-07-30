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

    private fun walkObj(type: Class<*>, obj: Any, fieldAction: (field: Field) -> Unit) {

        val fields = type.declaredFields

        fields.forEach { f ->
            if (!f.isList()) {
                fieldAction.invoke(f)
            } else {
                val list = f.get(obj) as MutableList<Any>
                list.forEach { e ->
                    walkObj(e.javaClass, e, fieldAction)
                }
            }
        }

    }

    fun walk(obj: FixMessage, fieldAction: (field: Field) -> Unit) {

        walkObj(FixMessage::class.java, obj, fieldAction)

        walkObj(obj.javaClass, obj, fieldAction)

    }

    fun fromObject(obj: FixMessage): Message {
        val msg = Message()

        return msg
    }

}