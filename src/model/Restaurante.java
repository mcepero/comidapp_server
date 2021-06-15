/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;

/**
 *
 * @author Manuel
 */
public class Restaurante implements Serializable{
    private int id;
    private String usuario;
    private String nombre;
    private String contrasena;
    private String email;
    private String direccion;
    private String ciudad;
    private String telefono;
    private String categoria;
    private double latitud;
    private double longitud;

    public Restaurante(int id, String usuario, String nombre, String email, String direccion, String ciudad, String telefono) {
        this.id=id;
        this.usuario = usuario;
        this.nombre = nombre;
        this.email = email;
        this.direccion = direccion;
        this.ciudad=ciudad;
        this.telefono = telefono;
    }

    public Restaurante(int id, String usuario, String nombre, String contrasena, String email, String direccion, String ciudad, String telefono, String categoria) {
        this.id = id;
        this.usuario = usuario;
        this.nombre = nombre;
        this.contrasena = contrasena;
        this.email = email;
        this.direccion = direccion;
        this.ciudad = ciudad;
        this.telefono = telefono;
        this.categoria = categoria;
    }

    public Restaurante(int id, String usuario, String nombre, String contrasena, String email, String direccion, String ciudad, String telefono, String categoria, double latitud, double longitud) {
        this.id = id;
        this.usuario = usuario;
        this.nombre = nombre;
        this.contrasena = contrasena;
        this.email = email;
        this.direccion = direccion;
        this.ciudad = ciudad;
        this.telefono = telefono;
        this.categoria = categoria;
        this.latitud = latitud;
        this.longitud = longitud;
    }
    

    public Restaurante() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }
    
    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }
    
    
       
}
