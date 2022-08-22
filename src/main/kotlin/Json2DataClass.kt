import Json2DataClass.allNullMode
import Json2DataClass.noteValue
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.request.*
import java.nio.file.Path
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.*
import kotlin.io.path.Path

object Json2DataClass {
    /**
     * 全部属性可空模式
     */
    var allNullMode = true

    /**
     *  把接口中的值作为注释展示
     */
    var noteValue = false

    /**
     * 通过 url 获取 json 并转为 data class 保存到文件中
     * @param baseClassName 类名 / 文件名
     * @param path 文件保存的路径
     * @param url 链接
     */
    suspend fun json2DataClassFile(baseClassName: String, path: String, url: String) {
        saveFile(baseClassName, path, json2DataClass(baseClassName, url))
    }

    /**
     * 把 json 字符串并转为 data class 保存到文件中
     * @param baseClassName 类名 / 文件名
     * @param path 文件保存的路径
     * @param jsonStr json 字符串
     */
    suspend fun jsonString2DataClassFile(baseClassName: String, path: String, jsonStr: String) {
        saveFile(baseClassName, path, jsonString2DataClass(baseClassName, jsonStr))
    }

    /**
     * 通过 url 获取 json 并转为 data class
     * @param baseClassName 类名
     * @param url 链接
     */
    suspend fun json2DataClass(baseClassName: String, url: String): String {
        val client = HttpClient(OkHttp)
        val resStr = client.get(url).body<String>()
        return jsonString2DataClass(baseClassName, resStr)
    }

    /**
     * 把 json 字符串并转为 data class
     * @param baseClassName 类名
     * @param jsonStr json 字符串
     */
    fun jsonString2DataClass(baseClassName: String, jsonStr: String) =
         json.parseToJsonElement(jsonStr).jsonObject.decodeJsonObject(baseClassName)

    private suspend fun saveFile(baseClassName: String, path: String, data: String) {
        withContext(Dispatchers.IO) {
            val path = Path(path)
            path.toFile().mkdirs()
            val file = path.resolve("$baseClassName.kt").toFile()
            if (file.exists()) file.delete()
            file.createNewFile()
            file.appendText("import kotlinx.serialization.SerialName\n")
            file.appendText("import kotlinx.serialization.Serializable\n\n")
            file.appendText(data)
        }
    }
}

val json = Json {
    prettyPrint = true
    ignoreUnknownKeys = true
    isLenient = true
    allowStructuredMapKeys = true
}

private fun JsonObject.decodeJsonObject(objName: String): String {
    var obj = ""
    val plus = if (allNullMode) "? = null," else ","
    return buildString {
        appendLine("@Serializable")
        appendLine("data class $objName(")

        entries.forEach {
            val key = it.key.replace(" ", "_")
            if (noteValue && it.value is JsonPrimitive) {
                appendLine("    // ${it.value}")
            }
            appendLine("    @SerialName(\"$key\")")
            try {
                when (it.value) {
                    is JsonPrimitive -> {
                        val attr = it.value.jsonPrimitive.parse()
                        appendLine("    val ${snakeToCamelLowerFirst(key)}: $attr$plus")
                    }
                    is JsonObject -> {
                        val objKey = snakeToCamel(key)
                        appendLine("    val ${snakeToCamelLowerFirst(key)}: $objKey$plus")
                        obj += "\n" + it.value.jsonObject.decodeJsonObject(objKey)
                    }
                    is JsonArray -> {
                        val attr = if (it.value.jsonArray.isEmpty()) {
                            "JsonElement"
                        }else {
                            val arr = it.value.jsonArray.first()
                            if (arr is JsonPrimitive) {
                                arr.jsonPrimitive.parse()
                            } else {
                                val k = snakeToCamel(key)
                                obj += "\n" + arr.jsonObject.decodeJsonObject(k)
                                k
                            }
                        }
                        appendLine("    val ${snakeToCamelLowerFirst(key)}: List<$attr>$plus")
                    }
                    is JsonNull -> {
                        appendLine("    val ${snakeToCamelLowerFirst(key)}: JsonElement? = null,")
                    }
                }
            } catch (e: Exception) {
                println(e)
                println("Error Key: ${it.key}")
            }
        }
        append(")")
        if (obj != "") {
            append("{")
            appendLine(obj.replace("\n", "\n    "))
            append("}")
        }
    }
}

private fun JsonPrimitive.parse() =
    if (intOrNull != null) "Int"
    else if (longOrNull != null) "Long"
    else if (booleanOrNull != null) "Boolean"
    else if (floatOrNull != null) "Float"
    else if (isString) "String"
    else "String"

private fun snakeToCamel(name: String) =
    name.split("_").joinToString("") { s -> s.replaceRange(0, 1, s.first().uppercase()) }

private fun snakeToCamelLowerFirst(name: String): String {
    val k = snakeToCamel(name)
    return k.replaceRange(0, 1, k.first().lowercase())
}

