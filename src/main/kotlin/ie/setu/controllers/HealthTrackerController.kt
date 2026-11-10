package ie.setu.controllers
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.joda.JodaModule
import ie.setu.domain.repository.UserDAO
import io.javalin.http.Context
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import ie.setu.domain.User
import ie.setu.domain.Activity
import ie.setu.domain.repository.ActivityDAO


object HealthTrackerController {

    private val userDao = UserDAO()
    private val activityDAO = ActivityDAO()

    fun getAllUsers(ctx: Context) {
        ctx.json(userDao.getAll())
    }
    fun getUserByUserId(ctx: Context) {
        val user = userDao.findById(ctx.pathParam("user-id").toInt())
        if (user != null) {
            ctx.json(user)
        }
    }

    fun addUser(ctx: Context) {
        val body = jacksonObjectMapper().readTree(ctx.body())

        val name = body.get("name")?.asText()
        val email = body.get("email")?.asText()

        if (name.isNullOrBlank() || email.isNullOrBlank()) {
            ctx.status(400).result("Faltan name o email")
            return
        }

        val user = User(
            id = 0,
            name = name,
            email = email
        )

        userDao.save(user)

        ctx.status(201).json(user)
    }

    fun getUserByEmail(ctx: Context){
        val user = userDao.findByEmail(ctx.pathParam("user-email"))
        if (user != null) {
            ctx.json(user)
        }
    }

    fun DeleteUser(ctx: Context) {
        userDao.delete(ctx.pathParam("user-id").toInt())

    }
    fun updateUser(ctx: Context) {

        val id = ctx.pathParam("user-id").toInt()
        val body = jacksonObjectMapper().readTree(ctx.body())

        val name = body.get("name")?.asText()
        val email = body.get("email")?.asText()

        if (name.isNullOrBlank() || email.isNullOrBlank()) {
            ctx.status(400).result("Faltan name o email")
            return
        }

        val user = User(
            id = id,
            name = name,
            email = email
        )

        val updatedRows = userDao.update(id, user)

        if (updatedRows > 0) {
            ctx.status(200).json(user)
        } else {
            ctx.status(404).result("User not found")
        }
    }

    //--------------------------------------------------------------
    // ActivityDAO specifics
    //-------------------------------------------------------------

    fun getAllActivities(ctx: Context) {
        //mapper handles the deserialization of Joda date into a String.
        val mapper = jacksonObjectMapper()
            .registerModule(JodaModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        ctx.json(mapper.writeValueAsString( activityDAO.getAll() ))
    }

    fun getActivitiesByUserId(ctx: Context) {
        if (userDao.findById(ctx.pathParam("user-id").toInt()) != null) {
            val activities = activityDAO.findByUserId(ctx.pathParam("user-id").toInt())
            if (activities.isNotEmpty()) {
                //mapper handles the deserialization of Joda date into a String.
                val mapper = jacksonObjectMapper()
                    .registerModule(JodaModule())
                    .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                ctx.json(mapper.writeValueAsString(activities))
            }
        }
    }

    fun addActivity(ctx: Context) {
        //mapper handles the serialisation of Joda date into a String.
        val mapper = jacksonObjectMapper()
            .registerModule(JodaModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        val activity = mapper.readValue<Activity>(ctx.body())
        activityDAO.save(activity)
        ctx.json(activity)
    }

    fun deleteActivitiesByUserId(ctx: Context) {
        val userId = ctx.pathParam("user-id").toInt()

        val deletedActivities = activityDAO.deleteAllByUserId(userId)

        ctx.status(200).json(
            mapOf(
                "message" to "Activities deleted successfully",
                "deleted" to deletedActivities
            )
        )
    }

    fun deleteSpecificActivity(ctx: Context) {
        val userId = ctx.pathParam("activity-id").toInt()

        val deletedActivities = activityDAO.deleteSpecificActivity(userId)

        ctx.status(200).json(
            mapOf(
                "message" to "Activities deleted successfully",
                "deleted" to deletedActivities
            )
        )
    }

}

