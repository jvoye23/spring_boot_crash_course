package com.jvcs.spring_boot_crash_course.database.model

import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

@Document("refresh_tokens")
data class RefreshToken(
    val userId: ObjectId,
    @Indexed(expireAfter = "0s")
    val expiresAt: java.time.Instant,
    val hashedToken: String,
    val createdAt: java.time.Instant = java.time.Instant.now()
)
