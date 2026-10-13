package ie.setu.domain.repository

import ie.setu.utils.mapToUser
import ie.setu.domain.User
import ie.setu.domain.db.Users
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class UserDAO {

    private val users = arrayListOf<User>(
        User(name = "Alice", email = "alice@wonderland.com", id = 0),
        User(name = "Bob", email = "bob@cat.ie", id = 1),
        User(name = "Mary", email = "mary@contrary.com", id = 2),
        User(name = "Carol", email = "carol@singer.com", id = 3)
    )

    fun getAll(): ArrayList<User> {
        val userList: ArrayList<User> = arrayListOf()
        transaction {
            Users.selectAll().map {
                userList.add(mapToUser(it)) }
        }
        return userList
    }

    fun findById(id: Int): User?{
        return transaction {
            Users.selectAll().where { Users.id eq id }
                .map{mapToUser(it)}
                .firstOrNull()
        }
    }
    fun save(user: User){
        return transaction {
            Users.update {
                it[name] = user.name
                it[email] = user.email
            }
        }
    }
    fun findByEmail(email: String) :User?{
        return transaction {
            Users.selectAll().where { Users.email eq email }
                .map{mapToUser(it)}
                .firstOrNull()
        }
    }


}