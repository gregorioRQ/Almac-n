package com.pola.api_inventario.rest.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pola.api_inventario.rest.models.Mensaje;

@Repository
public interface IMensajeDao extends JpaRepository<Mensaje, Long> {
    List<Mensaje> findAllByOrderByTimestampAsc();
}
