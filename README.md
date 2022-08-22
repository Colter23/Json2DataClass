# Json2DataClass

把 Json 转为 Kotlin 的 data class

此项目之前为 [bilibili-dynamic-mirai-plugin](https://github.com/Colter23/bilibili-dynamic-mirai-plugin) 的一个小工具，现分离出来    

### 使用
- clone 此项目   
- 刷新依赖   
- `src/test/kotlin` 下有个 `Run.kt` 文件，运行里面的方法即可

### 代码

主要代码 [Json2DataClass](src/main/kotlin/Json2DataClass.kt)

```kotlin
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
```
