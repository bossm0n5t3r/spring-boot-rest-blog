package me.bossm0n5t3r.blog.common.exception

class ResourceNotFoundException(message: String? = null, cause: Throwable? = null) : Exception(message, cause) {
    constructor(cause: Throwable) : this(null, cause)
}
