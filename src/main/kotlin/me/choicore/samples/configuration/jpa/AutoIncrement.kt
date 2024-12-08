package me.choicore.samples.configuration.jpa

import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import org.springframework.data.domain.Persistable

@MappedSuperclass
abstract class AutoIncrement : Persistable<Long> {
    @Id
    val id: Long = 0L

    override fun getId(): Long = this.id

    override fun isNew(): Boolean = this.id == 0L
}
