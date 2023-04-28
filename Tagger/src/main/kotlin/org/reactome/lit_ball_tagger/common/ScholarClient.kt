package org.reactome.lit_ball_tagger.common
interface ScholarClient {
//    fun getAbstractFor(doi: String): String?
}

object S2client : ScholarClient {
    suspend fun getDataFor(doi: String): S2Service.PaperDetails? {
        return S2Service.getPaperDetails("DOI:$doi", "paperId,externalIds,title,abstract,publicationTypes,tldr")
    }
    suspend fun getDataFor(doiSet: List<String>): List<S2Service.PaperDetails>? {
        return S2Service.getBulkPaperDetails(doiSet, "paperId,externalIds,title,abstract,publicationTypes,tldr")
    }

}
