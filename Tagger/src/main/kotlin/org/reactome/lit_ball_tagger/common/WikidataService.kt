package org.reactome.lit_ball_tagger.common

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.BufferedReader
import java.io.InputStreamReader

object WikidataService {
    private const val CHUNK_SIZE = 1000
    fun getPMIDs(dois: List<String>): Map<String, String> {
        return dois.chunked(CHUNK_SIZE).flatMap { doiChunk ->
            val query="""
                SELECT DISTINCT ?doi ?pmid
                WHERE
                {
                  VALUES ?doi { ${"\"" + doiChunk.joinToString ("\" \"" ) + "\"" } }
                  ?item wdt:P356 ?doi.
                  ?item wdt:P698 ?pmid.
                }
            """.trimIndent()
            val jol = doQuery(query)
            jol.map { (it["doi"] ?: "") to (it["pmid"] ?: "") }
        }.toMap()
    }
    fun getPMCs(dois: List<String>): Map<String, String> {
        return dois.chunked(CHUNK_SIZE).flatMap { doiChunk ->
            val query="""
                SELECT DISTINCT ?doi ?pmc
                WHERE
                {
                  VALUES ?doi { ${"\"" + doiChunk.joinToString ("\" \"" ) + "\"" } }
                  ?item wdt:P356 ?doi.
                  ?item wdt:P932 ?pmc.
                }
            """.trimIndent()
            val jol = doQuery(query)
            jol.map { (it["doi"] ?: "") to (it["pmc"] ?: "") }
        }.toMap()
    }
    fun getPubDates(dois: List<String>): Map<String, String> {
        return dois.chunked(CHUNK_SIZE).flatMap { doiChunk ->
            val query="""
                SELECT DISTINCT ?doi ?pDate
                WHERE
                {
                  VALUES ?doi { ${"\"" + doiChunk.joinToString ("\" \"" ) + "\"" } }
                  ?item wdt:P356 ?doi.
                  ?item wdt:P577 ?pDate.
                }
            """.trimIndent()
            val jol = doQuery(query)
            print(jol)
            jol.map { (it["doi"] ?: "") to (it["pDate"] ?: "").split("T")[0] }
        }.toMap()
    }
}

fun doQuery(query: String): MutableList<Map<String, String>> {
    val tempFile = File.createTempFile("litBall-query", ".rq")
    tempFile.writeText(query)
    val result = executeCommand("wd sparql ${tempFile.absolutePath}")
    return Json.decodeFromString<MutableList<Map<String,String>>>(result)
}
fun executeCommand(command: String): String {
    val process = ProcessBuilder()
        .command(*command.split(" ").toTypedArray())
        .start()

    val reader = BufferedReader(InputStreamReader(process.inputStream))
    val output = StringBuilder()

    reader.use {
        var line: String? = it.readLine()
        while (line != null) {
            output.append(line).append("\n")
            line = it.readLine()
        }
    }

    return output.toString()
}
