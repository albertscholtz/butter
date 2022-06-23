package butter.batchprocessing

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.batch.core.BatchStatus
import org.springframework.batch.core.JobExecution
import org.springframework.batch.core.listener.JobExecutionListenerSupport
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component
import java.sql.ResultSet

@Component
class JobCompletionNotificationListener(val jdbcTemplate: JdbcTemplate) : JobExecutionListenerSupport() {

    private val log: Logger = LoggerFactory.getLogger(this.javaClass)

    override fun afterJob(jobExecution: JobExecution) {
        if (jobExecution.status == BatchStatus.COMPLETED) {
            log.info("!!! JOB FINISHED! Time to verify the results")

            jdbcTemplate.query("SELECT first_name, last_name FROM people") { rs: ResultSet, _: Int ->
                Person(rs.getString(1), rs.getString(2))
            }.forEach { person -> log.info("Found <$person> in the database.") }
        }
    }
}
