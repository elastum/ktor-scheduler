package net.kiberion.ktor_scheduler

import org.jobrunr.storage.AbstractStorageProvider
import org.jobrunr.storage.sql.common.db.dialect.Dialect
import javax.sql.DataSource

class SchedulerConfiguration private constructor() {

    lateinit var storageProvider: AbstractStorageProvider
    var threads: Int = 10

    companion object {
        fun create(): SchedulerConfiguration {
            return SchedulerConfiguration()
        }
    }
}
