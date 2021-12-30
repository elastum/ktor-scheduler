package net.kiberion.ktor_scheduler.utils

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import javax.sql.DataSource

fun initH2Database(): DataSource {
    val config = HikariConfig()
    config.setJdbcUrl("jdbc:h2:mem:test_mem")
    config.setUsername("sa")
    config.setPassword("")
    val dataSource = HikariDataSource(config)
    return dataSource
}
