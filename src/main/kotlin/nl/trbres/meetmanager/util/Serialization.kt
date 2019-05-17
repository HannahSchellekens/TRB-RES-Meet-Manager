package nl.trbres.meetmanager.util

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.io.*
import java.net.URL

/**
 * @author Hannah Schellekens
 */
object Serialization {

    @JvmStatic
    val mapper by lazy { jacksonObjectMapper() }
}

infix fun Any?.serializeTo(stream: OutputStream) = Serialization.mapper.writeValue(stream, this)
infix fun Any?.serializeTo(writer: Writer) = Serialization.mapper.writeValue(writer, this)
infix fun Any?.serializeTo(dataOutput: DataOutput) = Serialization.mapper.writeValue(dataOutput, this)
infix fun Any?.serializeTo(file: File) = Serialization.mapper.writeValue(file, this)
infix fun Any?.serializeTo(fileName: String) = serializeTo(File(fileName))

fun Any?.serializeToString() = Serialization.mapper.writeValueAsString(this)!!
fun Any?.serializeToBytes() = Serialization.mapper.writeValueAsBytes(this)!!

inline fun <reified T> deserializeFrom(stream: InputStream): T = Serialization.mapper.readValue(stream, T::class.java)
inline fun <reified T> deserializeFrom(reader: Reader): T = Serialization.mapper.readValue(reader, T::class.java)
inline fun <reified T> deserializeFrom(dataInput: DataInput): T = Serialization.mapper.readValue(dataInput, T::class.java)
inline fun <reified T> deserializeFrom(url: URL): T = Serialization.mapper.readValue(url, T::class.java)
inline fun <reified T> deserializeFrom(bytes: ByteArray): T = Serialization.mapper.readValue(bytes, T::class.java)
inline fun <reified T> deserializeFrom(string: String): T = Serialization.mapper.readValue(string, T::class.java)
inline fun <reified T> deserializeFrom(file: File): T = Serialization.mapper.readValue(file, T::class.java)
inline fun <reified T> deserializeFromFile(fileName: String): T = deserializeFrom(File(fileName))

inline fun <reified T> InputStream.deserialize(): T = Serialization.mapper.readValue(this, T::class.java)
inline fun <reified T> Reader.deserialize(): T = Serialization.mapper.readValue(this, T::class.java)
inline fun <reified T> DataInput.deserialize(): T = Serialization.mapper.readValue(this, T::class.java)
inline fun <reified T> URL.deserialize(): T = Serialization.mapper.readValue(this, T::class.java)
inline fun <reified T> ByteArray.deserialize(): T = Serialization.mapper.readValue(this, T::class.java)
inline fun <reified T> String.deserialize(): T = Serialization.mapper.readValue(this, T::class.java)
inline fun <reified T> File.deserialize(): T = Serialization.mapper.readValue(this, T::class.java)
inline fun <reified T> String.deserializeFile(): T = File(this).deserialize()