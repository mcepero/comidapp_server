/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 *
 * @author Manuel
 */
public class Repartidor {
    private int id;
    private String nombre;
    private String usuario;
    private String email;
    private String dni;
    private String direccion;

    public Repartidor(int id, String nombre, String usuario, String email, String dni, String direccion) {
        this.id = id;
        this.nombre = nombre;
        this.usuario = usuario;
        this.email = email;
        this.dni = dni;
        this.direccion = direccion;
    }

    public Repartidor() {
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

   
    
    
}
