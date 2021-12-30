package net.kiberion.ktor_scheduler

import io.ktor.application.*
import io.ktor.util.pipeline.*
import org.jobrunr.jobs.lambdas.JobLambda

@ContextDsl
fun Application.recurringJob(configuration: Scheduler.() -> Unit): Scheduler =
    feature(Scheduler).apply(configuration)

@ContextDsl
inline fun Scheduler.schedule(
    cron: String,
    job: JobLambda,
): String {
    return recurringJob(cron, job)
}
