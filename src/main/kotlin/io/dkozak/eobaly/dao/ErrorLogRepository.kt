package io.dkozak.eobaly.dao

import io.dkozak.eobaly.domain.ErrorLog
import org.springframework.data.jpa.repository.JpaRepository

interface ErrorLogRepository : JpaRepository<ErrorLog, Long>