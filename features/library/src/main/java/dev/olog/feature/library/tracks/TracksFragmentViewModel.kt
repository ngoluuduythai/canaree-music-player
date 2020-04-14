package dev.olog.feature.library.tracks

import androidx.lifecycle.ViewModel
import dev.olog.domain.entity.sort.SortEntity
import dev.olog.domain.gateway.podcast.PodcastGateway
import dev.olog.domain.gateway.track.TrackGateway
import dev.olog.domain.prefs.SortPreferences
import dev.olog.domain.schedulers.Schedulers
import dev.olog.feature.library.tab.TabFragmentHeaders
import dev.olog.feature.library.tab.toTabDisplayableItem
import dev.olog.feature.presentation.base.model.DisplayableItem
import dev.olog.shared.startWithIfNotEmpty
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class TracksFragmentViewModel @Inject constructor(
    private val headers: TabFragmentHeaders,
    private val trackGateway: TrackGateway,
    private val podcastGateway: PodcastGateway,
    private val sortPreferences: SortPreferences,
    private val schedulers: Schedulers
) : ViewModel() {

    fun data(isPodcast: Boolean): Flow<List<DisplayableItem>> {
        return if (isPodcast) {
            trackGateway.observeAllPodcasts()
        } else {
            trackGateway.observeAllTracks()
        }.map {
            it.map { it.toTabDisplayableItem() }
                .startWithIfNotEmpty(headers.shuffleHeader)
        }.flowOn(schedulers.cpu)
    }

    fun observeAllCurrentPositions() = podcastGateway.observeAllCurrentPositions()
        .map {
            it.groupBy { it.id }.mapValues { it.value[0].position.toInt() }
        }.flowOn(schedulers.cpu)

    fun getAllTracksSortOrder(): SortEntity {
        return sortPreferences.getAllTracksSort()
    }

}