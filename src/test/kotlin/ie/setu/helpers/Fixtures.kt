package ie.setu.helpers

import ie.setu.domain.Activity
import ie.setu.domain.User
import ie.setu.domain.db.Users
import ie.setu.domain.repository.UserDAO
import ie.setu.repository.user1
import ie.setu.repository.user2
import ie.setu.repository.user3
import org.jetbrains.exposed.sql.SchemaUtils
import org.joda.time.DateTime

val nonExistingEmail = "112233445566778testUser@xxxxx.xx"
val nonExistingUserId = 5
val nonExistingActivityId = 6
val validName = "Test User 1"
val validEmail = "testuser1@test.com"

val users = arrayListOf<User>(
    User(name = "Alice Wonderland", email = "alice@wonderland.com", id = 1),
    User(name = "Bob Cat", email = "bob@cat.ie", id = 2),
    User(name = "Mary Contrary", email = "mary@contrary.com", id = 3),
    User(name = "Carol Singer", email = "carol@singer.com", id = 4)
)

val activities = arrayListOf<Activity>(

    Activity(id = 1, description = "Run", duration = 40.0, calories = 50, started = DateTime.parse("2025-08-25T07:30:00"), userId = 1),
    Activity(id = 2, description = "Run1", duration = 50.0, calories = 60, started = DateTime.parse("2025-09-25T07:30:00"), userId = 2),
    Activity(id = 3, description = "Run2", duration = 60.0, calories = 70, started = DateTime.parse("2025-10-25T07:30:00"), userId = 3),
    Activity(id = 4, description = "Run3", duration = 70.0, calories = 80, started = DateTime.parse("2025-11-25T07:30:00"), userId = 4),
    Activity(id = 5, description = "Run4", duration = 80.0, calories = 90, started = DateTime.parse("2025-12-25T07:30:00"), userId = 1)
)

fun populateUserTable(): UserDAO{
    SchemaUtils.create(Users)
    val userDAO = UserDAO()
    userDAO.save(user1)
    userDAO.save(user2)
    userDAO.save(user3)
    return userDAO
}






