package com.jvcs.spring_boot_crash_course.controllers

import com.jvcs.spring_boot_crash_course.controllers.NoteController.NoteResponse
import com.jvcs.spring_boot_crash_course.database.model.Note
import com.jvcs.spring_boot_crash_course.database.repository.NoteRepository
import org.bson.types.ObjectId
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

// POST http://localhost:8080/notes
// GET http://localhost:8080/notes?ownerId=123
// DELETE http://localhost:8080/notes/123

@RestController
@RequestMapping("/notes")
class NoteController(
    private val noteRepository: NoteRepository
) {

    data class NoteRequest(
        val id: String?,
        val title: String,
        val content: String,
        val color: Long
    )

    data class NoteResponse @OptIn(ExperimentalTime::class) constructor(
        val id: String?,
        val title: String,
        val content: String,
        val color: Long,
        val createdAt: Instant
    )

    @OptIn(ExperimentalTime::class)
    @PostMapping
    fun save(
        @RequestBody body: NoteRequest
    ): NoteResponse {
        val note =  noteRepository.save(
            Note(
                id = body.id?.let { ObjectId(it)} ?: ObjectId.get(), // if id is null post a new note, if it is not null create a new note
                title = body.title,
                content = body.content,
                color = body.color,
                createdAt = Clock.System.now(),
                ownerId = ObjectId()
            )
        )

        return note.toNoteReponse()
    }

    @GetMapping
    fun findByOwnerId(
        @RequestParam(required = true) ownerId: String
    ): List<NoteResponse> {
        return noteRepository.findByOwnerId(ObjectId(ownerId)).map {
            it.toNoteReponse()
        }
    }

    @DeleteMapping(path = ["/{id}"])
    fun deleteById(@PathVariable id: String) {
        noteRepository.deleteById(ObjectId(id))
    }

}

@OptIn(ExperimentalTime::class)
private fun Note.toNoteReponse(): NoteController.NoteResponse {
    return NoteResponse(
        id = id.toHexString(),
        title = title,
        content = content,
        color = color,
        createdAt = createdAt
    )
}