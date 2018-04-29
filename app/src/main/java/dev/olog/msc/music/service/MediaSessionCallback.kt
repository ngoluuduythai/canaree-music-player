package dev.olog.msc.music.service

import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.RatingCompat
import android.support.v4.media.session.MediaSessionCompat
import android.view.KeyEvent
import androidx.core.os.bundleOf
import dev.olog.msc.R
import dev.olog.msc.constants.MusicConstants
import dev.olog.msc.dagger.qualifier.ApplicationContext
import dev.olog.msc.dagger.qualifier.ServiceLifecycle
import dev.olog.msc.dagger.scope.PerService
import dev.olog.msc.domain.interactor.favorite.ToggleFavoriteUseCase
import dev.olog.msc.music.service.interfaces.Player
import dev.olog.msc.music.service.interfaces.Queue
import dev.olog.msc.music.service.interfaces.SkipType
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.k.extension.toast
import dev.olog.msc.utils.k.extension.unsubscribe
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
import java.io.File
import javax.inject.Inject

@PerService
class MediaSessionCallback @Inject constructor(
        @ApplicationContext private val context: Context,
        @ServiceLifecycle lifecycle: Lifecycle,
        private val queue: Queue,
        private val player: Player,
        private val repeatMode: RepeatMode,
        private val shuffleMode: ShuffleMode,
        private val mediaButton: MediaButton,
        private val playerState: PlayerState,
        private val toggleFavoriteUseCase: ToggleFavoriteUseCase

): MediaSessionCompat.Callback(), DefaultLifecycleObserver {

    private val subscriptions = CompositeDisposable()
    private var prepareDisposable: Disposable? = null

    init {
        lifecycle.addObserver(this)
        onPrepare()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        subscriptions.clear()
        prepareDisposable.unsubscribe()
    }

    override fun onPrepare() {
        prepareDisposable = queue.prepare()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(player::prepare, Throwable::printStackTrace)
    }

    override fun onPlayFromMediaId(mediaIdAsString: String, extras: Bundle?) {
        if (extras != null){
            val mediaId = MediaId.fromString(mediaIdAsString)

            when {
                extras.isEmpty ||
                        extras.getString(MusicConstants.ARGUMENT_SORT_TYPE) != null ||
                        extras.getString(MusicConstants.ARGUMENT_SORT_ARRANGING) != null -> {
                    queue.handlePlayFromMediaId(mediaId, extras)
                }
                extras.getBoolean(MusicConstants.BUNDLE_MOST_PLAYED, false) -> {
                    queue.handlePlayMostPlayed(mediaId)
                }
                extras.getBoolean(MusicConstants.BUNDLE_RECENTLY_PLAYED, false) -> {
                    queue.handlePlayRecentlyPlayed(mediaId)
                }
                else -> Single.error(Throwable("invalid case $extras"))
            }.observeOn(AndroidSchedulers.mainThread())
                    .subscribe(player::play, Throwable::printStackTrace)
                    .addTo(subscriptions)
        }
    }

    override fun onPlay() {
        doWhenReady ({
            player.resume()
        })
    }

    override fun onPlayFromSearch(query: String, extras: Bundle) {
        queue.handlePlayFromGoogleSearch(query, extras)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(player::play, {
                    playerState.setEmptyQueue()
                    it.printStackTrace()
                })
                .addTo(subscriptions)
    }

    override fun onPause() {
        player.pause(true)
    }

    override fun onStop() {
        onPause()
    }

    override fun onSkipToNext() {
        onSkipToNext(false)
    }

    override fun onSkipToPrevious() {
        doWhenReady ({
            val metadata = queue.handleSkipToPrevious(player.getBookmark())
            player.playNext(metadata, SkipType.SKIP_PREVIOUS)
        }, {
            context.toast(R.string.popup_error_message)
        })
    }

    private fun onTrackEnded(){
        onSkipToNext(true)
    }

    private fun onSkipToNext(trackEnded: Boolean){
        doWhenReady ({
            val metadata = queue.handleSkipToNext(trackEnded)
            if (trackEnded){
                player.playNext(metadata, SkipType.TRACK_ENDED)
            } else {
                player.playNext(metadata, SkipType.SKIP_NEXT)
            }
        }, {
            context.toast(R.string.popup_error_message)
        })
    }

    private fun doWhenReady(action: () -> Unit, error: (() -> Unit)? = null){
        prepareDisposable.unsubscribe()
        if (queue.isReady()){
            try {
                action()
            } catch (ex: Exception){
                error?.invoke()
            }
        } else {
            prepareDisposable = queue.prepare()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        try {
                            player.prepare(it)
                            action()
                        } catch (ex: Exception){
                            error?.invoke()
                        }
                    }, Throwable::printStackTrace)
                    .addTo(subscriptions)
        }
    }

    override fun onSkipToQueueItem(id: Long) {
        val mediaEntity = queue.handleSkipToQueueItem(id)
        player.play(mediaEntity)
    }

    override fun onSeekTo(pos: Long) {
        player.seekTo(pos)
    }

    override fun onSetRating(rating: RatingCompat?) {
        onSetRating(rating, null)
    }

    override fun onSetRating(rating: RatingCompat?, extras: Bundle?) {
        toggleFavoriteUseCase.execute()
    }

    override fun onCustomAction(action: String?, extras: Bundle?) {
        if (action != null){
            when (action){
                MusicConstants.ACTION_SWAP -> queue.handleSwap(extras!!)
                MusicConstants.ACTION_SWAP_RELATIVE -> queue.handleSwapRelative(extras!!)
                MusicConstants.ACTION_REMOVE -> queue.handleRemove(extras!!)
                MusicConstants.ACTION_REMOVE_RELATIVE -> queue.handleRemoveRelative(extras!!)
                MusicConstants.ACTION_SHUFFLE -> {
                    doWhenReady ({
                        val mediaIdAsString = extras!!.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)
                        val mediaId = MediaId.fromString(mediaIdAsString)
                        queue.handlePlayShuffle(mediaId)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(player::play, Throwable::printStackTrace)
                                .addTo(subscriptions)
                    })
                }
                MusicConstants.ACTION_PlAY_FOLDER_TREE -> {
                    doWhenReady({
                        doWhenReady ({
                            val file = extras!!.getString(MusicConstants.ARGUMENT_PlAY_FOLDER_TREE_FILE)
                            val mediaId = MediaId.folderId(file.substring(0, file.lastIndexOf(File.separator)))
                            queue.handlePlayFolderTree(mediaId)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(player::play, Throwable::printStackTrace)
                                    .addTo(subscriptions)
                        })
                    })
                }
            }
        }
    }

    override fun onSetRepeatMode(repeatMode: Int) {
        this.repeatMode.update()
        playerState.toggleSkipToActions(queue.getCurrentPositionInQueue())
        queue.onRepeatModeChanged()
    }

    override fun onSetShuffleMode(unused: Int) {
        val newShuffleMode = this.shuffleMode.update()
        if (newShuffleMode) {
            queue.shuffle()
        } else {
            queue.sort()
        }
        playerState.toggleSkipToActions(queue.getCurrentPositionInQueue())
    }

    override fun onMediaButtonEvent(mediaButtonEvent: Intent): Boolean {
        val event = mediaButtonEvent.getParcelableExtra<KeyEvent>(Intent.EXTRA_KEY_EVENT)

        if (event.action == KeyEvent.ACTION_DOWN) {
            val keyCode = event.keyCode

            when (keyCode) {
                KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> handlePlayPause()
                KeyEvent.KEYCODE_MEDIA_NEXT -> onSkipToNext()
                KeyEvent.KEYCODE_MEDIA_PREVIOUS -> onSkipToPrevious()
                KeyEvent.KEYCODE_MEDIA_STOP -> player.stopService()
                KeyEvent.KEYCODE_MEDIA_PAUSE -> player.pause(false)
                KeyEvent.KEYCODE_MEDIA_PLAY -> onPlay()
                KeyEvent.KEYCODE_MEDIA_FAST_FORWARD -> onTrackEnded()
                else -> mediaButton.onNextEvent(mediaButtonEvent)
            }
        }

        return true
    }



    override fun onAddQueueItem(description: MediaDescriptionCompat) {
        val split = description.mediaId!!.split(",")
        val position = queue.addQueueItem(split.map { it.trim().toLong() })
        playerState.toggleSkipToActions(position)
    }

    /**
     * this function DO NOT KILL service on pause
     */
    fun handlePlayPause() {
        if (player.isPlaying()) {
            player.pause(false)
        } else {
            onPlay()
        }
    }

}