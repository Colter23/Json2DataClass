import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import kotlin.io.path.Path

internal class PluginTest {

    @Test
    fun jsonToDataClassFile(): Unit = runBlocking {

        Json2DataClass.allNullMode = true // 全部属性可空模式
        Json2DataClass.noteValue = false // 把接口中的值作为注释展示

        val url = "http://api.bilibili.com/pgc/view/web/season?season_id=6038"
        Json2DataClass.json2DataClassFile("Season", "src/main/kotlin/data", url )

    }

    @Test
    fun jsonToDataClassString(): Unit = runBlocking {

        val url = "http://api.bilibili.com/pgc/view/web/season?season_id=6038"
        val res = Json2DataClass.json2DataClass("Season", url)
        println(res)

    }

    @Test
    fun jsonStringToDataClassFile(): Unit = runBlocking {

        val json = "{\"code\":0,\"message\":\"0\",\"ttl\":1,\"data\": \"\"}"
        Json2DataClass.jsonString2DataClassFile("Demo", "src/main/kotlin/data", json)

    }

    @Test
    fun jsonStringToDataClassString(): Unit = runBlocking {

        val json = "{\"code\":0,\"message\":\"0\",\"ttl\":1,\"data\": \"\"}"
        val res = Json2DataClass.jsonString2DataClass("Demo", json)
        println(res)

    }

}