package com.kaizensundays.fusion.quickfix.messages

import com.kaizensundays.fusion.quickfix.firstCharToUpper
import com.kaizensundays.fusion.quickfix.toLocalDateTime
import quickfix.FieldMap
import quickfix.Group
import quickfix.Message
import quickfix.field.MsgType
import java.lang.reflect.Field

/**
 * Created: Saturday 6/25/2022, 12:32 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
typealias FixFieldSetter = FieldMap.(Int, Field, Any) -> Unit

typealias GroupFactory = () -> Group

class GenericFixMessageConverter(private val dictionary: FixDictionary) : ObjectConverter<Message, FixMessage> {

    private val msgTypeToJavaTypeMap: Map<*, () -> FixMessage> = mapOf(
        MsgType.QUOTE_REQUEST to { QuoteRequest() }
    )

    private val componentToGroupMap: Map<*, GroupFactory> = mapOf(
        InstrumentLeg::class.java to { quickfix.fix44.QuoteRequest.NoRelatedSym.NoLegs() }
    )

    private val groupFactory: (component: Any) -> Group? = { component ->
        componentToGroupMap[component.javaClass]?.invoke()
    }

    val charSetter: FixFieldSetter = { tag, field, obj ->
        val value = field.get(obj)
        if (value != null) {
            this.setChar(tag, field.get(obj) as Char)
        }
    }

    val stringSetter: FixFieldSetter = { tag, field, obj ->
        val value = field.get(obj)
        if (value != null) {
            this.setString(tag, field.get(obj) as String)
        }
    }

    val intSetter: FixFieldSetter = { tag, field, obj ->
        val value = field.get(obj)
        if (value != null) {
            this.setInt(tag, value as Int)
        }
    }

    val doubleSetter: FixFieldSetter = { tag, field, obj ->
        val value = field.get(obj)
        if (value != null) {
            this.setDouble(tag, field.get(obj) as Double)
        }
    }

    val fixFieldSettersMap: Map<Class<*>, FieldMap.(tag: Int, field: Field, obj: Any) -> Unit> = mapOf(
        Character::class.java to charSetter,
        Integer::class.java to intSetter,
        String::class.java to stringSetter,
        java.lang.Double::class.java to doubleSetter,
        java.lang.Long::class.java to { tag, field, obj ->
            when (fixType(tag)) {
                "UTCTIMESTAMP" -> {
                    val timestamp = field.get(obj) as Long
                    this.setUtcTimeStamp(tag, toLocalDateTime(timestamp))
                }
                "MONTHYEAR" -> {
                    this.setInt(tag, (field.get(obj) as Long).toInt())
                }
                "INT" -> {
                    this.setInt(tag, (field.get(obj) as Long).toInt())
                }
            }
        },
    )


    private fun tag(fieldName: String): Int? {
        val field = dictionary.nameToFieldMap()[fieldName.firstCharToUpper()]
        return field?.number?.toInt()
    }

    fun fixType(tag: Int): String {
        val field = dictionary.tagToFieldMap()[tag]
        return if (field != null) field.type else "?"
    }

    fun FieldMap.set(field: Field, obj: Any) {

        val tag = tag(field.name)
        if (tag != null) {

            val setter = fixFieldSettersMap[field.type]
            if (setter != null) {
                this.setter(tag, field, obj)
            }

        } else if (dictionary.hasComponent(field.name)) {
            if (field.type.isArray) {
                val components = field.get(obj) as Array<Any>
                components.forEach { component ->
                    val group = groupFactory(component)
                    if (group != null) {
                        if (!this.isSetField(group.fieldTag)) {
                            this.setInt(group.fieldTag, components.size)
                        }
                        group.setFields(component.javaClass, component)
                        this.addGroup(group)
                    }
                }
            }
            println()
        }
    }

    fun FieldMap.setFields(type: Class<*>, obj: Any) {

        val fieldMap = type.declaredFields.map { f -> f.name.firstCharToUpper() to f }.toMap()

        val names = fieldMap.map { entry -> entry.key }

        names.forEach { name ->

            val f = fieldMap[name]
            if (f != null) {
                this.set(f, obj)
            }
        }

    }

    override fun fromObject(obj: FixMessage): Message {

        val msg = Message()

        msg.header.setFields(FixMessage::class.java, obj)

        msg.setFields(obj.javaClass, obj)

        return msg
    }

    fun FieldMap.getFields(obj: FixMessage) {

        val type: Class<*> = obj.javaClass

        val fieldMap = type.declaredFields.map { f -> f.name.firstCharToUpper() to f }.toMap()

        val names = fieldMap.map { entry -> entry.key }

        names.forEach { name ->
            val f = fieldMap[name]
            if (f != null) {
                val tag = tag(f.name)
                if (tag != null && this.isSetField(tag)) {
                    val value = this.getString(tag)
                    f.set(obj, value)
                }
            }
        }
    }

    override fun toObject(msg: Message): FixMessage {


        val obj = msgTypeToJavaTypeMap[msg.header.getString(MsgType.FIELD)]?.invoke() ?: throw IllegalStateException()

        msg.getFields(obj)

        return obj
    }

}