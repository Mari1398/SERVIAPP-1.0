/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.egg.ServiApp.repositorio;

import com.egg.ServiApp.entidades.Calificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author marco
 */
@Repository
public interface calificacionRepositorio extends JpaRepository<Calificacion, String>{
 

        
    @Query("SELECT a FROM Calificacion a WHERE a.id = :id")
    public Calificacion buscarPorId(@Param ("id")String id);
    
    @Query("SELECT a FROM Calificacion a WHERE a.puntuacion = :puntuacion")
    public Calificacion buscarPorPuntuacion(@Param ("puntuacion")String puntuacion);

}