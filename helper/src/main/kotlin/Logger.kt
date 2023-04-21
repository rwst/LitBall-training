import io.github.oshai.KotlinLogging

object Log {
    private val logger = KotlinLogging.logger {}
    fun e(e: Exception) { logger.error(e.message) }
    fun i(e: Exception) { logger.info(e.message) }
    fun i(tag: String, e: Exception) { logger.info(tag + ": " + e.message) }
    fun i(tag: String, s: String) { logger.info("$tag: $s") }
}