package net.kiberion.ktor_scheduler

import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.server.testing.withTestApplication
import net.kiberion.ktor_scheduler.utils.initH2Database
import org.awaitility.Awaitility.await
import org.jobrunr.scheduling.cron.Cron
import org.jobrunr.storage.sql.h2.H2StorageProvider
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isLessThan
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

private fun Application.testModule() {
    install(Scheduler) {
        storageProvider = H2StorageProvider(initH2Database())
        threads = 3
    }
}

var atomicCounter = AtomicInteger()

class SchedulerTest {

    private fun jobPayload(addedValue: Int) {
        val oldValue = atomicCounter.getAndAdd(addedValue)
        if (oldValue > 0) {
            throw IllegalStateException("Exceeded amount of increments")
        }
    }

    private fun jobPayloadInfinite(addedValue: Int) {
        atomicCounter.getAndAdd(addedValue)
    }

    private fun taskPayload(addedValue: Int) {
        atomicCounter.getAndAdd(addedValue)
    }

    @Test
    fun `should execute recurring job`(): Unit =
        withTestApplication({
            testModule()
            atomicCounter.set(0)
            schedule {
                recurringJob("incCounter", Cron.minutely()) {
                    jobPayload(1)
                }
            }
        }) {
            expectThat(atomicCounter.get()).isEqualTo(0)
            await().atMost(90, TimeUnit.SECONDS).until {
                atomicCounter.get() == 1
            }
        }

    @Test
    fun `should override existing recurring job when id match`(): Unit =
        withTestApplication({
            testModule()
            atomicCounter.set(0)
            schedule {
                recurringJob("incCounter", "* * * * *") {
                    jobPayload(1)
                }
            }
            schedule {
                recurringJob("incCounter", "* * * * *") {
                    jobPayload(2)
                }
            }
        }) {
            expectThat(atomicCounter.get()).isEqualTo(0)
            await().atMost(90, TimeUnit.SECONDS).until {
                atomicCounter.get() == 2
            }
        }

    @Test
    fun `should enqueue multiple tasks`(): Unit =
        withTestApplication({
            testModule()
            atomicCounter.set(0)
        }) {
            val scheduler: Scheduler = this.application.attributes[Scheduler.SchedulerKey]

            scheduler.scheduleEnqueuedTask { taskPayload(1) }
            scheduler.scheduleEnqueuedTask { taskPayload(3) }
            scheduler.scheduleEnqueuedTask { taskPayload(10) }

            expectThat(atomicCounter.get()).isEqualTo(0)
            await().atMost(90, TimeUnit.SECONDS).until {
                atomicCounter.get() == 14
            }
        }

    @Test
    fun `should stop execution after application shutdown`(): Unit =
        withTestApplication({
            testModule()
            atomicCounter.set(0)
            schedule {
                recurringJob("incCounter", Cron.minutely()) {
                    jobPayloadInfinite(1)
                }
            }
        }) {
            this.application.dispose()
            val oldValue = atomicCounter.get()
            expectThat(oldValue).isLessThan(2)
            TimeUnit.SECONDS.sleep(70);
            expectThat(atomicCounter.get()).isEqualTo(oldValue)
        }
}
