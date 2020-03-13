package dev.olog.presentation.popup

import android.view.View
import androidx.appcompat.widget.PopupMenu
import dev.olog.core.gateway.podcast.PodcastAuthorGateway
import dev.olog.core.gateway.podcast.PodcastPlaylistGateway
import dev.olog.core.gateway.track.*
import dev.olog.core.schedulers.Schedulers
import dev.olog.presentation.PresentationId
import dev.olog.presentation.PresentationIdCategory.*
import dev.olog.presentation.popup.album.AlbumPopup
import dev.olog.presentation.popup.artist.ArtistPopup
import dev.olog.presentation.popup.folder.FolderPopup
import dev.olog.presentation.popup.genre.GenrePopup
import dev.olog.presentation.popup.playlist.PlaylistPopup
import dev.olog.presentation.popup.song.SongPopup
import dev.olog.shared.throwNotHandled
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class PopupMenuFactory @Inject constructor(
    private val folderGateway: FolderGateway,
    private val playlistGateway: PlaylistGateway,
    private val trackGateway: TrackGateway,
    private val albumGateway: AlbumGateway,
    private val artistGateway: ArtistGateway,
    private val genreGateway: GenreGateway,
    private val podcastPlaylistGateway: PodcastPlaylistGateway,
    private val podcastAuthorGateway: PodcastAuthorGateway,
    private val listenerFactory: MenuListenerFactory,
    private val schedulers: Schedulers

) {

    suspend fun create(anchor: View, container: View?, mediaId: PresentationId): PopupMenu = withContext(schedulers.io) {
        return@withContext when (val category = mediaId.category) {
            FOLDERS -> getFolderPopup(anchor, container, mediaId)
            PLAYLISTS -> getPlaylistPopup(anchor, container, mediaId)
            SONGS,
            PODCASTS -> getSongPopup(anchor, container, mediaId)
            ALBUMS -> getAlbumPopup(anchor, container, mediaId)
            ARTISTS -> getArtistPopup(anchor, container, mediaId)
            GENRES -> getGenrePopup(anchor, container, mediaId)
            PODCASTS_PLAYLIST -> getPodcastPlaylistPopup(anchor, container, mediaId)
            PODCASTS_AUTHORS -> getPodcastArtistPopup(anchor, container, mediaId)
            else -> throw IllegalArgumentException("invalid category $category")
        }
    }

    private fun getFolderPopup(anchor: View, container: View?, mediaId: PresentationId): FolderPopup {
        val folder = folderGateway.getByParam(mediaId.categoryId)!!
        return when (mediaId) {
            is PresentationId.Category -> {
                FolderPopup(anchor, folder, null, listenerFactory.folder(container, folder, null))
            }
            is PresentationId.Track -> {
                val song = trackGateway.getByParam(mediaId.id)
                FolderPopup(anchor, folder, song, listenerFactory.folder(container, folder, song))
            }
        }
    }

    private fun getPlaylistPopup(anchor: View, container: View?, mediaId: PresentationId): PlaylistPopup {
        val playlist = playlistGateway.getByParam(mediaId.categoryId)!!
        return when (mediaId) {
            is PresentationId.Category -> {
                PlaylistPopup(anchor, playlist, null, listenerFactory.playlist(container, playlist, null))

            }
            is PresentationId.Track -> {
                val song = trackGateway.getByParam(mediaId.id)
                PlaylistPopup(anchor, playlist, song, listenerFactory.playlist(container, playlist, song))
            }
        }
    }

    private fun getSongPopup(anchor: View, container: View?, mediaId: PresentationId): SongPopup {
        return when (mediaId) {
            is PresentationId.Category -> throwNotHandled(mediaId)
            is PresentationId.Track -> {
                val song = trackGateway.getByParam(mediaId.id)!!
                SongPopup(anchor, listenerFactory.song(container, song), song)
            }
        }
    }

    private fun getAlbumPopup(anchor: View, container: View?, mediaId: PresentationId): AlbumPopup {
        val album = albumGateway.getByParam(mediaId.categoryId)!!
        return when (mediaId) {
            is PresentationId.Category -> {
                AlbumPopup(anchor, null, listenerFactory.album(container, album, null))
            }
            is PresentationId.Track -> {
                val song = trackGateway.getByParam(mediaId.id)
                AlbumPopup(anchor, song, listenerFactory.album(container, album, song))
            }
        }
    }

    private fun getArtistPopup(anchor: View, container: View?, mediaId: PresentationId): ArtistPopup {
        val artist = artistGateway.getByParam(mediaId.categoryId)!!
        return when (mediaId) {
            is PresentationId.Category -> {
                ArtistPopup(anchor, artist, null, listenerFactory.artist(container, artist, null))
            }
            is PresentationId.Track -> {
                val song = trackGateway.getByParam(mediaId.id)
                ArtistPopup(anchor, artist, song, listenerFactory.artist(container, artist, song))
            }
        }
    }

    private fun getGenrePopup(anchor: View, container: View?, mediaId: PresentationId): GenrePopup {
        val genre = genreGateway.getByParam(mediaId.categoryId)!!
        return when (mediaId) {
            is PresentationId.Category -> {
                GenrePopup(anchor, genre, null, listenerFactory.genre(container, genre, null))
            }
            is PresentationId.Track -> {
                val song = trackGateway.getByParam(mediaId.id)
                GenrePopup(anchor, genre, song, listenerFactory.genre(container, genre, song))
            }
        }
    }

    private fun getPodcastPlaylistPopup(anchor: View, container: View?, mediaId: PresentationId): PlaylistPopup {
        val playlist = podcastPlaylistGateway.getByParam(mediaId.categoryId)!!
        return when (mediaId) {
            is PresentationId.Category -> {
                PlaylistPopup(anchor, playlist, null, listenerFactory.playlist(container, playlist, null))
            }
            is PresentationId.Track -> {
                val song = trackGateway.getByParam(mediaId.id)
                PlaylistPopup(anchor, playlist, song, listenerFactory.playlist(container, playlist, song))
            }
        }
    }

    private fun getPodcastArtistPopup(anchor: View, container: View?, mediaId: PresentationId): ArtistPopup {
        val artist = podcastAuthorGateway.getByParam(mediaId.categoryId)!!
        return when (mediaId) {
            is PresentationId.Category -> {
                ArtistPopup(anchor, artist, null, listenerFactory.artist(container, artist, null))
            }
            is PresentationId.Track -> {
                val song = trackGateway.getByParam(mediaId.id)
                ArtistPopup(anchor, artist, song, listenerFactory.artist(container, artist, song))
            }
        }
    }

}