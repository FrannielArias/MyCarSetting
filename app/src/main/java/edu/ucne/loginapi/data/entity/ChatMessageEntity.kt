package edu.ucne.loginapi.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val conversationId: String,
    val role: String,
    val content: String,
    val timestamp: Long
)
