package ie.setu.controllers
import ie.setu.domain.repository.UserDAO
import io.javalin.http.Context
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import ie.setu.domain.User

object HealthTrackerController {

    private val userDao = UserDAO()

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


}