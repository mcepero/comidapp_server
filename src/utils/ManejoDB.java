/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Cliente;
import model.Pedido;
import model.Producto;
import model.Repartidor;
import model.Restaurante;

/**
 *
 * @author Manuel
 */
public class ManejoDB {

    private String url = "jdbc:mariadb://localhost/comidapp";
    private String user = "root";
    private String pass = "";
    public java.sql.Connection conexion = null;
    public java.sql.Statement sentencia = null;
    public java.sql.ResultSet resultset = null;

    public ManejoDB() {
        try {
            conexion();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ManejoDB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void conexion() throws ClassNotFoundException {
        try {
            this.conexion = DriverManager.getConnection(url, user, pass);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al conectar con la base de datos");
        }
    }

    public boolean iniciarSesionUsuario(String usuario, String contrasena) {
        try {
            this.sentencia = conexion.createStatement();
            PreparedStatement ps = conexion.prepareStatement("SELECT usuario, contrasena FROM cliente WHERE usuario=? AND contrasena=?;");

            ps.setString(1, usuario);
            ps.setString(2, contrasena);

            resultset = ps.executeQuery();

            while (resultset.next()) {
                return true;
            }

        } catch (SQLException e) {
            e.getMessage();
        }

        return false;
    }

    public boolean registroUsuario(String usuario, String email, String contrasena, String nombre, String direccion) {
        try {
            PreparedStatement ps = conexion.prepareStatement("INSERT INTO cliente (nombre, usuario, contrasena, email, direccion) VALUES(?,?,?,?,?)");

            if (!comprobarUsuarioRepetido(usuario, email)) {
                ps.setString(1, usuario);
                ps.setString(2, email);
                ps.setString(3, contrasena);
                ps.setString(4, nombre);
                ps.setString(5, direccion);

                resultset = ps.executeQuery();

                return true;
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    //Añade un repartidor
    public boolean registroRepartidor(String usuario, String email, String contrasena, String nombre, String dni) {
        try {
            PreparedStatement ps = conexion.prepareStatement("INSERT INTO repartidor (usuario, nombre, contrasena, email, dni) VALUES(?,?,?,?,?)");

            if (!comprobarRepartidorRepetido(usuario, email)) {
                ps.setString(1, usuario);
                ps.setString(2, nombre);
                ps.setString(3, contrasena);
                ps.setString(4, email);
                ps.setString(5, dni);

                resultset = ps.executeQuery();

                return true;
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    public boolean comprobarUsuarioRepetido(String usuario, String email) {
        try {
            this.sentencia = conexion.createStatement();
            PreparedStatement ps = conexion.prepareStatement("SELECT * FROM cliente WHERE usuario=? OR email=?;");

            ps.setString(1, usuario);
            ps.setString(2, email);

            resultset = ps.executeQuery();

            while (resultset.next()) {
                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(ManejoDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    //Comprueba que un repartidor no repita un usuario o email
    public boolean comprobarRepartidorRepetido(String usuario, String email) {
        try {
            this.sentencia = conexion.createStatement();
            PreparedStatement ps = conexion.prepareStatement("SELECT * FROM repartidor WHERE usuario=? OR email=?;");

            ps.setString(1, usuario);
            ps.setString(2, email);

            resultset = ps.executeQuery();

            while (resultset.next()) {
                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(ManejoDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean iniciarSesionRestaurante(String usuario, String contrasena) {
        try {
            this.sentencia = conexion.createStatement();
            PreparedStatement ps = conexion.prepareStatement("SELECT usuario, contrasena FROM restaurante WHERE usuario=? AND contrasena=?;");

            ps.setString(1, usuario);
            ps.setString(2, contrasena);

            resultset = ps.executeQuery();

            while (resultset.next()) {
                return true;
            }

        } catch (SQLException e) {
            e.getMessage();
        }

        return false;
    }

    public boolean registroRestaurante(String usuario, String email, String contrasena, String nombre, String direccion, String telefono, String ciudad) {
        System.out.println(telefono);
        try {
            PreparedStatement ps = conexion.prepareStatement("INSERT INTO restaurante (usuario, email, contrasena, nombre, direccion, telefono, ciudad) VALUES(?,?,?,?,?,?,?)");

            if (!comprobarUsuarioRepetido(usuario, email)) {
                ps.setString(1, usuario);
                ps.setString(2, email);
                ps.setString(3, contrasena);
                ps.setString(4, nombre);
                ps.setString(5, direccion);
                ps.setString(6, telefono);
                ps.setString(7, ciudad);

                resultset = ps.executeQuery();

                return true;
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    public boolean comprobarRestauranteRepetido(String usuario, String email) {
        try {
            this.sentencia = conexion.createStatement();
            PreparedStatement ps = conexion.prepareStatement("SELECT * FROM restaurante WHERE usuario=? OR email=?;");

            ps.setString(1, usuario);
            ps.setString(2, email);

            resultset = ps.executeQuery();

            while (resultset.next()) {
                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(ManejoDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public Restaurante obtenerRestaurante(String usuario) throws SQLException {
        this.sentencia = conexion.createStatement();
        PreparedStatement ps = conexion.prepareStatement("SELECT * FROM restaurante WHERE usuario=?;");

        ps.setString(1, usuario);

        resultset = ps.executeQuery();

        Restaurante r = new Restaurante();
        while (resultset.next()) {
            r.setId(resultset.getInt("id"));
            r.setUsuario(resultset.getString("usuario"));
            r.setNombre(resultset.getString("nombre"));
            r.setEmail(resultset.getString("email"));
            r.setDireccion(resultset.getString("direccion"));
            r.setTelefono(resultset.getString("telefono"));
        }
        return r;
    }
    
    public Cliente obtenerClienteNombre(String usuario) throws SQLException {
        PreparedStatement ps = conexion.prepareStatement("SELECT * FROM cliente WHERE usuario=?;");

        ps.setString(1, usuario);

        resultset = ps.executeQuery();

        Cliente c = new Cliente();
        while (resultset.next()) {
            c.setId(resultset.getInt("id"));
            c.setUsuario(resultset.getString("usuario"));
            c.setNombre(resultset.getString("nombre"));
            c.setEmail(resultset.getString("email"));
            c.setDireccion(resultset.getString("direccion"));
        }
        return c;
    }

    //Editar usuario restaurante (sin contraseña)
    public void modificarRestaurante(String usuarioActual, String nuevoUsuario, String email, String nombre, String direccion, String telefono) {
        try {
            PreparedStatement ps = conexion.prepareStatement("UPDATE restaurante SET usuario=?, nombre=?, email=?, direccion=?, telefono=? WHERE usuario=?");

            ps.setString(1, nuevoUsuario);
            ps.setString(2, nombre);
            ps.setString(3, email);
            ps.setString(4, direccion);
            ps.setString(5, telefono);
            ps.setString(6, usuarioActual);

            int resultado = ps.executeUpdate();
        } catch (SQLException e) {
            e.getMessage();
        }
    }

    //Editar usuario restaurante (con contraseña)
    public void modificarRestauranteContrasena(String usuarioActual, String nuevoUsuario, String email, String nombre, String direccion, String telefono, String contrasena) {
        try {
            PreparedStatement ps = conexion.prepareStatement("UPDATE restaurante SET usuario=?, nombre=?, email=?, direccion=?, telefono=?, contrasena=? WHERE usuario=?");

            ps.setString(1, nuevoUsuario);
            ps.setString(2, nombre);
            ps.setString(3, email);
            ps.setString(4, direccion);
            ps.setString(5, telefono);
            ps.setString(6, contrasena);
            ps.setString(7, usuarioActual);

            int resultado = ps.executeUpdate();
        } catch (SQLException e) {
            e.getMessage();
        }
    }

    //Obtiene todos los productos de un restautante y devuelve una lista
    public ArrayList<Producto> obtenerCarta(int idRestaurante) {
        ArrayList<Producto> listaProductos = new ArrayList<>();

        try {
            this.sentencia = conexion.createStatement();
            PreparedStatement ps = conexion.prepareStatement("SELECT * FROM producto WHERE idRestaurante=?;");

            ps.setInt(1, idRestaurante);

            resultset = ps.executeQuery();

            while (resultset.next()) {
                Producto p = new Producto();
                p.setId(resultset.getInt("id"));
                p.setNombre(resultset.getString("nombre"));
                p.setIngredientes(resultset.getString("ingredientes"));
                p.setPrecio(resultset.getDouble("precio"));
                p.setIdRestaurante(idRestaurante);
                listaProductos.add(p);
            }

        } catch (SQLException ex) {
            Logger.getLogger(ManejoDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return listaProductos;
    }

    //Añade un producto a la carta de un restaurante
    public boolean anadirProducto(int idRestaurante, String nombre, String ingredientes, double precio) {
        try {
            PreparedStatement ps = conexion.prepareStatement("INSERT INTO producto (nombre, ingredientes, precio, idRestaurante) VALUES(?,?,?,?)");

            ps.setString(1, nombre);
            ps.setString(2, ingredientes);
            ps.setDouble(3, precio);
            ps.setInt(4, idRestaurante);

            int resp = ps.executeUpdate();
            if (resp > 0) {
                return true;
            }
            //resultset = ps.executeQuery();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    //Modifica un producto
    public void modificarProducto(int id, String nombre, String ingredientes, double precio) {
        try {
            PreparedStatement ps = conexion.prepareStatement("UPDATE producto SET nombre=?, ingredientes=?, precio=? WHERE id=?");

            ps.setString(1, nombre);
            ps.setString(2, ingredientes);
            ps.setDouble(3, precio);
            ps.setInt(4, id);

            int resultado = ps.executeUpdate();
        } catch (SQLException e) {
            e.getMessage();
        }
    }

    public void eliminarProducto(int id) {
        try {
            PreparedStatement ps = conexion.prepareStatement("DELETE FROM producto WHERE id=?");

            ps.setInt(1, id);

            int resultado = ps.executeUpdate();
        } catch (SQLException e) {
            e.getMessage();
        }
    }

    public void eliminarRepartidor(int id) {
        try {
            PreparedStatement ps = conexion.prepareStatement("UPDATE repartidor SET idRestaurante=null WHERE id=?");

            ps.setInt(1, id);

            int resultado = ps.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(ManejoDB.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    //Obtiene todos los pedidos de un restautante y devuelve una lista
    public ArrayList<Pedido> obtenerPedidos(int idRestaurante) throws SQLException {
        ArrayList<Pedido> listaPedidos = new ArrayList<>();

        PreparedStatement ps = conexion.prepareStatement("SELECT id, fecha, estado, precio, idCliente, idRepartidor, idRestaurante FROM pedido WHERE idRestaurante=? ORDER BY fecha DESC;");

        ps.setInt(1, idRestaurante);
        resultset = ps.executeQuery();
        while (resultset.next()) {
            Pedido p = new Pedido();
            int id = resultset.getInt("id");
            p.setId(id);
            p.setFecha(resultset.getString("fecha"));
            p.setEstado(resultset.getString("estado"));
            p.setPrecio(resultset.getDouble("precio"));
            p.setIdCliente(resultset.getInt("idCliente"));
            p.setIdRepartidor(resultset.getInt("idRepartidor"));
            listaPedidos.add(p);
        }
        return listaPedidos;
    }

    public ArrayList<Producto> obtenerProductos(int idPedido) {
        ArrayList<Producto> productos = new ArrayList<>();

        try {
            this.sentencia = conexion.createStatement();
            PreparedStatement ps = conexion.prepareStatement("SELECT p.nombre, p.precio, p.ingredientes FROM productospedido pd, producto p WHERE pd.idProducto=p.id AND pd.idPedido=?;");

            ps.setInt(1, idPedido);

            resultset = ps.executeQuery();

            while (resultset.next()) {
                Producto p = new Producto();
                p.setNombre(resultset.getString("nombre"));
                p.setPrecio(resultset.getDouble("precio"));
                p.setIngredientes(resultset.getString("ingredientes"));
                productos.add(p);
            }

        } catch (SQLException ex) {
            Logger.getLogger(ManejoDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return productos;
    }

    //Obtiene un cliente dado su ID
    public Cliente obtenerCliente(int idCliente) throws SQLException {
        this.sentencia = conexion.createStatement();
        PreparedStatement ps = conexion.prepareStatement("SELECT * FROM cliente WHERE id=?;");

        ps.setInt(1, idCliente);

        resultset = ps.executeQuery();

        Cliente c = new Cliente();
        while (resultset.next()) {
            c.setId(resultset.getInt("id"));
            c.setUsuario(resultset.getString("usuario"));
            c.setNombre(resultset.getString("nombre"));
            c.setEmail(resultset.getString("email"));
            c.setDireccion(resultset.getString("direccion"));
        }
        return c;
    }

    public Pedido obtenerPedido(int idPedido) throws SQLException {
        this.sentencia = conexion.createStatement();
        PreparedStatement ps = conexion.prepareStatement("SELECT * FROM pedido WHERE id=?;");

        ps.setInt(1, idPedido);

        resultset = ps.executeQuery();

        Pedido p = new Pedido();
        while (resultset.next()) {
            p.setId(resultset.getInt("id"));
            p.setPrecio(resultset.getDouble("precio"));
            p.setFecha(resultset.getString("fecha"));
            p.setEstado(resultset.getString("estado"));
            p.setIdCliente(resultset.getInt("idCliente"));
            p.setIdRepartidor(resultset.getInt("idRepartidor"));
            p.setProductos(obtenerProductos(idPedido));
        }
        return p;
    }

    public boolean anadirRepartidorRestaurante(int idRestaurante, String dni) {
        try {
            PreparedStatement ps = conexion.prepareStatement("UPDATE repartidor SET idRestaurante=? WHERE dni=?");

            ps.setInt(1, idRestaurante);
            ps.setString(2, dni);

            int resultado = ps.executeUpdate();

            if (resultado == 1) {
                return true;
            }
        } catch (SQLException e) {
            e.getMessage();
        }
        return false;
    }

    public ArrayList<Repartidor> obtenerRepartidores(int idRestaurante) {
        ArrayList<Repartidor> listaRepartidores = new ArrayList<>();

        try {
            PreparedStatement ps = conexion.prepareStatement("SELECT * FROM repartidor WHERE idRestaurante=?;");

            ps.setInt(1, idRestaurante);

            resultset = ps.executeQuery();

            while (resultset.next()) {
                Repartidor r = new Repartidor();
                r.setId(resultset.getInt("id"));
                r.setUsuario(resultset.getString("usuario"));
                r.setNombre(resultset.getString("nombre"));
                r.setEmail(resultset.getString("email"));
                r.setDni(resultset.getString("dni"));
                listaRepartidores.add(r);
            }

        } catch (SQLException ex) {
            Logger.getLogger(ManejoDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return listaRepartidores;
    }
}
