package de.schreib.handball.handballtag.server

import com.opentable.db.postgres.embedded.EmbeddedPostgres
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import javax.sql.DataSource

@Configuration
@Profile("test")
class EmbeddedPostgresConfiguration {
    @Bean
    fun dataSource(): DataSource? {
        return EmbeddedPostgres.builder().start().postgresDatabase
    }
}