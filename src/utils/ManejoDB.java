/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Cliente;
import model.Pedido;
import model.Producto;
import model.Repartidor;
import model.Restaurante;
import model.Valoracion;

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
            System.out.println("Error al conectar con la base de datos");
        } catch (Exception e) {
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

    public boolean registroUsuario(String usuario, String email, String contrasena, String nombre, String direccion, String ciudad) {
        try {
            PreparedStatement ps = conexion.prepareStatement("INSERT INTO cliente (usuario, email, contrasena, nombre, direccion, ciudad) VALUES(?,?,?,?,?,?)");

            if (!comprobarUsuarioRepetido(usuario, email)) {
                ps.setString(1, usuario);
                ps.setString(2, email);
                ps.setString(3, contrasena);
                ps.setString(4, nombre);
                ps.setString(5, direccion);
                ps.setString(6, ciudad);

                resultset = ps.executeQuery();

                return true;
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    //A??ade un repartidor
    public boolean registroRepartidor(String usuario, String contrasena, String email, String nombre, String dni) {
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

    public boolean registroRestaurante(String usuario, String email, String contrasena, String nombre, String direccion, String telefono, String ciudad, int idCategoria) {
        System.out.println(telefono);
        try {
            PreparedStatement ps = conexion.prepareStatement("INSERT INTO restaurante (usuario, email, contrasena, nombre, direccion, telefono, ciudad, idCategoria) VALUES(?,?,?,?,?,?,?,?)");

            if (!comprobarRestauranteRepetido(usuario, email)) {
                ps.setString(1, usuario);
                ps.setString(2, email);
                ps.setString(3, contrasena);
                ps.setString(4, nombre);
                ps.setString(5, direccion);
                ps.setString(6, telefono);
                ps.setString(7, ciudad);
                ps.setInt(8, idCategoria);

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
    
    
    //Mismo m??todo que el de comprobarRestauranteRepetido, solo que recibe el usuario y email actual
    public boolean comprobarRestauranteRepetidoActualizar(String usuario, String email, String usuarioActual, String emailActual) {
        try {
            this.sentencia = conexion.createStatement();
            PreparedStatement ps = conexion.prepareStatement("SELECT * FROM restaurante WHERE usuario=? OR email=?;");

            ps.setString(1, usuarioActual);
            ps.setString(2, emailActual);
            ps.setString(3, usuario);
            ps.setString(4, email);

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

    public Restaurante obtenerRestauranteMapa(String nombre, String direccion, String ciudad) throws SQLException {
        this.sentencia = conexion.createStatement();
        PreparedStatement ps = conexion.prepareStatement("SELECT r.id, r.nombre, r.email, r.direccion, r.ciudad, r.telefono, c.nombre AS nombreCategoria FROM restaurante r, categoria c WHERE r.nombre=? AND r.direccion=? AND r.ciudad=?;");

        ps.setString(1, nombre);
        ps.setString(2, direccion);
        ps.setString(3, ciudad);

        resultset = ps.executeQuery();

        Restaurante r = new Restaurante();

        while (resultset.next()) {
            r.setId(resultset.getInt("id"));
            r.setNombre(resultset.getString("nombre"));
            r.setEmail(resultset.getString("email"));
            r.setDireccion(resultset.getString("direccion"));
            r.setCiudad(resultset.getString("ciudad"));
            r.setTelefono(resultset.getString("telefono"));
            r.setCategoria(resultset.getString("nombreCategoria"));
            return r;

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
            c.setCiudad(resultset.getString("ciudad"));
        }
        return c;
    }

    //Editar usuario restaurante (sin contrase??a)
    public boolean modificarRestaurante(String usuarioActual, String nuevoUsuario, String email, String nombre, String direccion, String telefono, String emailActual) {
        try {
            PreparedStatement ps = conexion.prepareStatement("UPDATE restaurante SET usuario=?, nombre=?, email=?, direccion=?, telefono=? WHERE usuario=?");

            if (!comprobarRestauranteRepetidoActualizar(nuevoUsuario, email, usuarioActual, emailActual)) {
                ps.setString(1, nuevoUsuario);
                ps.setString(2, nombre);
                ps.setString(3, email);
                ps.setString(4, direccion);
                ps.setString(5, telefono);
                ps.setString(6, usuarioActual);

                int resultado = ps.executeUpdate();

                return true;
            }
        } catch (SQLException e) {
            e.getMessage();
        }

        return false;
    }

    //Editar usuario restaurante (con contrase??a)
    public boolean modificarRestauranteContrasena(String usuarioActual, String nuevoUsuario, String email, String nombre, String direccion, String telefono, String contrasena, String emailActual) {
        try {
            PreparedStatement ps = conexion.prepareStatement("UPDATE restaurante SET usuario=?, nombre=?, email=?, direccion=?, telefono=?, contrasena=? WHERE usuario=?");

            if (!comprobarRestauranteRepetidoActualizar(nuevoUsuario, email, usuarioActual, emailActual)) {
                ps.setString(1, nuevoUsuario);
                ps.setString(2, nombre);
                ps.setString(3, email);
                ps.setString(4, direccion);
                ps.setString(5, telefono);
                ps.setString(6, contrasena);
                ps.setString(7, usuarioActual);

                int resultado = ps.executeUpdate();
                
                return true;
            }
        } catch (SQLException e) {
            e.getMessage();
        }
        
        return false;
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
                p.setImagen(resultset.getString("imagen"));
                p.setIdRestaurante(idRestaurante);
                listaProductos.add(p);
            }

        } catch (SQLException ex) {
            Logger.getLogger(ManejoDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return listaProductos;
    }

    //A??ade un producto a la carta de un restaurante
    public boolean anadirProducto(int idRestaurante, String nombre, String ingredientes, double precio, String nombreImagen) {
        try {
            PreparedStatement ps = conexion.prepareStatement("INSERT INTO producto (nombre, ingredientes, precio, idRestaurante, imagen) VALUES(?,?,?,?,?)");

            ps.setString(1, nombre);
            ps.setString(2, ingredientes);
            ps.setDouble(3, precio);
            ps.setInt(4, idRestaurante);
            ps.setString(5, nombreImagen);

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

    //Modifica un producto (la imagen ha cambiado)
    public void modificarProductoConImagen(int id, String nombre, String ingredientes, double precio, String imagen) {
        try {
            PreparedStatement ps = conexion.prepareStatement("UPDATE producto SET nombre=?, ingredientes=?, precio=?, imagen=? WHERE id=?");

            ps.setString(1, nombre);
            ps.setString(2, ingredientes);
            ps.setDouble(3, precio);
            ps.setString(4, imagen);
            ps.setInt(5, id);

            int resultado = ps.executeUpdate();
        } catch (SQLException e) {
            e.getMessage();
        }
    }

    //Modifica un producto (la imagen NO ha cambiado)
    public void modificarProductoSinImagen(int id, String nombre, String ingredientes, double precio) {
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

    public Producto obtenerProducto(int idProducto) {
        Producto p = new Producto();
        try {
            this.sentencia = conexion.createStatement();
            PreparedStatement ps = conexion.prepareStatement("SELECT * FROM producto WHERE id=?;");

            ps.setInt(1, idProducto);

            resultset = ps.executeQuery();

            while (resultset.next()) {
                p.setId(resultset.getInt("id"));
                p.setNombre(resultset.getString("nombre"));
                p.setIngredientes(resultset.getString("ingredientes"));
                p.setPrecio(resultset.getDouble("precio"));
                p.setImagen(resultset.getString("imagen"));
                p.setIdRestaurante(resultset.getInt("idRestaurante"));
            }

        } catch (SQLException ex) {
            Logger.getLogger(ManejoDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return p;
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

        PreparedStatement ps = conexion.prepareStatement("SELECT pedido.id, fecha, e.nombre AS nombreestado, precio, idCliente, idRepartidor, idRestaurante FROM pedido, estado e WHERE idRestaurante=? AND pedido.idEstado=e.id ORDER BY fecha DESC;");

        ps.setInt(1, idRestaurante);
        resultset = ps.executeQuery();
        while (resultset.next()) {
            Pedido p = new Pedido();
            int id = resultset.getInt("id");
            p.setId(id);
            p.setFecha(resultset.getString("fecha"));
            p.setEstado(resultset.getString("nombreEstado"));
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
            c.setCiudad(resultset.getString("ciudad"));
        }
        return c;
    }

    public Pedido obtenerPedido(int idPedido) throws SQLException {
        this.sentencia = conexion.createStatement();
        PreparedStatement ps = conexion.prepareStatement("SELECT p.id, p.precio, p.fecha, e.nombre AS nombreestado, p.idCliente, p.idRepartidor FROM pedido p, estado e WHERE p.id=? AND p.idEstado=e.id;");

        ps.setInt(1, idPedido);

        resultset = ps.executeQuery();

        Pedido p = new Pedido();
        while (resultset.next()) {
            p.setId(resultset.getInt("id"));
            p.setPrecio(resultset.getDouble("precio"));
            p.setFecha(resultset.getString("fecha"));
            p.setEstado(resultset.getString("nombreestado"));
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

    //Obtiene todos los restaurantes para mostrarselos al cliente (m??s adelante filtrar por ciudad)
    public ArrayList<Restaurante> obtenerRestaurantes() {
        ArrayList<Restaurante> listaRestaurantes = new ArrayList<>();

        try {
            PreparedStatement ps = conexion.prepareStatement("SELECT r.id, r.nombre, r.email, r.direccion, r.ciudad, r.telefono, c.nombre AS nombreCategoria FROM restaurante r, categoria c WHERE usuario!='admin' AND r.idCategoria=c.id;");

            resultset = ps.executeQuery();

            while (resultset.next()) {
                Restaurante r = new Restaurante();
                r.setId(resultset.getInt("id"));
                r.setNombre(resultset.getString("nombre"));
                r.setEmail(resultset.getString("email"));
                r.setDireccion(resultset.getString("direccion"));
                r.setCiudad(resultset.getString("ciudad"));
                r.setTelefono(resultset.getString("telefono"));
                r.setCategoria(resultset.getString("nombreCategoria"));
                listaRestaurantes.add(r);
            }

        } catch (SQLException ex) {
            Logger.getLogger(ManejoDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return listaRestaurantes;
    }

    //Editar usuario (sin contrase??a)
    public boolean modificarPerfilUsuario(String usuarioActual, String nuevoUsuario, String email, String nombre, String direccion, String ciudad) {
        try {
            PreparedStatement ps = conexion.prepareStatement("UPDATE cliente SET usuario=?, nombre=?, email=?, direccion=?, ciudad=? WHERE usuario=?");

            ps.setString(1, nuevoUsuario);
            ps.setString(2, nombre);
            ps.setString(3, email);
            ps.setString(4, direccion);
            ps.setString(5, ciudad);
            ps.setString(6, usuarioActual);

            int resultado = ps.executeUpdate();
            
            return true;
        } catch (SQLException e) {
            e.getMessage();
        }
        return false;
    }

    //Editar usuario (con contrase??a)
    public boolean modificarPerfilUsuarioContrasena(String usuarioActual, String nuevoUsuario, String email, String nombre, String direccion, String ciudad, String contrasena) {
        try {
            PreparedStatement ps = conexion.prepareStatement("UPDATE cliente SET usuario=?, nombre=?, email=?, direccion=?, ciudad=?, contrasena=? WHERE usuario=?");

            ps.setString(1, nuevoUsuario);
            ps.setString(2, nombre);
            ps.setString(3, email);
            ps.setString(4, direccion);
            ps.setString(5, ciudad);
            ps.setString(6, contrasena);
            ps.setString(7, usuarioActual);

            int resultado = ps.executeUpdate();
            
            return true;
            
        } catch (SQLException e) {
            e.getMessage();
        }
        
        return false;
    }

    //Devuelve todas las categorias para a??adirlas al spinner
    public ArrayList<String> obtenerCategorias() {
        ArrayList<String> listaCategorias = new ArrayList<>();

        try {
            PreparedStatement ps = conexion.prepareStatement("SELECT nombre FROM categoria;");

            resultset = ps.executeQuery();

            while (resultset.next()) {
                listaCategorias.add(resultset.getString("nombre"));
            }

        } catch (SQLException ex) {
            Logger.getLogger(ManejoDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return listaCategorias;
    }

    //Devuelve todas las valoraciones de un restaurante
    public ArrayList<Valoracion> obtenerValoraciones(int idRestaurante) {
        ArrayList<Valoracion> listaValoraciones = new ArrayList<>();

        try {
            PreparedStatement ps = conexion.prepareStatement("SELECT * FROM valoracion WHERE idRestaurante=?;");

            ps.setInt(1, idRestaurante);

            resultset = ps.executeQuery();

            while (resultset.next()) {
                int id = resultset.getInt("id");
                String comentario = resultset.getString("comentario");
                int puntuacion = resultset.getInt("puntuacion");
                int idCliente = resultset.getInt("idCliente");
                listaValoraciones.add(new Valoracion(id, comentario, puntuacion, idCliente));
            }

        } catch (SQLException ex) {
            Logger.getLogger(ManejoDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return listaValoraciones;
    }

    //A??ade una valoraci??n
    public boolean anadirValoracion(int idRestaurante, String comentario, int puntuacion, int idCliente) {

        //Comprueba que haya hecho alg??n pedido anteriormente
        ArrayList<Pedido> pedidosUsuario = obtenerPedidosUsuario(idCliente);

        boolean haPedido = false;
        for (int i = 0; i < pedidosUsuario.size(); i++) {
            try {
                if (pedidosUsuario.get(i).getNombreRestaurante().equals(obtenerRestauranteId(idRestaurante).getNombre())) {
                    haPedido = true;
                }
            } catch (SQLException ex) {
                Logger.getLogger(ManejoDB.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (!haPedido) {
            return false;
        }

        //Si ha hecho alg??n pedido inserta la valoraci??n
        try {
            PreparedStatement ps = conexion.prepareStatement("INSERT INTO valoracion (idRestaurante, comentario, puntuacion, idCliente) VALUES(?,?,?,?)");

            ps.setInt(1, idRestaurante);
            ps.setString(2, comentario);
            ps.setInt(3, puntuacion);
            ps.setInt(4, idCliente);

            resultset = ps.executeQuery();

            return true;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    //A??ade un pedido al restaurante
    public int anadirPedido(double precio, String fecha, int idCliente, int idRestaurante) {
        try {
            PreparedStatement ps = conexion.prepareStatement("INSERT INTO pedido (precio, fecha, idEstado, idCliente, idRepartidor, idRestaurante) VALUES(?,?,'1',?, Null ,?)");

            ps.setDouble(1, precio);
            ps.setString(2, fecha);
            ps.setInt(3, idCliente);
            ps.setInt(4, idRestaurante);

            resultset = ps.executeQuery();

            int id;

            ps = conexion.prepareStatement("SELECT id FROM pedido WHERE precio=? AND fecha=? AND idCliente=? AND idRestaurante=?;");

            ps.setDouble(1, precio);
            ps.setString(2, fecha);
            ps.setInt(3, idCliente);
            ps.setInt(4, idRestaurante);

            resultset = ps.executeQuery();

            while (resultset.next()) {
                id = resultset.getInt("id");
                return id;
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return 0;
    }

    //A??ade un producto a un pedido (tabla productospedido);
    public boolean anadirProductoPedido(int idProducto, int idPedido) {
        try {
            PreparedStatement ps = conexion.prepareStatement("INSERT INTO productospedido (idProducto, idPedido) VALUES(?,?)");

            ps.setInt(1, idProducto);
            ps.setInt(2, idPedido);

            resultset = ps.executeQuery();

            return true;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    //A??ade el ID de un repartidor al pedido
    public void adjudicarRepartidor(int idRepartidor, int idPedido) {
        try {
            PreparedStatement ps = conexion.prepareStatement("UPDATE pedido SET idRepartidor=? WHERE id=?");

            ps.setInt(1, idRepartidor);
            ps.setInt(2, idPedido);

            int resultado = ps.executeUpdate();
        } catch (SQLException e) {
            e.getMessage();
        }
    }

    //Devuelve un repartidor
    public Repartidor obtenerRepartidor(int idRepartidor) {
        try {
            PreparedStatement ps = conexion.prepareStatement("SELECT * FROM repartidor WHERE id=?;");

            ps.setInt(1, idRepartidor);

            resultset = ps.executeQuery();

            Repartidor r = new Repartidor();
            while (resultset.next()) {
                r.setId(resultset.getInt("id"));
                r.setNombre(resultset.getString("nombre"));
                r.setDni(resultset.getString("dni"));
                r.setEmail(resultset.getString("email"));
                return r;
            }
        } catch (SQLException ex) {
            Logger.getLogger(ManejoDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    //Devuelve los pedidos de un usuario
    public ArrayList<Pedido> obtenerPedidosUsuario(int idCliente) {
        try {
            ArrayList<Pedido> listaPedidos = new ArrayList<>();

            PreparedStatement ps = conexion.prepareStatement("SELECT p.id, p.fecha, e.nombre AS nombreestado, p.precio, p.idRepartidor, r.nombre FROM pedido p, restaurante r, estado e WHERE p.idCliente=? AND p.idRestaurante=r.id AND p.idEstado=e.id ORDER BY fecha DESC;");

            ps.setInt(1, idCliente);
            resultset = ps.executeQuery();
            while (resultset.next()) {
                Pedido p = new Pedido();
                int id = resultset.getInt("id");
                p.setId(id);
                p.setFecha(resultset.getString("fecha"));
                p.setEstado(resultset.getString("nombreestado"));
                p.setPrecio(resultset.getDouble("precio"));
                p.setNombreRestaurante(resultset.getString("nombre"));
                listaPedidos.add(p);
            }
            return listaPedidos;
        } catch (SQLException ex) {
            Logger.getLogger(ManejoDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    //Devuelve los pedidos de un repartidor
    public ArrayList<Pedido> obtenerPedidosRepartidor(int idRepartidor) {
        try {
            ArrayList<Pedido> listaPedidos = new ArrayList<>();

            PreparedStatement ps = conexion.prepareStatement("SELECT p.id, p.fecha, p.precio, e.nombre AS nombreestado, p.idRepartidor, r.nombre FROM pedido p, restaurante r, estado e WHERE p.idRepartidor=? AND p.idRestaurante=r.id AND p.idEstado=e.id ORDER BY fecha DESC;");

            ps.setInt(1, idRepartidor);
            resultset = ps.executeQuery();
            while (resultset.next()) {
                Pedido p = new Pedido();
                int id = resultset.getInt("id");
                p.setId(id);
                p.setFecha(resultset.getString("fecha"));
                p.setEstado(resultset.getString("nombreestado"));
                p.setPrecio(resultset.getDouble("precio"));
                p.setNombreRestaurante(resultset.getString("nombre"));
                p.setIdRepartidor(resultset.getInt("idRepartidor"));
                listaPedidos.add(p);
            }
            return listaPedidos;
        } catch (SQLException ex) {
            Logger.getLogger(ManejoDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    //Obtener un restaurante con el ID
    public Restaurante obtenerRestauranteId(int idRestaurante) throws SQLException {
        this.sentencia = conexion.createStatement();
        PreparedStatement ps = conexion.prepareStatement("SELECT * FROM restaurante WHERE id=?;");

        ps.setInt(1, idRestaurante);

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

    public boolean iniciarSesionRepartidor(String usuario, String contrasena) {
        try {
            this.sentencia = conexion.createStatement();
            PreparedStatement ps = conexion.prepareStatement("SELECT usuario, contrasena FROM repartidor WHERE usuario=? AND contrasena=?;");

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

    public Repartidor obtenerRepartidorNombre(String usuario) throws SQLException {
        PreparedStatement ps = conexion.prepareStatement("SELECT * FROM repartidor WHERE usuario=?;");

        ps.setString(1, usuario);

        resultset = ps.executeQuery();

        Repartidor r = new Repartidor();
        while (resultset.next()) {
            r.setId(resultset.getInt("id"));
            r.setUsuario(resultset.getString("usuario"));
            r.setNombre(resultset.getString("nombre"));
            r.setEmail(resultset.getString("email"));
            r.setDni(resultset.getString("dni"));
            Restaurante restaurante = obtenerRestauranteId(resultset.getInt("idRestaurante"));
            r.setRestaurante(restaurante.getNombre());
        }
        return r;
    }

    //Editar repartidor (sin contrase??a)
    public boolean modificarPerfilRepartidor(String usuarioActual, String nuevoUsuario, String email, String nombre, String dni) {
        try {
            PreparedStatement ps = conexion.prepareStatement("UPDATE repartidor SET usuario=?, nombre=?, email=?, dni=? WHERE usuario=?");

            ps.setString(1, nuevoUsuario);
            ps.setString(2, nombre);
            ps.setString(3, email);
            ps.setString(4, dni);
            ps.setString(5, usuarioActual);

            int resultado = ps.executeUpdate();
            
            return true;
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        
        return false;
    }

    //Editar repartidor (con contrase??a)
    public boolean modificarPerfilRepartidorContrasena(String usuarioActual, String nuevoUsuario, String email, String nombre, String dni, String contrasena) {
        try {
            PreparedStatement ps = conexion.prepareStatement("UPDATE repartidor SET usuario=?, nombre=?, email=?, dni=?, contrasena=? WHERE usuario=?");

            ps.setString(1, nuevoUsuario);
            ps.setString(2, nombre);
            ps.setString(3, email);
            ps.setString(4, dni);
            ps.setString(5, contrasena);
            ps.setString(6, usuarioActual);

            int resultado = ps.executeUpdate();
            
            return true;
        } catch (SQLException e) {
            e.getMessage();
        }
        
        return false;
    }

    //Actualiza el estado de un pedido
    public boolean actualizarEstado(int idPedido, int idEstado) {
        try {
            PreparedStatement ps = conexion.prepareStatement("UPDATE pedido SET idEstado=? WHERE id=?");

            ps.setInt(1, idEstado);
            ps.setInt(2, idPedido);

            int resultado = ps.executeUpdate();

            if (resultado == 1) {
                return true;
            }
        } catch (SQLException e) {
            e.getMessage();
        }
        return false;
    }

    //Actualiza el estado de un pedido
    public ArrayList<String> obtenerEstados() {
        ArrayList<String> estados = new ArrayList();

        try {
            PreparedStatement ps = conexion.prepareStatement("SELECT nombre FROM estado;");

            resultset = ps.executeQuery();

            while (resultset.next()) {
                estados.add(resultset.getString("nombre"));
            }

        } catch (SQLException e) {
            e.getMessage();
        }
        return estados;
    }

    //Obtiene el nombre de un estado a traves del ID
    public String obtenerNombreEstado(int id) {
        String estado = "";
        try {
            PreparedStatement ps = conexion.prepareStatement("SELECT nombre FROM estado WHERE id=?;");

            ps.setInt(1, id);

            resultset = ps.executeQuery();

            while (resultset.next()) {
                estado = resultset.getString("nombre");
            }

        } catch (SQLException e) {
            e.getMessage();
        }
        return estado;
    }

    public void recargarUbicacion(int idUsuario, double latitud, double longitud) {
        try {
            PreparedStatement ps = conexion.prepareStatement("UPDATE repartidor SET latitud=?, longitud=? WHERE id=?");

            ps.setDouble(1, latitud);
            ps.setDouble(2, longitud);
            ps.setInt(3, idUsuario);

            int resultado = ps.executeUpdate();
        } catch (SQLException e) {
            e.getMessage();
        }
    }

}
