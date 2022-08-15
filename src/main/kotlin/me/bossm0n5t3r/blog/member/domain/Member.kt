package me.bossm0n5t3r.blog.member.domain

import com.google.common.hash.Hashing
import me.bossm0n5t3r.blog.common.domain.BaseEntity
import org.apache.commons.codec.binary.Hex
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "member_info")
class Member(
    @Column(name = "email", nullable = false, unique = true, updatable = true)
    var email: String,

    password: String,

    @Column(name = "name", nullable = false, updatable = true)
    var name: String
) : BaseEntity<Long>() {
    @Column(name = "password", nullable = false, updatable = true)
    var password: String = encryptPassword(email, password, name)

    fun setPassword(newPassword: String): Member {
        this.password = encryptPassword(this.email, newPassword, this.name)
        return this
    }

    fun verifyPassword(otherPassword: String): Boolean {
        return this.password == encryptPassword(this.email, otherPassword, this.name)
    }

    private fun encryptPassword(email: String, password: String, name: String): String {
        require(password.isNotEmpty())
        return Hex.encodeHexString(Hashing.sha256().hashString("$email$password$name", Charsets.UTF_8).asBytes())
    }
}
