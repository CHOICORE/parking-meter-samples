package me.choicore.samples.parking

import org.springframework.data.jpa.repository.JpaRepository

interface AccessEntityRepository : JpaRepository<AccessEntity, Long>
