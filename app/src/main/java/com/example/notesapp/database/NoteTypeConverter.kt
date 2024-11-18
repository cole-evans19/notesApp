package com.example.notesapp.database

import androidx.room.TypeConverter
import java.util.UUID

class NoteTypeConverter {

    @TypeConverter
    fun fromUUID(id: UUID) : String {
        return id.toString()
    }

    @TypeConverter
    fun toUUID(idString: String) : UUID {
        return UUID.fromString(idString)
    }
 }