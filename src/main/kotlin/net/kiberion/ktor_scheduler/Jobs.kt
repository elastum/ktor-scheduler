package net.kiberion.ktor_scheduler

import io.ktor.application.Application
import io.ktor.application.feature
import io.ktor.util.pipeline.ContextDsl
import org.jobrunr.jobs.lambdas.JobLambda
import java.util.*

@ContextDsl
fun Application.schedule(configuration: Scheduler.() -> Unit): Scheduler =
    feature(Scheduler).apply(configuration)

@ContextDsl
fun Scheduler.recurringJob(
    id: String,
    cron: String,
    job: JobLambda,
): String {
    return scheduleRecurringJob(id, cron, job)
}

@ContextDsl
fun Scheduler.enqueuedTask(
    job: JobLambda,
): UUID {
    return scheduleEnqueuedTask(job)
}
