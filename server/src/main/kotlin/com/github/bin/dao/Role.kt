package com.github.bin.dao

import com.github.bin.config.json
import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.varchar

interface Role : Entity<Role> {
    var id: String
    var name: String
    var tags: List<Tag>

    companion object : Entity.Factory<Role>()
}

object TRole : Table<Role>(tableName = "Role", entityClass = Role::class) {
    val id = varchar("id").bindTo { it.id }.primaryKey()
    val name = varchar("name").bindTo { it.name }
    val tags = json<List<Tag>>("tags").bindTo { it.tags }
}
