package butter.batchprocessing

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.batch.item.ItemProcessor

class PersonItemProcessor : ItemProcessor<Person, Person> {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    override fun process(person: Person): Person {
        val firstNameUpper = person.firstName.uppercase()
        val lastNameUpper = person.lastName.uppercase()

        val transformedPerson = Person(lastNameUpper, firstNameUpper)

        logger.info("Converting ($person) into ($transformedPerson)")

        return transformedPerson
    }
}
