package ie.setu.config

import ie.setu.controllers.HealthTrackerController
import ie.setu.utils.jsonObjectMapper
import io.javalin.json.JavalinJackson
import io.javalin.Javalin
import io.javalin.config.JavalinConfig

class ServerConfig {

    fun startJavalinService(): Javalin {

        val app = Javalin.create { config ->
            config.jsonMapper(
                JavalinJackson(jsonObjectMapper())
            )
            config.routes.exception(Exception::class.java) { e, ctx ->
                e.printStackTrace()
            }
            config.routes.error(404) { ctx ->
                ctx.json("404 - Not Found")
            }
            registerRoutes(config)
        }.start(getRemoteAssignedPort())
        return app
    }

    private fun registerRoutes(config: JavalinConfig) {
        config.routes.get("/api/users", HealthTrackerController::getAllUsers)
        config.routes.get("/api/users/{user-id}", HealthTrackerController::getUserByUserId)
        config.routes.get("/api/users/email/{user-email}", HealthTrackerController::getUserByEmail)
        config.routes.post("/api/users", HealthTrackerController::addUser)
        config.routes.patch("/api/users/{user-id}", HealthTrackerController::updateUser)
        config.routes.delete("/api/users/{user-id}", HealthTrackerController::deleteUser)

        config.routes.get("/api/activities", HealthTrackerController::getAllActivities)
        config.routes.post("/api/activities", HealthTrackerController::addActivity)
        config.routes.get("/api/users/{user-id}/activities", HealthTrackerController::getActivitiesByUserId)

        config.routes.post("/api/users/{user-id}/activities", HealthTrackerController::deleteActivitiesByUserId)
        config.routes.post("/api/activities/{activities-id}", HealthTrackerController::deleteSpecificActivity)
        config.routes.put("/api/activities/{activities-id}", HealthTrackerController::updateActivity)
        config.routes.get("/api/activities/{activities-id}", HealthTrackerController::getActivityById)

    }
    private fun getRemoteAssignedPort(): Int {
        val remotePort = System.getenv("PORT")
        return if (remotePort != null) {
            Integer.parseInt(remotePort)
        } else 8080
    }

}


