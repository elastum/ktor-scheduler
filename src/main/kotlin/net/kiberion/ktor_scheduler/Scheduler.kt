package net.kiberion.ktor_scheduler

import io.ktor.application.Application
import io.ktor.application.ApplicationFeature
import io.ktor.application.ApplicationStopped
import io.ktor.util.AttributeKey
import org.jobrunr.configuration.JobRunr
import org.jobrunr.configuration.JobRunrConfiguration
import org.jobrunr.jobs.lambdas.JobLambda
import java.util.*

class Scheduler(
    val configuration: SchedulerConfiguration,
    val scheduler: JobRunrConfiguration.JobRunrConfigurationResult
) {

    /**
     * Schedule a job that will be repeated on specified time
     * Specifying id that is already in use will override existing job definition
     */
    fun scheduleRecurringJob(
        id: String,
        cron: String,
        job: JobLambda,
    ): String {
        return scheduler.jobScheduler.scheduleRecurrently(id, cron, job)
    }

    /**
     * Add task to a queue that should be processed as soon as possible
     */
    fun scheduleEnqueuedTask(
        job: JobLambda,
    ): UUID {
        return scheduler.jobScheduler.enqueue(job).asUUID()
    }

    companion object Feature : ApplicationFeature<Application, SchedulerConfiguration, Scheduler> {

        val SchedulerKey = AttributeKey<Scheduler>("Scheduler")

        override val key: AttributeKey<Scheduler>
            get() = SchedulerKey

        override fun install(
            pipeline: Application,
            configure: SchedulerConfiguration.() -> Unit
        ): Scheduler {
            val configuration = SchedulerConfiguration.create()
            configuration.apply(configure)

            val jobRunr: JobRunrConfiguration.JobRunrConfigurationResult = JobRunr.configure()
                .useStorageProvider(configuration.storageProvider)
                .useBackgroundJobServer(configuration.threads)
                .initialize()

            val scheduler = Scheduler(configuration, jobRunr)

            pipeline.environment.monitor.subscribe(ApplicationStopped) {
                jobRunr.jobScheduler.shutdown()
                jobRunr.jobRequestScheduler.shutdown()
            }

            pipeline.attributes.put(key, scheduler)

            return scheduler
        }
    }
}
