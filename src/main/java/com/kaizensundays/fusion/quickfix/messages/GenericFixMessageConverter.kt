package com.kaizensundays.fusion.quickfix.messages

import com.kaizensundays.fusion.quickfix.firstCharToUpper
import com.kaizensundays.fusion.quickfix.toEpochMilli
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
        MsgType.ORDER_SINGLE to { NewOrderSingle() },
        MsgType.QUOTE_REQUEST to { QuoteRequest() }
    )

    private val componentToGroupMap: Map<*, GroupFactory> = mapOf(
        QuoteRequest.NoRelatedSym::class.java to { quickfix.fix44.QuoteRequest.NoRelatedSym() },
        QuoteRequest.NoRelatedSym.NoLegs::class.java to { quickfix.fix44.QuoteRequest.NoRelatedSym.NoLegs() }
    )

    private val groupFactory: (component: Any) -> Group? = { component ->
        componentToGroupMap[component.javaClass]?.invoke()
    }

    private inline fun Any.getValue(field: Field, set: (Any) -> Unit) {
        val value = field.get(this)
        if (value != null) {
            set(value)
        }
    }

    val setCharTag: SetTag = { tag, field, obj ->
        obj.getValue(field) { value -> this.setChar(tag, value as Char) }
    }

    private val setStringTag: SetTag = { tag, field, obj ->
        obj.getValue(field) { value -> this.setString(tag, value as String) }
    }

    private val setIntTag: SetTag = { tag, field, obj ->
        obj.getValue(field) { value -> this.setInt(tag, value as Int) }
    }

    val setLongTag: SetTag = { tag, field, obj ->
        obj.getValue(field) { value ->
            when (fixType(tag)) {
                "UTCTIMESTAMP" -> {
                    val timestamp = value as Long
                    this.setUtcTimeStamp(tag, toLocalDateTime(timestamp))
                }
                "MONTHYEAR" -> {
                    this.setInt(tag, (value as Long).toInt())
                }
                "INT" -> {
                    this.setInt(tag, (value as Long).toInt())
                }
            }
        }
    }

    val setDoubleTag: SetTag = { tag, field, obj ->
        obj.getValue(field) { value -> this.setDouble(tag, value as Double) }
    }

    val setCharField: SetField = { field, tag, msg ->
        if (msg.isSetField(tag)) {
            val value = msg.getChar(tag)
            if (!field.isFinal()) {
                field.set(this, value)
            }
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

    val setIntField: SetField = { field, tag, msg ->
        if (msg.isSetField(tag)) {
            val value = msg.getInt(tag)
            if (!field.isFinal()) {
                field.set(this, value)
            }
        }
    }

    val setLongField: SetField = { field, tag, msg ->
        if (msg.isSetField(tag) && !field.isFinal()) {
            when (fixType(tag)) {
                "UTCTIMESTAMP" -> {
                    val value = toEpochMilli(msg.getUtcTimeStamp(tag))
                    field.set(this, value)
                }
                "MONTHYEAR" -> {
                    val value = msg.getInt(tag).toLong()
                    field.set(this, value)
                }
            }
        }
    }

    val setDoubleField: SetField = { field, tag, msg ->
        if (msg.isSetField(tag)) {
            val value = msg.getDouble(tag)
            if (!field.isFinal()) {
                field.set(this, value)
            }
        }
    }

    val setTagMap: Map<Class<*>, FieldMap.(tag: Int, field: Field, obj: Any) -> Unit> = mapOf(
        Character::class.java to setCharTag,
        String::class.java to setStringTag,
        Integer::class.java to setIntTag,
        java.lang.Long::class.java to setLongTag,
        java.lang.Double::class.java to setDoubleTag,
    )

    private val setFieldMap: Map<Class<*>, SetField> = mapOf(
        Character::class.java to setCharField,
        String::class.java to setStringField,
        Integer::class.java to setIntField,
        java.lang.Long::class.java to setLongField,
        java.lang.Double::class.java to setDoubleField,
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
        if (tag != null && !field.type.isArray) {

            val setTag = setTagMap[field.type]
            if (setTag != null) {
                this.setTag(tag, field, obj)
            }

        } else if (field.type.isArray) {
            val array = field.get(obj) as Array<Any>
            array.forEach { component ->
                val group = groupFactory(component)
                if (group != null) {
                    if (!this.isSetField(group.fieldTag)) {
                        this.setInt(group.fieldTag, array.size)
                    }
                    group.setFields(component.javaClass, component)
                    this.addGroup(group)
                }
            }
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

        val groupTags = dictionary.getGroupTags(msg)

        groupTags.forEach { tag ->
            val groups = msg.getGroups(tag)
            if (groups != null && groups.isNotEmpty()) {
                println("")
            }
        }

        return obj
    }

}