package dev.olog.presentation.activity_preferences

import android.os.Bundle
import android.preference.PreferenceActivity
import dev.olog.presentation.R
import dev.olog.presentation.utils.extension.setLightStatusBar
import dev.olog.shared_android.isOreo
import kotlinx.android.synthetic.main.activity_preferences.*

class PreferencesActivity : PreferenceActivity() {

    companion object {
        const val REQUEST_CODE = 1221
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (isOreo()){
            window.setLightStatusBar()
        }
        setContentView(R.layout.activity_preferences)
    }

    override fun onResume() {
        super.onResume()
        back.setOnClickListener { onBackPressed() }
    }

    override fun onPause() {
        super.onPause()
        back.setOnClickListener(null)
    }

}