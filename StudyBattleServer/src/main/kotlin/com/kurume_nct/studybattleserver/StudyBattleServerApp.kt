package com.kurume_nct.studybattleserver

import org.jetbrains.ktor.application.Application
import org.jetbrains.ktor.application.install
import org.jetbrains.ktor.logging.CallLogging

/**
 * Created by gedorinku on 2017/07/23.
 */
class StudyBattleServerApp {

    fun Application.install() {
        install(CallLogging)
    }
}