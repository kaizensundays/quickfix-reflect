package com.kaizensundays.fusion.quickfix.messages

import com.kaizensundays.fusion.quickfix.firstCharToUpper
import com.kaizensundays.fusion.quickfix.toLocalDateTime
import quickfix.FieldMap
import quickfix.Group
import quickfix.Message
import quickfix.field.MsgType
import java.lang.reflect.Field
import java.lang.reflect.Modifier

/**
 * Created: Saturday 6/25/2022, 12:32 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
typealias SetTag = FieldMap.(Int, Field, Any) -> Unit
typealias SetField = Any.(Field, Int, FieldMap) -> Unit

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

    private fun Any.getAndSet(field: Field, set: (Any) -> Unit) {
        val value = field.get(this)
        if (value != null) {
            set(value)
        }
    }

    val setCharTag: SetTag = { tag, field, obj ->
        val value = field.get(obj)
        if (value != null) {
            this.setChar(tag, field.get(obj) as Char)
        }
    }

    val setStringField: SetField = { field, tag, msg ->
        if (msg.isSetField(tag)) {
            val value = msg.getString(tag)
            if (!field.isFinal()) {
                field.set(this, value)
            }
        }
    }

    private val setStringTag: SetTag = { tag, field, obj ->
        obj.getAndSet(field) { value -> this.setString(tag, value as String) }
    }

    val setIntField: SetField = { field, tag, msg ->
        if (msg.isSetField(tag)) {
            val value = msg.getInt(tag)
            if (!field.isFinal()) {
                field.set(this, value)
            }
        }
    }

    private val setIntTag: SetTag = { tag, field, obj ->
        obj.getAndSet(field) { value -> this.setInt(tag, value as Int) }
    }

    val setLongTag: SetTag = { tag, field, obj ->
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
    }

    val setDoubleTag: SetTag = { tag, field, obj ->
        val value = field.get(obj)
        if (value != null) {
            this.setDouble(tag, field.get(obj) as Double)
        }
    }

    val setTagMap: Map<Class<*>, FieldMap.(tag: Int, field: Field, obj: Any) -> Unit> = mapOf(
        Character::class.java to setCharTag,
        String::class.java to setStringTag,
        Integer::class.java to setIntTag,
        java.lang.Long::class.java to setLongTag,
        java.lang.Double::class.java to setDoubleTag
    )

    private val setFieldMap: Map<Class<*>, SetField> = mapOf(
        String::class.java to setStringField,
        Integer::class.java to setIntField,
    )

    private fun Field.isFinal() = Modifier.isFinal(this.modifiers)

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

            val setTag = setTagMap[field.type]
            if (setTag != null) {
                this.setTag(tag, field, obj)
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

    fun FieldMap.getFields(type: Class<*>, obj: FixMessage) {

        val fieldMap = type.declaredFields.map { f -> f.name.firstCharToUpper() to f }.toMap()

        val names = fieldMap.map { entry -> entry.key }

        names.forEach { name ->
            val field = fieldMap[name]
            if (field != null) {
                val tag = tag(field.name)
                if (tag != null) {
                    val setField = setFieldMap[field.type]
                    if (setField != null) {
                        obj.setField(field, tag, this)
                    }
                }
            }
        }
    }

    override fun toObject(msg: Message): FixMessage {


        val obj = msgTypeToJavaTypeMap[msg.header.getString(MsgType.FIELD)]?.invoke() ?: throw IllegalStateException()

        msg.header.getFields(FixMessage::class.java, obj)

        msg.getFields(obj.javaClass, obj)

        return obj
    }

}