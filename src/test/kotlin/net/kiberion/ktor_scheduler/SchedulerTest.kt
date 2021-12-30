package net.kiberion.ktor_scheduler

import io.ktor.application.*
import io.ktor.server.testing.*
import net.kiberion.ktor_scheduler.utils.initH2Database
import org.awaitility.Awaitility.await
import org.jobrunr.storage.sql.h2.H2StorageProvider
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import java.util.concurrent.TimeUnit

private fun Application.testModule() {
    install(Scheduler) {
        storageProvider = H2StorageProvider(initH2Database())
        threads = 5
    }
}

var counter = 0

class SchedulerTest {

    private fun jobPayload() {
        if (counter > 0) {
            throw IllegalStateException("Exceeded amount of increments")
        }
        counter = 1
    }

    @Test
    fun `should execute recurring job`(): Unit =
        withTestApplication({
            testModule()
            counter = 0
            recurringJob {
                schedule("* * * * *") {
                    jobPayload()
                }
            }

        }) {
            expectThat(counter).isEqualTo(0)
            await().atMost(90, TimeUnit.SECONDS).until {
                counter == 1
            }
        }
}
