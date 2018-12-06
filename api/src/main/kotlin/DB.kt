import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction

object Post: LongIdTable() {
    val groupDomain = varchar("groupDomain", 128)
    val postId = varchar("postId", 256)
    val urlPic = varchar("urlPic", 256)
    var text = text("text", "utf8mb4_unicode_ci")
    val group = reference("group", Group, ReferenceOption.CASCADE)
    var index = text("index", "utf8mb4_unicode_ci")
}

object Group: LongIdTable() {
    val shift = integer("shift")
    val read = integer("read")
    val domain = varchar("domain", 100).index(isUnique = true)
}

object User: LongIdTable() {
    val login = varchar("login", 100).index(isUnique = true)
    val hashPassword = varchar("hashPassword", 100)
    // List<Period>
    val periods = text("periods")
}

class PostEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<PostEntity>(Post)

    var groupDomain by Post.groupDomain
    var postId by Post.postId
    var urlPic by Post.urlPic
    var text by Post.text
    var group by EGroup referencedOn Post.group
    var index by Post.index
}

class EGroup(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<EGroup>(Group)

    var lastRead by Group.read
    var shift by Group.shift
    var domain by Group.domain
}

class EUser(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<EUser>(User)
    var login by User.login
    var hashPassword: Hash by User.hashPassword
    var periods by User.periods
}


class EPost(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<PostEntity>(Post)

    var groupDomain by Post.groupDomain
    var postId by Post.postId
    var urlPic by Post.urlPic
    var text by Post.text
    var group by EGroup referencedOn Post.group
    var index by Post.index
}


val registeredDB = arrayOf(Post, User, Group)

fun createTables() = transaction {
    addLogger(StdOutSqlLogger)
    SchemaUtils.create(*registeredDB)
}

fun cleanUp() = transaction {
    registeredDB.forEach {
        it.dropStatement()
    }
}
