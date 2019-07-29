package dev.olog.shared.widgets.adaptive

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import dev.olog.shared.lazyFast
import dev.olog.shared.widgets.ForegroundImageView

open class AdaptiveColorImageView @JvmOverloads constructor(
        context: Context,
        attr: AttributeSet? = null

) : ForegroundImageView(context, attr) {

    private val presenter by lazyFast {
        AdaptiveColorImageViewPresenter(
            context
        )
    }

    override fun setImageBitmap(bm: Bitmap?) {
        super.setImageBitmap(bm)
        if (!isInEditMode){
            presenter.onNextImage(bm)
        }
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        if (isInEditMode){
            return
        }

        presenter.onNextImage(drawable)
    }

    fun observeProcessorColors() = presenter.observeProcessorColors()
    fun observePaletteColors() = presenter.observePalette()

}