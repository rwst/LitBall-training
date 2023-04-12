interface ScholarClient {
    fun getAbstractFor(doi: String): String?
}

object S2client : ScholarClient {
    override fun getAbstractFor(doi: String): String? {
        TODO("Not yet implemented")
    }

}

fun getAbstractFor(doi: String): String? {
    val abs = S2client.getAbstractFor(doi)
    if (abs is String) return abs
    return null
}