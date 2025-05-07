package com.pola.api_inventario.rest.repositorio;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pola.api_inventario.rest.models.Logistica;

@Repository
public interface LogisticaDao extends JpaRepository<Logistica, Long> {
    Optional<Logistica> findByDepartamento(String departamento);
}
