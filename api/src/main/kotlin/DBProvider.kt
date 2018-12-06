import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.apache.commons.codec.digest.DigestUtils
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction

typealias Hash = String

class DBProvider {
    private val mapper = jacksonObjectMapper()

    private fun String.hash(): Hash = DigestUtils.md5Hex(this)

    fun createNewUser(login: String, password: String): Boolean = transaction {
        if (EUser.find { User.login eq login }.empty()) {
            EUser.new {
                this.login = login
                this.hashPassword = password.hash()
                this.periods = mapper.writeValueAsString(listOf<Period>())
            }
            true
        } else false
    }

    fun authUser(login: String, password: String): Boolean = transaction {
        val hashPassword = password.hash()
        !EUser.find { (User.login eq login) and (User.hashPassword eq hashPassword) }.empty()
    }

}
