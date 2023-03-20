package net.kiberion.ktor_scheduler

import io.ktor.server.application.Application
import io.ktor.server.application.plugin
import io.ktor.util.*
import org.jobrunr.jobs.lambdas.JobLambda
import java.util.*

@KtorDsl
fun Application.schedule(configuration: Scheduler.() -> Unit): Scheduler =
    plugin(Scheduler).apply(configuration)

@KtorDsl
fun Scheduler.recurringJob(
    id: String,
    cron: String,
    job: JobLambda,
): String {
    return scheduleRecurringJob(id, cron, job)
}

@KtorDsl
fun Scheduler.enqueuedTask(
    job: JobLambda,
): UUID {
    return scheduleEnqueuedTask(job)
}
