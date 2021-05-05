package com.example.cache

import org.jetbrains.exposed.sql.transactions.TransactionManager
import java.sql.ResultSet

// useful for queries that expect an answer
fun String.execAndReturn(closure: (ResultSet) -> Unit) {
    TransactionManager.current().exec(this) { rs ->
        closure(rs)
    }
}

// just executes sql query as a string, useful for queries that do not expect an answer
fun String.exec() {
    TransactionManager.current().exec(this)
}

// might protect a little more against sql injection attacks?
//fun Transaction.exec(sql: String, body: PreparedStatement.() -> Unit) : ResultSet? {
//    return connection.prepareStatement(sql).apply(body).run {
//        if (sql.toLowerCase().startsWith("select "))
//            sql.execAndMap { rs ->
//                // TODO
//                rs.getString(...)
//            }
//        else {
//            null
//        }
//    }
//}

// taken from https://github.com/JetBrains/Exposed/issues/118, might be useful later
//fun <T:Any> String.execAndMap(transform : (ResultSet) -> T) : List<T> {
//    val result = arrayListOf<T>()
//    TransactionManager.current().exec(this) { rs ->
//        while (rs.next()) {
//            result += transform(rs)
//        }
//    }
//    return result
//}