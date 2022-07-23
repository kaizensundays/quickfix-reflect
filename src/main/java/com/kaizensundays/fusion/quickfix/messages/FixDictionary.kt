package com.kaizensundays.fusion.quickfix.messages

import com.kaizensundays.fusion.quickfix.firstCharToUpper
import com.kaizensundays.fusion.quickfix.fix44.ComponentType
import com.kaizensundays.fusion.quickfix.fix44.FieldType
import com.kaizensundays.fusion.quickfix.fix44.FixType
import com.kaizensundays.fusion.quickfix.fix44.GroupType
import org.springframework.core.io.ClassPathResource
import quickfix.Message
import quickfix.field.MsgType
import java.io.FileInputStream
import java.util.*
import javax.xml.bind.JAXBContext
import javax.xml.transform.stream.StreamSource

/**
 * Created: Saturday 7/2/2022, 12:48 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class FixDictionary(private val path: String) {

    private var nameToFieldMap: Map<String, FieldType> = emptyMap()
    private var tagToFieldMap: Map<Int, FieldType> = emptyMap()
    private var nameToComponentMap: Map<String, ComponentType> = emptyMap()
    private var msgTypeToGroupsMap: Map<String, List<GroupType>> = emptyMap()
    private var msgTypeToGroupTagMap: Map<String, SortedSet<Int>> = emptyMap()

    fun init() {

        val resource = ClassPathResource("FIX44.xml")

        val context = JAXBContext.newInstance(FixType::class.java)

        val unmarshaller = context.createUnmarshaller()

        val source = StreamSource(FileInputStream(resource.file))

        val element = unmarshaller.unmarshal(source, FixType::class.java)

        val fix = element.value

        val fields = fix.fields.field

        nameToFieldMap = fields.map { field -> field.name to field }.toMap()

        tagToFieldMap = fields.map { field -> field.number.toInt() to field }.toMap()

        val components = fix.components.component

        nameToComponentMap = components.map { c -> c.name to c }.toMap()

        msgTypeToGroupsMap = fix.messages.message.filter { message -> message.fieldOrGroupOrComponent != null }
            .map { message -> message.msgtype to message.fieldOrGroupOrComponent.filterIsInstance<GroupType>() }.toMap()

        msgTypeToGroupTagMap = fix.messages.message.filter { message -> message.fieldOrGroupOrComponent != null }
            .map { message ->
                message.msgtype to message.fieldOrGroupOrComponent.filterIsInstance<GroupType>()
                    .mapNotNull { group -> tag(group.name) }.toSortedSet()
            }.toMap()
    }

    fun nameToFieldMap(): Map<String, FieldType> {
        return nameToFieldMap
    }

    fun tagToFieldMap(): Map<Int, FieldType> {
        return tagToFieldMap
    }

    fun nameToComponentMap(): Map<String, ComponentType> {
        return nameToComponentMap
    }

    fun hasComponent(name: String): Boolean {
        return nameToComponentMap.containsKey(name.firstCharToUpper())
    }

    fun tag(fieldName: String): Int? {
        val field = nameToFieldMap[fieldName.firstCharToUpper()]
        return field?.number?.toInt()
    }

    fun getGroupTags(msg: Message): Set<Int> {
        if (msg.header.isSetField(MsgType.FIELD)) {
            val msgType = msg.header.getString(MsgType.FIELD)
            val tags = msgTypeToGroupTagMap[msgType]
            if (tags != null) {
                return tags
            }
        }
        return emptySet()
    }

}