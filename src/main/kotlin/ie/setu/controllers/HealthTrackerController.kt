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
    /* fun addUser(ctx: Context){

        val email = ctx.pathParam( "email")
        val name = ctx.pathParam( "name")
        //val name = ctx.formParam("name")
        //val email = ctx.formParam("email")

        if (name == null || email == null) {
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
    }*/

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


}