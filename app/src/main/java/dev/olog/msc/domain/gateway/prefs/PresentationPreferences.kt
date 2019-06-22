package dev.olog.msc.domain.gateway.prefs

import dev.olog.msc.domain.entity.LibraryCategoryBehavior
import io.reactivex.Completable
import io.reactivex.Observable

interface PresentationPreferences {
    fun getLastBottomViewPage(): Int
    fun setLastBottomViewPage(page: Int)
    fun isFirstAccess(): Boolean
    fun observeVisibleTabs(): Observable<BooleanArray>
    fun getViewPagerLibraryLastPage(): Int
    fun setViewPagerLibraryLastPage(lastPage: Int)
    fun getViewPagerPodcastLastPage(): Int
    fun setViewPagerPodcastLastPage(lastPage: Int)
    fun getLibraryCategories() : List<LibraryCategoryBehavior>
    fun getDefaultLibraryCategories() : List<LibraryCategoryBehavior>
    fun setLibraryCategories(behavior: List<LibraryCategoryBehavior>)
    fun getPodcastLibraryCategories() : List<LibraryCategoryBehavior>
    fun getDefaultPodcastLibraryCategories() : List<LibraryCategoryBehavior>
    fun setPodcastLibraryCategories(behavior: List<LibraryCategoryBehavior>)
    fun observeLibraryNewVisibility(): Observable<Boolean>
    fun observeLibraryRecentPlayedVisibility(): Observable<Boolean>
    fun canShowPodcastCategory(): Boolean

    fun setDefault(): Completable
}