package com.kurume_nct.studybattleserver.daemon

import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.newSingleThreadContext
import org.joda.time.DateTime
import org.joda.time.Duration
import java.util.concurrent.TimeUnit

/**
 * Created by gedorinku on 2017/10/16.
 */
object DaemonManager {

    val fastUpdateInterval: Duration = Duration.standardSeconds(1L)
    val slowUpdateInterval: Duration = Duration.standardMinutes(1L)
    private val daemons = mutableListOf<Daemon>()

    fun register(daemon: Daemon) = daemons.add(daemon)

    fun startAsync() = async(newSingleThreadContext(javaClass.simpleName)) {
        var lastSlowUpdate = 0L

        while (true) {
            val now = DateTime.now().millis

            if (slowUpdateInterval.millis <= now - lastSlowUpdate) {
                daemons.forEach {
                    it.onSlowUpdate()
                }
                lastSlowUpdate = now
            }

            daemons.forEach {
                it.onFastUpdate()
            }

            delay(fastUpdateInterval.standardSeconds, TimeUnit.SECONDS)
        }
    }
}