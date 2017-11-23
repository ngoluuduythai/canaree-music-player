package dev.olog.shared

object MediaIdHelper {

    const val MEDIA_ID_EMPTY = "__EMPTY__"
    const val MEDIA_ID_ROOT = "__ROOT__"
    const val MEDIA_ID_BY_ALL = "__BY_ALL__"
    const val MEDIA_ID_BY_FOLDER = "__BY_FOLDER__"
    const val MEDIA_ID_BY_PLAYLIST = "__BY_PLAYLIST__"
    const val MEDIA_ID_BY_ALBUM = "__BY_ALBUM__"
    const val MEDIA_ID_BY_ARTIST = "__BY_ARTIST__"
    const val MEDIA_ID_BY_GENRE = "__BY_GENRE__"

    private const val CATEGORY_SEPARATOR = '/'
    private const val LEAF_SEPARATOR = '|'

    private fun createCategoryValue(category: String, categoryValue: String): String {
        return category + CATEGORY_SEPARATOR + categoryValue
    }

    fun folderId(value: String): String {
        return createCategoryValue(MEDIA_ID_BY_FOLDER, value)
    }

    fun playlistId(value: Long): String {
        return createCategoryValue(MEDIA_ID_BY_PLAYLIST, value.toString())
    }

    fun songId(value: Long): String {
        return MEDIA_ID_BY_ALL + CATEGORY_SEPARATOR + LEAF_SEPARATOR + value
    }

    fun albumId(value: Long): String {
        return createCategoryValue(MEDIA_ID_BY_ALBUM, value.toString())
    }

    fun artistId(value: Long): String {
        return createCategoryValue(MEDIA_ID_BY_ARTIST, value.toString())
    }

    fun genreId(value: Long): String {
        return createCategoryValue(MEDIA_ID_BY_GENRE, value.toString())
    }

    fun extractCategory(mediaId: String) : String{
        when (mediaId){
            MEDIA_ID_BY_FOLDER,
            MEDIA_ID_BY_PLAYLIST,
            MEDIA_ID_BY_ALL,
            MEDIA_ID_BY_ALBUM,
            MEDIA_ID_BY_ARTIST,
            MEDIA_ID_BY_GENRE -> return mediaId
        }
        val indexOfCategory = mediaId.indexOf(CATEGORY_SEPARATOR)
        if (indexOfCategory == -1){
            throw IllegalArgumentException("invalid media id $mediaId")
        }
        return mediaId.substring(0, indexOfCategory)
    }

    fun extractCategoryValue(mediaId: String): String {
        val indexOfCategory = mediaId.indexOf(CATEGORY_SEPARATOR)
        if (indexOfCategory == -1){
            throw IllegalArgumentException("invalid media id $mediaId")
        }
        val indexOfLeaf = mediaId.indexOf(LEAF_SEPARATOR)
        if (indexOfLeaf == -1) {
            return mediaId.substring(indexOfCategory)
        } else{
            return mediaId.substring(indexOfCategory, indexOfLeaf)
        }
    }

    fun extractLeaf(mediaId: String) : String {
        val indexOfSongId = mediaId.indexOf(LEAF_SEPARATOR)
        if (indexOfSongId == -1){
            throw IllegalArgumentException("invalid media id $mediaId")
        }
        return mediaId.substring(indexOfSongId)
    }

    fun mapCategoryToSource(mediaId: String): Int {
        val category = extractCategory(mediaId)
        when (category) {
            MediaIdHelper.MEDIA_ID_BY_FOLDER -> return 0
            MediaIdHelper.MEDIA_ID_BY_PLAYLIST -> return 1
            MediaIdHelper.MEDIA_ID_BY_ALL -> return 2
            MediaIdHelper.MEDIA_ID_BY_ALBUM -> return 3
            MediaIdHelper.MEDIA_ID_BY_ARTIST -> return 4
            MediaIdHelper.MEDIA_ID_BY_GENRE -> return 5
        }
        throw IllegalArgumentException("invalid media id $mediaId")
    }

}