package dev.olog.presentation.fragment_detail

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dev.olog.presentation.R
import dev.olog.presentation._base.BaseFragment
import dev.olog.presentation.utils.asLiveData
import dev.olog.presentation.utils.subscribe
import dev.olog.presentation.utils.withArguments
import kotlinx.android.synthetic.main.fragment_detail.view.*
import javax.inject.Inject

class DetailFragment : BaseFragment() {

    companion object {
        const val TAG = "DetailFragment"
        const val ARGUMENTS_MEDIA_ID = "$TAG.arguments.media_id"

        fun newInstance(mediaId: String): DetailFragment {
            return DetailFragment().withArguments(
                    ARGUMENTS_MEDIA_ID to mediaId
            )

        }
    }

    @Inject lateinit var viewModel: DetailFragmentViewModel
    @Inject lateinit var horizontalAdapter: DetailHorizontalAdapter
    @Inject lateinit var adapter: DetailAdapter

    private lateinit var layoutManager: LinearLayoutManager

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.siblingsObservable
                .asLiveData()
                .subscribe(this, horizontalAdapter::updateDataSet)

        viewModel.songListLiveData
                .subscribe(this, adapter::updateDataSet)
    }

    override fun onViewBound(view: View, savedInstanceState: Bundle?) {
        layoutManager = LinearLayoutManager(context)
        view.list.layoutManager = layoutManager
        view.list.adapter = adapter
        view.list.setHasFixedSize(true)
    }

    override fun provideView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }
}