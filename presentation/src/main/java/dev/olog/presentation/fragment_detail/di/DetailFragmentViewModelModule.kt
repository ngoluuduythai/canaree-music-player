package dev.olog.presentation.fragment_detail.di

import android.arch.lifecycle.ViewModelProviders
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import dagger.multibindings.StringKey
import dev.olog.domain.interactor.detail.*
import dev.olog.presentation.fragment_detail.DetailFragment
import dev.olog.presentation.fragment_detail.DetailFragmentViewModel
import dev.olog.presentation.fragment_detail.DetailFragmentViewModelFactory
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.model.toDisplayableItem
import dev.olog.shared.MediaIdHelper
import io.reactivex.Flowable
import io.reactivex.rxkotlin.toFlowable


@Module
class DetailFragmentViewModelModule {

    @Provides
    internal fun provideViewModel(fragment: DetailFragment,
                                  factory: DetailFragmentViewModelFactory): DetailFragmentViewModel {

        return ViewModelProviders.of(fragment, factory).get(DetailFragmentViewModel::class.java)
    }

    @Provides
    @IntoMap
    @StringKey(MediaIdHelper.MEDIA_ID_BY_FOLDER)
    internal fun provideFolderData(useCase: GetFolderSiblingsUseCase)
            : Flowable<List<DisplayableItem>> {

        return useCase.execute().flatMapSingle {
            it.toFlowable().map { it.toDisplayableItem() }.toList()
        }
    }

    @Provides
    @IntoMap
    @StringKey(MediaIdHelper.MEDIA_ID_BY_PLAYLIST)
    internal fun providePlaylistData(useCase: GetPlaylistSiblingsUseCase)
            : Flowable<List<DisplayableItem>> {

        return useCase.execute().flatMapSingle {
            it.toFlowable().map { it.toDisplayableItem() }.toList()
        }
    }

    @Provides
    @IntoMap
    @StringKey(MediaIdHelper.MEDIA_ID_BY_ALBUM)
    internal fun provideAlbumData(mediaId: String,
                                  useCase: GetAlbumSiblingsUseCase)
            : Flowable<List<DisplayableItem>> {

        return useCase.execute(mediaId).flatMapSingle {
            it.toFlowable().map { it.toDisplayableItem() }.toList()
        }
    }

    @Provides
    @IntoMap
    @StringKey(MediaIdHelper.MEDIA_ID_BY_ARTIST)
    internal fun provideArtistData(mediaId: String,
                                   useCase: GetArtistSiblingsUseCase,
                                   getArtistUseCase: GetArtistUseCase)
            : Flowable<List<DisplayableItem>> {

        return useCase.execute(mediaId).flatMapSingle {
            it.toFlowable()
                    .map { it.toDisplayableItem() }
                    .startWith { getArtistUseCase.execute(mediaId)
                            .map { it.toDisplayableItem() }
                    }
                    .toList()
        }
    }

    @Provides
    @IntoMap
    @StringKey(MediaIdHelper.MEDIA_ID_BY_GENRE)
    internal fun provideGenreData(useCase: GetGenreSiblingsUseCase)
            : Flowable<List<DisplayableItem>> {

        return useCase.execute().flatMapSingle {
            it.toFlowable().map { it.toDisplayableItem() }.toList()
        }
    }


}
