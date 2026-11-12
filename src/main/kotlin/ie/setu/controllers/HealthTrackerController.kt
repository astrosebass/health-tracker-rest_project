package ie.setu.controllers
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import ie.setu.domain.Activity
import ie.setu.domain.User
import ie.setu.domain.repository.ActivityDAO
import ie.setu.domain.repository.UserDAO
import ie.setu.utils.jsonToObject
import io.javalin.http.Context
import org.joda.time.DateTime


object HealthTrackerController {

    val userDao = UserDAO()
    val activityDAO = ActivityDAO()

    fun getAllUsers(ctx: Context) {
        val users = userDao.getAll()
        if (users.size != 0) {
            ctx.status(200)
        }
        else{
            ctx.status(404)
        }
        ctx.json(users)
    }

    fun getUserByUserId(ctx: Context) {
        val user = userDao.findById(ctx.pathParam("user-id").toInt())
        if (user != null) {
            ctx.json(user)
            ctx.status(200)
        }
        else {
            ctx.status(404)
        }
    }

    fun addUser(ctx: Context) {
        val mapper = jacksonObjectMapper()
        val user = mapper.readValue<User>(ctx.body())
        val userId = userDao.save(user)
        if (userId != null) {
            user.id = userId
            ctx.json(user)
            ctx.status(201)
        }
    }

    fun getUserByEmail(ctx: Context) {
        val email = ctx.pathParam("user-email")
        val user = userDao.findByEmail(email)
        if (user != null) {
            ctx.json(user)
            ctx.status(200)
        }
        else{
            ctx.status(404)
        }
    }

    fun deleteUser(ctx: Context) {
        val userId = ctx.pathParam("user-id").toInt()
        if (userDao.delete(userId) != 0)
            ctx.status(204)
        else
            ctx.status(404)
    }

    fun updateUser(ctx: Context) {
        val userId = ctx.pathParam("user-id").toInt()
        val mapper = jacksonObjectMapper()
        val userUpdates = mapper.readValue<User>(ctx.body())
        if (userDao.update(userId, userUpdates) != 0)
            ctx.status(204)
        else
            ctx.status(404)
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
        val activity = jsonToObject<Activity>(ctx.body())
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
        val activityId = ctx.pathParam("activities-id").toInt()

        val deletedActivities = activityDAO.deleteSpecificActivity(activityId)

        ctx.status(200).json(
            mapOf(
                "message" to "Activities deleted successfully",
                "deleted" to deletedActivities
            )
        )
    }
    fun updateActivity(ctx: Context) {

        val id = ctx.pathParam("activity-id").toInt()

        val activity = jsonToObject<Activity>(ctx.body()).copy(id = id)

        val updatedRows = activityDAO.update(id, activity)

        if (updatedRows > 0) {
            ctx.status(200).json(activity)
        } else {
            ctx.status(404).result("Activity not found")
        }
    }

    fun getActivityById(ctx: Context) {
        val activity = activityDAO.findByActivityId(ctx.pathParam("activities-id").toInt())
        if (activity != null) {
            ctx.json(activity)
        }
    }

}

