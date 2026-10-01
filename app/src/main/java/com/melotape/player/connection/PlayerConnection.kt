package com.melotape.player.connection

import android.content.ComponentName
import android.content.Context
import androidx.core.content.ContextCompat
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.melotape.di.ApplicationScope
import com.melotape.player.service.MusicPlaybackService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerConnection @Inject constructor(
    @param:ApplicationContext private val context: Context,
    @param:ApplicationScope private val applicationScope: CoroutineScope,
) {
    private val _controller = MutableStateFlow<MediaController?>(null)
    val controller: StateFlow<MediaController?> = _controller.asStateFlow()

    private var sessionToken: SessionToken? = null

    init {
        connect()
    }

    fun connect() {
        if (_controller.value != null) return

        applicationScope.launch {
            val token = SessionToken(
                context,
                ComponentName(context, MusicPlaybackService::class.java)
            )
            sessionToken = token

            val controllerFuture = MediaController.Builder(context, token).buildAsync()
            controllerFuture.addListener(
                {
                    try {
                        val mediaController = controllerFuture.get()
                        _controller.value = mediaController
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                },
                ContextCompat.getMainExecutor(context)
            )
        }
    }

    fun disconnect() {
        _controller.value?.release()
        _controller.value = null
    }
}
