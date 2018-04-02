package dev.olog.msc.music.service.equalizer

import android.media.audiofx.BassBoost
import dev.olog.msc.domain.interactor.prefs.EqualizerPrefsUseCase
import dev.olog.msc.interfaces.equalizer.IBassBoost
import dev.olog.msc.utils.k.extension.printStackTraceOnDebug
import javax.inject.Inject

class BassBoostImpl @Inject constructor(
        private val equalizerPrefsUseCase: EqualizerPrefsUseCase

) : IBassBoost {

    private var bassBoost : BassBoost? = null

    override fun getStrength(): Int {
        return useOrDefault({ bassBoost!!.roundedStrength.toInt() }, 0)
    }

    override fun setStrength(value: Int) {
        use {
            bassBoost!!.setStrength(value.toShort())
        }
    }

    override fun setEnabled(enabled: Boolean) {
        use {
            bassBoost!!.enabled = enabled
        }
    }

    override fun onAudioSessionIdChanged(audioSessionId: Int) {
        release()

        use {
            bassBoost = BassBoost(0, audioSessionId)
            bassBoost!!.enabled = equalizerPrefsUseCase.isEqualizerEnabled()
        }

        use {
            val properties = equalizerPrefsUseCase.getBassBoostSettings()
            if (properties.isNotBlank()){
                bassBoost!!.properties = BassBoost.Settings(properties)
            }
        }
    }

    override fun release() {
        bassBoost?.let {
            try {
                equalizerPrefsUseCase.saveBassBoostSettings(it.properties.toString())
            } catch (ex: Exception){}
            use {
                it.release()
            }
        }
    }

    private fun use(action: () -> Unit){
        try {
            action()
        } catch (ex: Exception){
            ex.printStackTraceOnDebug()
        }
    }

    private fun <T> useOrDefault(action: () -> T, default: T): T {
        return try {
            action()
        } catch (ex: Exception){
            ex.printStackTraceOnDebug()
            default
        }
    }

}