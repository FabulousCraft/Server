import io.ktor.auth.UserPasswordCredential
import io.ktor.http.HttpStatusCode

class Auth {
    private val dbProvider = DBProvider()

    fun check(credentials: UserPasswordCredential) = dbProvider.authUser(credentials.name, credentials.password)

    fun add(login: String?, password: String?): HttpStatusCode =
        if (login == null || password == null || dbProvider.createNewUser(login, password))
            HttpStatusCode.OK
        else HttpStatusCode.Conflict
}