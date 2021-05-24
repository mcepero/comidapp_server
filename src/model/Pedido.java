/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author Manuel
 */
public class Pedido {
    private int id;
    private double precio;
    private String fecha;
    private String estado;
    private ArrayList<Producto> productos;
    private int idCliente;
    private int idRepartidor;
    private String nombreRestaurante;

    public Pedido(int id, double precio, String fecha, String estado, ArrayList<Producto> productos, int idCliente, int idRepartidor) {
        this.id = id;
        this.precio = precio;
        this.fecha = fecha;
        this.estado = estado;
        this.productos = productos;
        this.idCliente = idCliente;
        this.idRepartidor = idRepartidor;
    }

    public Pedido(int id, double precio, String fecha, String estado, String nombreRestaurante) {
        this.id = id;
        this.precio = precio;
        this.fecha = fecha;
        this.estado = estado;
        this.nombreRestaurante = nombreRestaurante;
    }
    
    

    public Pedido() {
        productos = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public int getIdRepartidor() {
        return idRepartidor;
    }

    public void setIdRepartidor(int idRepartidor) {
        this.idRepartidor = idRepartidor;
    }

    public ArrayList<Producto> getProductos() {
        return productos;
    }

    public void setProductos(ArrayList<Producto> productos) {
        this.productos = productos;
    }

    public String getNombreRestaurante() {
        return nombreRestaurante;
    }

    public void setNombreRestaurante(String nombreRestaurante) {
        this.nombreRestaurante = nombreRestaurante;
    }  
    
}
