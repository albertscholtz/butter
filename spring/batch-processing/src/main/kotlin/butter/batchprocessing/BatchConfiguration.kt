package butter.batchprocessing

import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider
import org.springframework.batch.item.database.JdbcBatchItemWriter
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder
import org.springframework.batch.item.file.FlatFileItemReader
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import javax.sql.DataSource

@Configuration
@EnableBatchProcessing
class BatchConfiguration {

    @Autowired
    lateinit var jobBuilderFactory: JobBuilderFactory

    @Autowired
    lateinit var stepBuilderFactory: StepBuilderFactory

    @Bean
    fun reader(): FlatFileItemReader<Person> {
        return FlatFileItemReaderBuilder<Person>()
            .name("personItemReader")
            .resource(ClassPathResource("sample-data.csv"))
            .delimited()
            .names("firstName", "lastName")
            .fieldSetMapper(BeanWrapperFieldSetMapper<Person>().apply { setTargetType(Person().javaClass) })
            .build()
    }

    @Bean
    fun processor() = PersonItemProcessor()

    @Bean
    fun writer(dataSource: DataSource): JdbcBatchItemWriter<Person> =
        JdbcBatchItemWriterBuilder<Person>()
            .itemSqlParameterSourceProvider(BeanPropertyItemSqlParameterSourceProvider())
            .sql("INSERT INTO people (first_name, last_name) VALUES (:firstName, :lastName)")
            .dataSource(dataSource)
            .build()

    @Bean
    fun importUserJob(listener: JobCompletionNotificationListener, step1: Step): Job =
        jobBuilderFactory.get("importUserJob")
            .incrementer(RunIdIncrementer())
            .listener(listener)
            .flow(step1)
            .end()
            .build()

    @Bean
    fun step1(writer: JdbcBatchItemWriter<Person>): Step =
        stepBuilderFactory.get("step1")
            .chunk<Person, Person>(10)
            .reader(reader())
            .processor(processor())
            .writer(writer)
            .build()
}
