package dev.olog.presentation.prefs.blacklist

import dev.olog.presentation.BindingsAdapter
import dev.olog.presentation.base.adapter.DataBoundViewHolder
import dev.olog.presentation.base.adapter.SimpleAdapter
import dev.olog.shared.android.extensions.toggleVisibility
import kotlinx.android.synthetic.main.dialog_blacklist_item.view.*

class BlacklistFragmentAdapter : SimpleAdapter<BlacklistModel>() {

    override fun getItemViewType(position: Int): Int = getItem(position).type

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        viewHolder.itemView.setOnClickListener {
            val item = getItem(viewHolder.adapterPosition)
            item.isBlacklisted = !item.isBlacklisted
            notifyItemChanged(viewHolder.adapterPosition)
        }
    }

    override fun bind(holder: DataBoundViewHolder, item: BlacklistModel, position: Int) {
        holder.itemView.apply {
            BindingsAdapter.loadAlbumImage(holder.imageView!!, item.mediaId)
            scrim.toggleVisibility(item.isBlacklisted, true)
            firstText.text = item.title
            secondText.text = item.displayablePath
        }
    }

}