package me.choicore.samples.configuration.jpa

import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration(proxyBeanMethods = false)
class JpaConfiguration {
    @Configuration(proxyBeanMethods = false)
    class QueryDslConfiguration {
        @Bean
        fun jpaQueryFactory(entityManager: EntityManager): JPAQueryFactory = JPAQueryFactory(entityManager)
    }
}
