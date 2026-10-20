package ie.setu.repository

import ie.setu.domain.db.Users
import ie.setu.domain.repository.UserDAO
import ie.setu.helpers.users
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test


//retrieving some test data from Fixtures
val user1 = users[0]
val user2 = users[1]
val user3 = users[2]

class UserDAOTest {

    companion object {

        //Make a connection to a local, in memory H2 database.
        @BeforeAll
        @JvmStatic
        internal fun setupInMemoryDatabaseConnection() {
            println("Connecting to H2 database...")
            Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver", user = "root", password = "")
        }
    }

    internal fun populateUserTable(): UserDAO{
        SchemaUtils.create(Users)
        val userDAO = UserDAO()
        userDAO.save(user1)
        userDAO.save(user2)
        userDAO.save(user3)
        return userDAO
    }

    @Nested
    inner class ReadUsers {

        @Test
        fun `getting all users from a populated table returns all rows`() {
            transaction {

                //Arrange - create and populate table with three users
                //SchemaUtils.create(Users)
                //val userDAO = UserDAO()
                //userDAO.save(user1)
                //userDAO.save(user2)
                //userDAO.save(user3)

                //Arrange - create and populate table with three users
                val userDAO = populateUserTable()

                //Act & Assert
                assertEquals(3, userDAO.getAll().size)
            }
        }

        @Test
        fun `get user by id that doesn't exist, results in no user returned`() {
            transaction {

                //Arrange - create and populate table with three users
                //SchemaUtils.create(Users)
                //val userDAO = UserDAO()
                //userDAO.save(user1)
                //userDAO.save(user2)
                //userDAO.save(user3)

                //Arrange - create and populate table with three users
                val userDAO = populateUserTable()

                //Act & Assert
                assertEquals(null, userDAO.findById(4))
            }
        }
        @Test
        fun `get user by id that exists, results in a correct user returned`() {
            transaction {
                //Arrange - create and populate table with three users
                //SchemaUtils.create(Users)
                //val userDAO = UserDAO()
                //userDAO.save(user1)
                //userDAO.save(user2)
                //userDAO.save(user3)

                //Arrange - create and populate table with three users
                val userDAO = populateUserTable()

                //Act & Assert
                assertEquals(user3, userDAO.findById(user3.id))
            }

        }

    }
    @Nested
    inner class DeleteUsers {

        @Test
        fun `deleting a non-existant user in table results in no deletion`() {
            transaction {

                //Arrange - create and populate table with three users
                val userDAO = populateUserTable()

                //Act & Assert
                assertEquals(3, userDAO.getAll().size)
                userDAO.delete(4)
                assertEquals(3, userDAO.getAll().size)
            }
        }
        @Test
        fun `deleting an existing user in table results in record being deleted`() {
            transaction {

                //Arrange - create and populate table with three users
                val userDAO = populateUserTable()

                //Act & Assert
                assertEquals(3, userDAO.getAll().size)
                userDAO.delete(user3.id)
                assertEquals(2, userDAO.getAll().size)
            }
        }

    }
    @Nested
    inner class AddUsers {

        @Test
        fun `multiple users added to table can be retrieved successfully`() {
            transaction {

                //Arrange - create and populate table with three users
                //SchemaUtils.create(Users)
                //val userDAO = UserDAO()
                //userDAO.save(user1)
                //userDAO.save(user2)
                //userDAO.save(user3)

                //Arrange - create and populate table with three users
                val userDAO = populateUserTable()

                //Act & Assert
                assertEquals(3, userDAO.getAll().size)
                assertEquals(user1, userDAO.findById(user1.id))
                assertEquals(user2, userDAO.findById(user2.id))
                assertEquals(user3, userDAO.findById(user3.id))
            }
        }
    }

}