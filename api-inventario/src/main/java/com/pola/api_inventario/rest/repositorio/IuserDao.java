package com.pola.api_inventario.rest.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pola.api_inventario.rest.models.User;

@Repository
public interface IuserDao extends JpaRepository<User, Long> {

    User findByUsername(String username);

}
