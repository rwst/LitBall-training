import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.io.path.Path
import kotlin.io.path.isReadable

fun main(args: Array<String>) {
    println("Program arguments: ${args.joinToString()}")

    val dPath = "../DOI/"
    val aPath = "../Abstract/"
    // get file names
    val dFiles = File(dPath).listFiles()
    val dFileNameSet = mutableSetOf<String>()
    dFiles?.map { dFileNameSet += it.name }
    val aFiles = File(aPath).listFiles()
    val aFileNameSet = mutableSetOf<String>()
    aFiles?.map { aFileNameSet += it.name }

    for (dfn in dFileNameSet) {
        val doiSet = mutableSetOf<String>()
        val lines = Files.readAllLines(Paths.get(dPath + dfn))
        doiSet.addAll(lines)
        println("Size of $dfn is ${doiSet.size}")
        val afPath = aPath + dfn
        val db = Path(afPath)
        var text = ""
        if (db.isReadable()) {
            text = File(afPath).readText()
        }
        val map = try {
            Json.decodeFromString<MutableMap<String, String?>>(text)
        }
        catch (_: Exception) {
            mutableMapOf()
        }
        for (doi in doiSet) {
            if (map[doi] == null || map[doi]!!.isEmpty()) {
                map[doi] = getAbstractFor(doi)
            }
            break
        }
        text = Json.encodeToString(map)
        File(afPath).writeText(text)
        break
    }
}