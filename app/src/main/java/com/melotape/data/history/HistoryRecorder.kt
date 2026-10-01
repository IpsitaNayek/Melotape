package com.melotape.data.history

import com.melotape.data.db.dao.PlayHistoryDao
import com.melotape.data.db.entity.PlayHistoryEntity
import com.melotape.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HistoryRecorder @Inject constructor(
    private val playHistoryDao: PlayHistoryDao,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) {
    /**
     * Records the start of a track playback session.
     * Returns the generated history row ID to attach listening duration later.
     */
    suspend fun onTrackStarted(songId: String): Long = withContext(ioDispatcher) {
        val entry = PlayHistoryEntity(
            songId = songId,
            playedAt = System.currentTimeMillis(),
            listenedMs = 0L,
        )
        playHistoryDao.insert(entry)
    }

    /**
     * Adds listened duration to an existing playback session.
     * Called strictly on track change, pause, or track completion — never on UI ticks.
     */
    suspend fun onListenedDuration(historyId: Long, listenedMs: Long) = withContext(ioDispatcher) {
        if (historyId > 0 && listenedMs > 0) {
            playHistoryDao.addListenedTime(historyId, listenedMs)
        }
    }

    /**
     * Total hours of playback across all sessions for Profile screen "Hours spun" stat.
     */
    fun getHoursSpun(): Flow<Double> {
        return playHistoryDao.getTotalListenedMs().map { totalMs ->
            totalMs / (1000.0 * 60.0 * 60.0)
        }
    }

    /**
     * Count of tracks spun today for the analog tape counter.
     */
    fun getPlaysToday(startOfDayTimestamp: Long): Flow<Int> {
        return playHistoryDao.getPlayCountSince(startOfDayTimestamp)
    }
}
