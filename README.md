# ktor-scheduler
Cluster-friendly task-scheduler for ktor

## Example usage

```kotlin
fun Application.configureJobs() {
    install(Scheduler) {
        storageProvider = H2StorageProvider(initH2Database())
        threads = 5
    }
  
    schedule {
        recurringJob("someUniqueId", Cron.minutely()) {
            jobPayload() // note that there are severe limitations on what can be included in lambda itself, so in most cases you should extract job logic into a separate function
        }
    }

    fun initH2Database(): DataSource {
        val config = HikariConfig()
        config.setJdbcUrl("jdbc:h2:mem:test_mem")
        config.setUsername("sa")
        config.setPassword("")
        return HikariDataSource(config)
    }

    fun jobPayload() {
        // Job logic here
    }    
}
```
