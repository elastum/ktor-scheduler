package net.kiberion.ktor_scheduler

import io.ktor.application.Application
import io.ktor.application.feature
import io.ktor.util.pipeline.ContextDsl
import org.jobrunr.jobs.lambdas.JobLambda

@ContextDsl
fun Application.recurringJob(configuration: Scheduler.() -> Unit): Scheduler =
    feature(Scheduler).apply(configuration)

@ContextDsl
fun Scheduler.schedule(
    cron: String,
    job: JobLambda,
): String {
    return recurringJob(cron, job)
}
