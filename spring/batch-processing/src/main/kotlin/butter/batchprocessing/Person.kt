package butter.batchprocessing

class Person() {

    lateinit var lastName: String
    lateinit var firstName: String

    constructor(lastName: String, firstName: String) : this() {
        this.lastName = lastName
        this.firstName = firstName
    }
}
