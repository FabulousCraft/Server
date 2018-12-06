/**
 *
 * @autor Toropin Konstantin (impy.bian@gmail.com)
 */

import LoginApi.Companion.wrapRespond
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson
import io.ktor.response.respond
import io.ktor.routing.*
import org.jetbrains.exposed.sql.Database

@Suppress("unused")
fun Application.main() {
    initDB()
    routings()
}


fun initDB() {
    Database.connect(
            "jdbc:mysql://localhost:3306/pipper",
            driver = "com.mysql.jdbc.Driver",
            user = "root",
            password = "secretpasport"
    )
    createTables()
}

fun Application.routings() {
    val auth = Auth()
    install(Authentication) {
        basic {
            realm = "Ktor Server"
            validate { credentials ->
                if (auth.check(credentials)) {
                    UserIdPrincipal(credentials.name)
                } else {
                    null
                }
            }
        }
    }
    routing {
        install(ContentNegotiation) {
            gson {}
        }
        authenticate {
            get("/get_pics") {
                wrapRespond {
                    getPages()
                }
            }
        }
        get("/add_user/{login}/{password}") {
            val password = call.parameters["password"]
            val login = call.parameters["login"]
            call.respond(auth.add(login, password))
        }
    }
}
