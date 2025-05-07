package com.pola.api_inventario.rest.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pola.api_inventario.rest.models.ActualizarRolLogisticaDTO;
import com.pola.api_inventario.rest.models.Logistica;
import com.pola.api_inventario.rest.models.LogisticaDTO;
import com.pola.api_inventario.rest.models.Role;
import com.pola.api_inventario.rest.models.User;
import com.pola.api_inventario.rest.repositorio.IuserDao;
import com.pola.api_inventario.rest.repositorio.LogisticaDao;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class LogisticaService {
    @Autowired
    private IuserDao userDao;
    @Autowired
    private LogisticaDao logisticaDao;

    @Transactional
    public LogisticaDTO crearLogistica(LogisticaDTO dto) {
        Optional<Logistica> logOptional = logisticaDao.findByDepartamento(dto.getDepartamento());
        if (logOptional.isEmpty()) {
            Logistica logistica = new Logistica().builder()
                    .departamento(dto.getDepartamento())
                    .descripcion(dto.getDescripcion())
                    .fechaCreacion(new Date())
                    .build();
            logisticaDao.save(logistica);
            return logistiaALogisticaDTO(logistica);
        } else {
            throw new RuntimeException("Este departamento ya existe");
        }

    }

    public LogisticaDTO obtenerLogisticaPorId(Long id) {
        Optional<Logistica> logOptional = logisticaDao.findById(id);
        if (logOptional.isPresent()) {
            return logistiaALogisticaDTO(logOptional.get());
        } else {
            throw new EntityNotFoundException("No se encontro la entidad");
        }
    }

    public List<LogisticaDTO> obtenerTodosLogistica() {
        return logisticaDao.findAll().stream().map(l -> logistiaALogisticaDTO(l)).toList();
    }

    public void eliminarLogisticaPorId(Long id) {
        logisticaDao.deleteById(id);
    }

    private LogisticaDTO logistiaALogisticaDTO(Logistica logistica) {
        return LogisticaDTO.builder()
                .departamento(logistica.getDepartamento())
                .descripcion(logistica.getDescripcion())
                .fechaCreacion(logistica.getFechaCreacion())
                .build();
    }

    @Transactional
    public void actualizarRolUsuarioLogistica(ActualizarRolLogisticaDTO dto) {
        User usuario = userDao.findByUsername(dto.getUsername());
        Optional<Logistica> logisticaOptional = logisticaDao.findByDepartamento(dto.getDepartamentoLogistica());

        if (usuario != null && logisticaOptional.isPresent()) {

            Logistica logistica = logisticaOptional.get();

            usuario.setRole(Role.LOGISTICA);
            usuario.setLogistica(logistica); // Establece la relación

            userDao.save(usuario);
        } else if (usuario == null) {
            // Manejar el caso en que el usuario o el departamento no existen
            throw new EntityNotFoundException("Usuario no encontrado.");
        } else if (logisticaOptional.isEmpty()) {
            throw new EntityNotFoundException("Este departamento no esta registrado.");
        }
    }
}
