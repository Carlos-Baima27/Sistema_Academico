package com.cursojava.sistemaacademico.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cursojava.sistemaacademico.model.MatriculaSequence;

import jakarta.persistence.LockModeType;

public interface MatriculaSequenceRepository
        extends JpaRepository<MatriculaSequence, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select sequence from MatriculaSequence sequence where sequence.id = :id")
    Optional<MatriculaSequence> findLockedById(@Param("id") Long id);
}
