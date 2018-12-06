import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.authentication
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.util.pipeline.PipelineContext
import org.jetbrains.exposed.sql.transactions.transaction

class BadRequestException : Exception()

data class Response(val count: Int, val page: List<String>)

@Suppress("unused")
class LoginApi(val login: String) {
    val mapper = jacksonObjectMapper()

    companion object {

        suspend fun PipelineContext<*, ApplicationCall>.wrapRespond(build: LoginApi.() -> Any) {
            try {
                val login = call.authentication.principal<UserIdPrincipal>()?.name ?: throw BadRequestException()
                val api = LoginApi(login)
                call.respond(build(api))
            } catch (e: BadRequestException) {
                call.respond(HttpStatusCode.BadRequest)
            }
        }
    }
    fun getPages() : Response = transaction {
        Response(0,  listOf("1", "2"))
    }
}