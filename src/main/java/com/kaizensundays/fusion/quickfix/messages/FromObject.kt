package com.kaizensundays.fusion.quickfix.messages

import com.kaizensundays.fusion.quickfix.firstCharToUpper
import quickfix.FieldMap
import quickfix.Message
import java.lang.reflect.Field


/**
 * Created: Saturday 7/30/2022, 12:55 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class FromObject(private val dictionary: FixDictionary) {

    private fun tag(fieldName: String): Int? {
        val field = dictionary.nameToFieldMap()[fieldName.firstCharToUpper()]
        return field?.number?.toInt()
    }

    private fun Field.isList() = this.type.equals(List::class.java)

    fun fieldCopyTo(field: Field, obj: Any, target: FieldMap) {
        val value = field.get(obj)
        if (value != null) {
            val tag = dictionary.tag(field.name)
            if (tag != null) {
                target.setString(tag, value as String)
            }
        }
    }

    private fun walkObj(type: Class<*>, obj: Any, action: (field: Field) -> Unit) {

        val fields = type.declaredFields

        fields.forEach { f ->
            if (!f.isList()) {
                action.invoke(f)
            } else {
                val list = f.get(obj) as MutableList<Any>
                list.forEach { e ->
                    walkObj(e.javaClass, e, action)
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