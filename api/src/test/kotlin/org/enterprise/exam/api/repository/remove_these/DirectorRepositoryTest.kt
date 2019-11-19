package org.enterprise.exam.api.repository.remove_these

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.enterprise.exam.api.entity.remove_these.DirectorEntity
import org.enterprise.exam.api.repository.remove_these.DirectorRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest
class DirectorRepositoryTest(

) {

    @Autowired
    private lateinit var repository: DirectorRepository

    @Test
    fun `can persist and retrieve director`() {

        val persisted = repository.save(DirectorEntity("Eorges", "Méliès", emptyList()))
        val retrieved = repository.findById(persisted.id!!).get()

        assertEquals(persisted.id, retrieved.id);
    }
}