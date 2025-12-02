package edu.ucne.loginapi.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import edu.ucne.loginapi.data.entity.ChatMessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatMessageDao {

    @Query(
        "SELECT * FROM chat_messages " +
                "WHERE conversationId = :conversationId " +
                "ORDER BY timestamp ASC"
    )
    fun observeMessages(conversationId: String): Flow<List<ChatMessageEntity>>

    @Insert
    suspend fun insertMessage(message: ChatMessageEntity)

    @Query("DELETE FROM chat_messages WHERE conversationId = :conversationId")
    suspend fun clearConversation(conversationId: String)
}
