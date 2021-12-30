package net.kiberion.ktor_scheduler

import org.jobrunr.storage.AbstractStorageProvider

class SchedulerConfiguration private constructor() {

    lateinit var storageProvider: AbstractStorageProvider
    var threads: Int = 10

    companion object {
        fun create(): SchedulerConfiguration {
            return SchedulerConfiguration()
        }
    }
}
