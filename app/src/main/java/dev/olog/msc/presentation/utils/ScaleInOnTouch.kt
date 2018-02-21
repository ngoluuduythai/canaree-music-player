package dev.olog.msc.presentation.utils

import android.content.Context
import android.view.View
import dev.olog.msc.R

class ScaleInOnTouch (
        private val view: View

) : ElevateOnTouch() {

    override fun animate(context: Context) {
        setAnimationAndPlay(view, R.animator.scale_in)
    }

    override fun restore(context: Context) {
        setAnimationAndPlay(view, R.animator.restore)
    }
}