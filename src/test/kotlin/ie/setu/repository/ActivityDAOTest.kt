package ie.setu.repository

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import ie.setu.controllers.HealthTrackerController.activityDAO
import ie.setu.controllers.HealthTrackerController.userDao
import ie.setu.domain.Activity
import ie.setu.domain.User
import ie.setu.domain.db.Activities
import ie.setu.domain.db.Users
import ie.setu.domain.repository.ActivityDAO
import ie.setu.domain.repository.UserDAO
import ie.setu.helpers.activities
import ie.setu.helpers.nonExistingEmail
import ie.setu.helpers.nonExistingUserId
import ie.setu.helpers.users
import io.javalin.http.Context
//import ie.setu.helpers.nonExistingEmail
//import ie.setu.helpers.users
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import ie.setu.helpers.nonExistingActivityId


//retrieving some test data from Fixtures
val activity1 = activities[0]
val activity2 = activities[1]
val activity3 = activities[2]
val activity4 = activities[3]
val activity5 = activities[4]

class ActivityDAOTest {

    companion object {

        //Make a connection to a local, in memory H2 database.
        @BeforeAll
        @JvmStatic
        internal fun setupInMemoryDatabaseConnection() {
            println("Connecting to H2 database...")
            Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver", user = "root", password = "")
        }
    }

    internal fun populateActivityTable(): ActivityDAO {

        SchemaUtils.drop(Activities, Users)
        SchemaUtils.create(Users, Activities)

        val userDAO = UserDAO()

        userDAO.save(users[0])
        userDAO.save(users[1])
        userDAO.save(users[2])
        userDAO.save(users[3])

        val activityDAO = ActivityDAO()

        activityDAO.save(activity1)
        activityDAO.save(activity2)
        activityDAO.save(activity3)
        activityDAO.save(activity4)
        activityDAO.save(activity5)

        return activityDAO
    }

    @Nested
    inner class ReadActivities {

        @Test
        fun `getting all activities from a populated table returns all rows`() {
            transaction {

                val activityDAO = populateActivityTable()

                //Act & Assert
                assertEquals(5, activityDAO.getAll().size)
            }
        }

        @Test
        fun `get activity by id that doesn't exist, results in no activity returned`() {
            transaction {

                //Arrange - create and populate table with four activities
                val activityDAO = populateActivityTable()

                //Act & Assert
                assertEquals(null, activityDAO.findByActivityId(6))
            }
        }

        @Test
        fun `get activity by id that exists, results in a correct activity returned`() {
            transaction {

                //Arrange - create and populate table with four activities
                val activityDAO = populateActivityTable()

                //Act & Assert
                assertEquals(activity1, activityDAO.findByActivityId(1))
            }

        }
        @Test
        fun `get all activities over empty table returns none`() {
            transaction {

                //Arrange - create and setup userDAO object
                SchemaUtils.create(Activities)
                val activityDAO = ActivityDAO()

                //Act & Assert
                assertEquals(0, activityDAO.getAll().size)
            }
        }

        @Test
        fun `get activity by user id that doesn't exist, results in no activity returned`() {
            transaction {

                //Arrange - create and populate table with four activities
                val activityDAO = populateActivityTable()

                //Act & Assert
                assertEquals(null, activityDAO.findByUserId(nonExistingActivityId))
            }
        }

        @Test
        fun `get user by user id that exists, results in correct activity returned`() {
            transaction {

                //Arrange - create and populate table with four activities
                val activityDAO = populateActivityTable()

                //Act & Assert
                assertEquals(listOf(activity2), activityDAO.findByUserId(activity2.userId))
            }
        }

    }
    @Nested
    inner class DeleteActivities {

        @Test
        fun `deleting a non-existant activity in table results in no deletion`() {
            transaction {

                //Arrange - create and populate table with four activities
                val activityDAO = populateActivityTable()

                //Act & Assert
                assertEquals(5, activityDAO.getAll().size)
                activityDAO.deleteSpecificActivity(6)
                assertEquals(5, activityDAO.getAll().size)
            }
        }
        @Test
        fun `deleting an existing activity in table results in record being deleted`() {
            transaction {

                //Arrange - create and populate table with four activities
                val activityDAO = populateActivityTable()

                //Act & Assert
                assertEquals(5, activityDAO.getAll().size)
                activityDAO.deleteSpecificActivity(5)
                assertEquals(4, activityDAO.getAll().size)
            }
        }

        @Test
        fun `deleting a non-existant user activities `() {
            transaction {

                //Arrange - create and populate table with four activities
                val activityDAO = populateActivityTable()

                //Act & Assert
                assertEquals(5, activityDAO.getAll().size)
                activityDAO.deleteAllByUserId(5)
                assertEquals(5, activityDAO.getAll().size)
            }
        }
        @Test
        fun `deleting a existant user activities `() {
            transaction {

                //Arrange - create and populate table with four activities
                val activityDAO = populateActivityTable()

                //Act & Assert
                assertEquals(5, activityDAO.getAll().size)
                activityDAO.deleteAllByUserId(1)
                assertEquals(3, activityDAO.getAll().size)
            }
        }

    }
    @Nested
    inner class UpdateActivities {

        @Test

        fun `updating existing activity in table results in successful update`() {
            transaction {

                //Arrange - create and populate table with four activities
                val activityDAO = populateActivityTable()

                //Act & Assert
                val activity3Updated = Activity(id = 3, description = "Run10", duration = 600.0, calories = 700, started = DateTime.parse("2026-10-25T07:30:00"), userId = 3)
                activityDAO.update(activity3.id, activity3Updated)
                assertEquals(activity3Updated, activityDAO.findByActivityId(3))
            }
        }

        @Test

        fun `updating non-existing activity in table results `() {
            transaction {

                //Arrange - create and populate table with four activities
                val activityDAO = populateActivityTable()

                //Act & Assert

                val activity6Updated = Activity(id = 6, description = "Run10", duration = 600.0, calories = 700, started = DateTime.parse("2026-10-25T07:30:00"), userId = 3)
                activityDAO.update(6, activity6Updated)
                assertEquals(null, activityDAO.findByActivityId(6))
                assertEquals(5, activityDAO.getAll().size)
            }
        }

    }


}

