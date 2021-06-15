/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidor;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import model.Cliente;
import model.Pedido;
import model.Producto;
import model.Repartidor;
import model.Restaurante;
import model.Valoracion;
import utils.ManejoDB;
import utils.Mensajes;

/**
 *
 * @author Manuel
 */
public class HiloCliente extends Thread {

    private Socket socket;
    private Servidor servidor;
    private BufferedReader entrada;
    private PrintWriter salida;
    private ManejoDB manejodb;

    /* InputStream in;
    OutputStream out;*/
    // private ObjectOutputStream envioObjetos;
    public HiloCliente(Socket socket, Servidor servidor) {
        manejodb = new ManejoDB();
        this.socket = socket;
        this.servidor = servidor;
        try {
            entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            salida = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
        } catch (IOException ex) {
            Logger.getLogger(HiloCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        // ObjectOutputStream oos = null;
        //try {
        // oos = new ObjectOutputStream(socket.getOutputStream());
        try {
            String received="";
            String flag = "";
            String[] args;

            while (true) {
                try {
                    received = entrada.readLine();
                }catch(IOException e){
                    System.out.println("Error al leer del cliente");
                    break;
                }catch(Exception e){
                    System.out.println("Cliente desconectado");
                    break;
                }
                args = received.split("--");
                flag = args[0];
                if (flag.equals(Mensajes.PETICION_LOGIN)) {
                    //Inicio de sesión del usuario (parámentros usuario y contraseña)
                    if (manejodb.iniciarSesionUsuario(args[1], args[2])) {
                        System.out.println("Ok...");
                        Cliente c = manejodb.obtenerClienteNombre(args[1]);
                        salida.println(Mensajes.PETICION_LOGIN_CORRECTO + "--" + c.getId() + "--" + c.getEmail() + "--" + c.getNombre() + "--" + c.getDireccion() + "--" + c.getCiudad());
                    } else {
                        salida.println(Mensajes.PETICION_LOGIN_ERROR);
                        System.out.println("Error");
                    }
                    //Registro para usuario
                } else if (flag.equals(Mensajes.PETICION_REGISTRO)) {
                    if (manejodb.registroUsuario(args[1], args[2], args[3], args[4], args[5], args[6])) {
                        System.out.println("Registro correcto");
                        salida.println(Mensajes.PETICION_REGISTRO_CORRECTO);
                    } else {
                        System.out.println("Error al registrarse");
                        salida.println(Mensajes.PETICION_REGISTRO_ERROR);
                    }
                } else if (flag.equals(Mensajes.PETICION_LOGIN_RESTAURANTE)) {
                    //Inicio de sesión del restaurante (parámentros usuario y contraseña)
                    if (manejodb.iniciarSesionRestaurante(args[1], args[2])) {
                        System.out.println("Ok...");
                        Restaurante r = manejodb.obtenerRestaurante(args[1]);
                        salida.println(Mensajes.PETICION_LOGIN_RESTAURANTE_CORRECTO + "--" + r.getUsuario() + "--" + r.getNombre() + "--" + r.getEmail() + "--" + r.getDireccion() + "--" + r.getTelefono() + "--" + r.getId());
                    } else {
                        salida.println(Mensajes.PETICION_LOGIN_RESTAURANTE_ERROR);
                        System.out.println("Error");
                    }
                    //Registro del restaurante
                } else if (flag.equals(Mensajes.PETICION_REGISTRO_RESTAURANTE)) {
                    if (manejodb.registroRestaurante(args[1], args[2], args[3], args[4], args[5], args[6], args[7], Integer.parseInt(args[8]))) {
                        System.out.println("Registro correcto");
                        salida.println(Mensajes.PETICION_REGISTRO_RESTAURANTE_CORRECTO);
                    } else {
                        System.out.println("Error al registrarse");
                        salida.println(Mensajes.PETICION_REGISTRO_RESTAURANTE_ERROR);
                    }
                    //Modificar los datos de un restaurante
                } else if (flag.equals(Mensajes.PETICION_MODIFICAR_RESTAURANTE)) {
                    if (args.length == 8) {
                        boolean modificado = manejodb.modificarRestaurante(args[1], args[2], args[3], args[4], args[5], args[6],args[7]);
                        if (modificado) {
                            salida.println(Mensajes.PETICION_MODIFICAR_RESTAURANTE_CORRECTO);
                        }else{
                            salida.println(Mensajes.PETICION_MODIFICAR_RESTAURANTE_ERROR);
                        }
                    } else if (args.length == 9) {
                        boolean modificado = manejodb.modificarRestauranteContrasena(args[1], args[2], args[3], args[4], args[5], args[6], args[7],args[8]);
                         if (modificado) {
                            salida.println(Mensajes.PETICION_MODIFICAR_RESTAURANTE_CORRECTO);
                        }else{
                            salida.println(Mensajes.PETICION_MODIFICAR_RESTAURANTE_ERROR);
                        }
                    }
                    //Mostrar la carta de un restaurante
                } else if (flag.equals(Mensajes.PETICION_MOSTRAR_CARTA)) {
                    ArrayList<Producto> listaProductos = manejodb.obtenerCarta(Integer.parseInt(args[1]));
                    salida.println(Mensajes.PETICION_MOSTRAR_CARTA_CORRECTO + "--" + listaProductos.size());
                    for (int i = 0; i < listaProductos.size(); i++) {
                        salida.println(Mensajes.PETICION_MOSTRAR_CARTA_CORRECTO + "--" + listaProductos.get(i).getNombre() + "--" + listaProductos.get(i).getIngredientes() + "--" + listaProductos.get(i).getPrecio() + "--" + listaProductos.get(i).getId() + "--" + listaProductos.get(i).getIdRestaurante());
                        //enviarImagen(listaProductos.get(i).getImagen());                         
                    }
                    //Añadir un producto a la carta de un restaurante
                } else if (flag.equals(Mensajes.PETICION_ANADIR_PRODUCTO)) {
                    String nombreImagen = leerImagenAnadir(args[1], args[2]);
                    if (manejodb.anadirProducto(Integer.parseInt(args[1]), args[2], args[3], Double.parseDouble(args[4]), nombreImagen)) {
                        salida.println(Mensajes.PETICION_ANADIR_PRODUCTO_CORRECTO);
                    } else {
                        salida.println(Mensajes.PETICION_ANADIR_PRODUCTO_ERROR);
                    }
                } else if (flag.equals(Mensajes.PETICION_MODIFICAR_PRODUCTO)) {
                    if (args.length == 6) {
                        String nombreImagen = leerImagenAnadir(args[1], args[2]);
                        manejodb.modificarProductoConImagen(Integer.parseInt(args[1]), args[2], args[3], Double.parseDouble(args[4]), nombreImagen);
                        //COMPROBAR QUE SE AÑADE
                        salida.println(Mensajes.PETICION_MODIFICAR_PRODUCTO_CORRECTO);
                    } else {
                        manejodb.modificarProductoSinImagen(Integer.parseInt(args[1]), args[2], args[3], Double.parseDouble(args[4]));
                        //COMPROBAR QUE SE AÑADE
                        salida.println(Mensajes.PETICION_MODIFICAR_PRODUCTO_CORRECTO);
                    }
                } else if (flag.equals(Mensajes.PETICION_ELIMINAR_PRODUCTO)) {
                    manejodb.eliminarProducto(Integer.parseInt(args[1]));
                    //COMPROBAR QUE SE ELIMINA
                    salida.println(Mensajes.PETICION_ELIMINAR_PRODUCTO_CORRECTO);

                    //Obtiene todos los pedidos de un restaurante (recibe el ID desde el cliente)
                } else if (flag.equals(Mensajes.PETICION_MOSTRAR_PEDIDOS)) {
                    ArrayList<Pedido> pedidos = manejodb.obtenerPedidos(Integer.parseInt(args[1]));
                    salida.println(Mensajes.PETICION_MOSTRAR_PEDIDOS_CORRECTO + "--" + pedidos.size());
                    for (int i = 0; i < pedidos.size(); i++) {
                        Cliente c = manejodb.obtenerCliente(pedidos.get(i).getIdCliente());
                        salida.println(Mensajes.PETICION_MOSTRAR_PEDIDOS_CORRECTO + "--" + pedidos.get(i).getId() + "--" + pedidos.get(i).getFecha() + "--" + pedidos.get(i).getPrecio() + "--" + c.getDireccion() + "--" + pedidos.get(i).getIdRepartidor());
                    }

                    //Devuelve los detalles de un pedido dado su ID
                } else if (flag.equals(Mensajes.PETICION_MOSTRAR_DETALLESPEDIDO)) {
                    Pedido p = manejodb.obtenerPedido(Integer.parseInt(args[1]));
                    Cliente c = manejodb.obtenerCliente(p.getIdCliente());
                    Repartidor r = null;
                    if (p.getIdRepartidor() != 0) {
                        r = manejodb.obtenerRepartidor(p.getIdRepartidor());
                    }
                    if (r != null) {
                        salida.println(Mensajes.PETICION_MOSTRAR_DETALLESPEDIDO_CORRECTO + "--" + c.getDireccion() + "--" + c.getNombre() + "--" + p.getFecha() + "--" + p.getPrecio() + "--" + r.getNombre() + "--" + p.getEstado() + "--" + p.getIdCliente() + "--" + p.getIdRepartidor() + "--" + p.getProductos().size());
                    } else {
                        salida.println(Mensajes.PETICION_MOSTRAR_DETALLESPEDIDO_CORRECTO + "--" + c.getDireccion() + "--" + c.getNombre() + "--" + p.getFecha() + "--" + p.getPrecio() + "--" + 0 + "--" + p.getEstado() + "--" + p.getIdCliente() + "--" + p.getIdRepartidor() + "--" + p.getProductos().size());
                    }
                    for (int i = 0; i < p.getProductos().size(); i++) {
                        salida.println(Mensajes.PETICION_MOSTRAR_DETALLESPEDIDO_CORRECTO + "--" + p.getProductos().get(i).getNombre() + "--" + p.getProductos().get(i).getIngredientes() + "--" + p.getProductos().get(i).getPrecio());
                    }

                    //Añade un repartidor existente a un restaurante
                } else if (flag.equals(Mensajes.PETICION_ANADIR_REPARTIDOR)) {
                    if (manejodb.anadirRepartidorRestaurante(Integer.parseInt(args[1]), args[2])) {
                        salida.println(Mensajes.PETICION_ANADIR_REPARTIDOR_CORRECTO);
                    } else {
                        salida.println(Mensajes.PETICION_ANADIR_REPARTIDOR_ERROR);
                    }

                    //Muestra los repartidores del restaurante (aplicacion escritorio)
                } else if (flag.equals(Mensajes.PETICION_MOSTRAR_REPARTIDORES)) {
                    ArrayList<Repartidor> listaRepartidores = manejodb.obtenerRepartidores(Integer.parseInt(args[1]));
                    salida.println(Mensajes.PETICION_MOSTRAR_REPARTIDORES_CORRECTO + "--" + listaRepartidores.size());
                    for (int i = 0; i < listaRepartidores.size(); i++) {
                        salida.println(Mensajes.PETICION_MOSTRAR_REPARTIDORES_CORRECTO + "--" + listaRepartidores.get(i).getId() + "--" + listaRepartidores.get(i).getNombre() + "--" + listaRepartidores.get(i).getUsuario() + "--" + listaRepartidores.get(i).getEmail() + "--" + listaRepartidores.get(i).getDni());
                    }
                    //Elimina a un repartidor de un restaurante (en la BD le deja el campo idRestaurante null)
                } else if (flag.equals(Mensajes.PETICION_ELIMINAR_REPARTIDOR)) {
                    manejodb.eliminarRepartidor(Integer.parseInt(args[1]));
                    salida.println(Mensajes.PETICION_ELIMINAR_REPARTIDOR_CORRECTO);
                } else if (flag.equals(Mensajes.PETICION_MOSTRAR_RESTAURANTES)) {
                    ArrayList<Restaurante> listaRestaurantes = manejodb.obtenerRestaurantes();

                    salida.println(Mensajes.PETICION_MOSTRAR_RESTAURANTES_CORRECTO + "--" + listaRestaurantes.size());

                    for (int i = 0; i < listaRestaurantes.size(); i++) {
                        salida.println(Mensajes.PETICION_MOSTRAR_RESTAURANTES_CORRECTO + "--" + listaRestaurantes.get(i).getNombre() + "--" + listaRestaurantes.get(i).getEmail() + "--" + listaRestaurantes.get(i).getTelefono() + "--" + listaRestaurantes.get(i).getDireccion() + "--" + listaRestaurantes.get(i).getCiudad() + "--" + listaRestaurantes.get(i).getCategoria() + "--" + listaRestaurantes.get(i).getId());
                    }
                    //Edita la foto del producto
                    /*} else if (flag.equals(Mensajes.PETICION_FOTO_PRODUCTO)) {
                    Producto p = manejodb.obtenerProducto(Integer.parseInt(args[1]));
                    System.out.println("Imagen " + p.getImagen());
                    enviarImagenEscritorio(p.getImagen());
                   // enviarImagen(p.getImagen());
                     */
                }else if(flag.equals(Mensajes.PETICION_OBTENER_NUMERO_RESTAURANTES)){
                     ArrayList<Restaurante> listaRestaurantes = manejodb.obtenerRestaurantes();
                     salida.println(Mensajes.PETICION_OBTENER_NUMERO_RESTAURANTES_CORRECTO+"--"+listaRestaurantes.size());
                }else if(flag.equals(Mensajes.PETICION_MOSTRAR_RESTAURANTE_ID)){
                    int numRestaurante = Integer.parseInt(args[1]);
                    ArrayList<Restaurante> listaRestaurantes = manejodb.obtenerRestaurantes();
                    salida.println(Mensajes.PETICION_MOSTRAR_RESTAURANTE_ID_CORRECTO + "--" + listaRestaurantes.get(numRestaurante).getNombre() + "--" + listaRestaurantes.get(numRestaurante).getEmail() + "--" + listaRestaurantes.get(numRestaurante).getTelefono() + "--" + listaRestaurantes.get(numRestaurante).getDireccion() + "--" + listaRestaurantes.get(numRestaurante).getCiudad() + "--" + listaRestaurantes.get(numRestaurante).getCategoria() + "--" + listaRestaurantes.get(numRestaurante).getId());
                }else if (flag.equals(Mensajes.PETICION_EDITAR_PERFIL)) {
                     boolean modificado=false;
                    if (args.length == 7) {
                        modificado = manejodb.modificarPerfilUsuario(args[1], args[2], args[3], args[4], args[5], args[6]);
                    } else if (args.length == 8) {
                        modificado = manejodb.modificarPerfilUsuarioContrasena(args[1], args[2], args[3], args[4], args[5], args[6], args[7]);
                    }
                    
                    if (modificado) {
                        salida.println(Mensajes.PETICION_EDITAR_PERFIL_CORRECTO);
                    }else{
                        salida.println(Mensajes.PETICION_EDITAR_PERFIL_ERROR);
                    }
                    
                    //Envía un restaurante para abrirlo
                } else if (flag.equals(Mensajes.PETICION_OBTENER_RESTAURANTE)) {
                    Restaurante r = manejodb.obtenerRestauranteMapa(args[1], args[2], args[3]);
                    salida.println(Mensajes.PETICION_OBTENER_RESTAURANTE_CORRECTO + "--" + r.getNombre() + "--" + r.getEmail() + "--" + r.getTelefono() + "--" + r.getDireccion() + "--" + r.getCiudad() + "--" + r.getCategoria() + "--" + r.getId());
                } else if (flag.equals(Mensajes.PETICION_OBTENER_CATEGORIAS)) {
                    ArrayList<String> categorias = manejodb.obtenerCategorias();
                    salida.println(Mensajes.PETICION_OBTENER_CATEGORIAS_CORRECTO + "--" + categorias.size());
                    for (int i = 0; i < categorias.size(); i++) {
                        salida.println(Mensajes.PETICION_OBTENER_CATEGORIAS_CORRECTO + "--" + categorias.get(i));
                    }

                } else if (flag.equals(Mensajes.PETICION_MOSTRAR_VALORACIONES)) {
                    ArrayList<Valoracion> valoraciones = manejodb.obtenerValoraciones(Integer.parseInt(args[1]));
                    salida.println(Mensajes.PETICION_MOSTRAR_VALORACIONES_CORRECTO + "--" + valoraciones.size());
                    for (int i = 0; i < valoraciones.size(); i++) {
                        salida.println(Mensajes.PETICION_MOSTRAR_VALORACIONES_CORRECTO + "--" + valoraciones.get(i).getId() + "--" + valoraciones.get(i).getComentario() + "--" + valoraciones.get(i).getNota() + "--" + valoraciones.get(i).getIdCliente());
                    }
                } else if (flag.equals(Mensajes.PETICION_ANADIR_VALORACION)) {
                    boolean insertado = manejodb.anadirValoracion(Integer.parseInt(args[1]), args[2], Integer.parseInt(args[3]), Integer.parseInt(args[4]));
                    if (insertado) {
                        salida.println(Mensajes.PETICION_ANADIR_VALORACION_CORRECTO);

                    } else {
                        salida.println(Mensajes.PETICION_ANADIR_VALORACION_ERROR);

                    }
                } else if (flag.equals(Mensajes.PETICION_REALIZAR_PEDIDO)) {
                    int idPedido = manejodb.anadirPedido(Double.parseDouble(args[1]), args[2], Integer.parseInt(args[3]), Integer.parseInt(args[4]));
                    String[] idProductos = args[5].split("/");
                    for (int i = 0; i < idProductos.length; i++) {
                        manejodb.anadirProductoPedido(Integer.parseInt(idProductos[i]), idPedido);
                    }
                    // salida.println(Mensajes.PETICION_REALIZAR_PEDIDO_CORRECTO);
                } else if (flag.equals(Mensajes.PETICION_CARGAR_IMAGEN)) {
                    enviarImagen(args[2] + args[1] + ".jpg");
                } else if (flag.equals(Mensajes.PETICION_ADJUDICAR_REPARTIDOR)) {
                    manejodb.adjudicarRepartidor(Integer.parseInt(args[1]), Integer.parseInt(args[2]));
                } else if (flag.equals(Mensajes.PETICION_MOSTRAR_PEDIDOS_USUARIO)) {
                    ArrayList<Pedido> listaPedidos = manejodb.obtenerPedidosUsuario(Integer.parseInt(args[1]));
                    salida.println(Mensajes.PETICION_MOSTRAR_PEDIDOS_USUARIO_CORRECTO + "--" + listaPedidos.size());

                    for (int i = 0; i < listaPedidos.size(); i++) {
                        salida.println(Mensajes.PETICION_MOSTRAR_RESTAURANTES_CORRECTO + "--" + listaPedidos.get(i).getId() + "--" + listaPedidos.get(i).getPrecio() + "--" + listaPedidos.get(i).getFecha() + "--" + listaPedidos.get(i).getEstado() + "--" + listaPedidos.get(i).getNombreRestaurante());
                    }
                    //Envía los productos de un pedido para el usuario
                } else if (flag.equals(Mensajes.PETICION_MOSTRAR_DETALLES_PEDIDO)) {
                    Pedido p = manejodb.obtenerPedido(Integer.parseInt(args[1]));
                    salida.println(Mensajes.PETICION_MOSTRAR_DETALLES_PEDIDO_CORRECTO + "--" + p.getProductos().size());
                    for (int i = 0; i < p.getProductos().size(); i++) {
                        salida.println(Mensajes.PETICION_MOSTRAR_DETALLES_PEDIDO_CORRECTO + "--" + p.getProductos().get(i).getNombre() + "--" + p.getProductos().get(i).getPrecio() + "--" + p.getProductos().get(i).getIngredientes());
                    }

                    //Inicio de sesión del repartidor (parámentros usuario y contraseña)
                } else if (flag.equals(Mensajes.PETICION_LOGIN_REPARTIDOR)) {
                    if (manejodb.iniciarSesionRepartidor(args[1], args[2])) {
                        System.out.println("Ok...");
                        Repartidor r = manejodb.obtenerRepartidorNombre(args[1]);
                        salida.println(Mensajes.PETICION_LOGIN_REPARTIDOR_CORRECTO + "--" + r.getId() + "--" + r.getEmail() + "--" + r.getNombre() + "--" + r.getDni() + "--" + r.getRestaurante());
                    } else {
                        salida.println(Mensajes.PETICION_LOGIN_REPARTIDOR_ERROR);
                        System.out.println("Error");
                    }
                } else if (flag.equals(Mensajes.PETICION_REGISTRO_REPARTIDOR)) {
                    if (manejodb.registroRepartidor(args[1], args[2], args[3], args[4], args[5])) {
                        System.out.println("Registro correcto");
                        salida.println(Mensajes.PETICION_REGISTRO_REPARTIDOR_CORRECTO);
                    } else {
                        System.out.println("Error al registrarse");
                        salida.println(Mensajes.PETICION_REGISTRO_REPARTIDOR_ERROR);
                    }
                    //Edita el perfil del repartidor
                } else if (flag.equals(Mensajes.PETICION_EDITAR_PERFIL_REPARTIDOR)) {
                    boolean modificado = false;
                    if (args.length == 6) {
                        modificado = manejodb.modificarPerfilRepartidor(args[1], args[2], args[3], args[4], args[5]);
                    } else if (args.length == 7) {
                        modificado = manejodb.modificarPerfilRepartidorContrasena(args[1], args[2], args[3], args[4], args[5], args[6]);
                    }
                    
                    if (modificado) {
                        salida.println(Mensajes.PETICION_EDITAR_PERFIL_REPARTIDOR_CORRECTO);
                    }else{
                        salida.println(Mensajes.PETICION_EDITAR_PERFIL_REPARTIDOR_ERROR);
                    }
                    
                    //Envía la lista de pedidos de un repartidor (devuelve dirección del cliente también)
                } else if (flag.equals(Mensajes.PETICION_MOSTRAR_PEDIDOS_REPARTIDOR)) {
                    ArrayList<Pedido> listaPedidos = manejodb.obtenerPedidosRepartidor(Integer.parseInt(args[1]));
                    salida.println(Mensajes.PETICION_MOSTRAR_PEDIDOS_REPARTIDOR_CORRECTO + "--" + listaPedidos.size());

                    for (int i = 0; i < listaPedidos.size(); i++) {
                        Cliente c = manejodb.obtenerCliente(listaPedidos.get(i).getId());
                        salida.println(Mensajes.PETICION_MOSTRAR_PEDIDOS_REPARTIDOR_CORRECTO + "--" + listaPedidos.get(i).getId() + "--" + listaPedidos.get(i).getPrecio() + "--" + listaPedidos.get(i).getFecha() + "--" + listaPedidos.get(i).getEstado() + "--" + listaPedidos.get(i).getNombreRestaurante() + "--" + c.getDireccion() + "--" + c.getCiudad() + "--" + c.getEmail());
                    }
                    //Envía los detalles de un pedido al repartidor (con los datos de entrega)
                } else if (flag.equals(Mensajes.PETICION_MOSTRAR_DETALLES_PEDIDO_REPARTIDOR)) {
                    Pedido p = manejodb.obtenerPedido(Integer.parseInt(args[1]));
                    Cliente c = manejodb.obtenerCliente(p.getIdCliente());
                    salida.println(Mensajes.PETICION_MOSTRAR_DETALLES_PEDIDO_REPARTIDOR_CORRECTO + "--" + p.getId() + "--" + p.getPrecio() + "--" + p.getFecha() + "--" + p.getEstado() + "--" + p.getNombreRestaurante() + "--" + c.getNombre() + "--" + c.getEmail() + "--" + c.getDireccion() + "--" + c.getCiudad());
                } else if (flag.equals(Mensajes.PETICION_ACTUALIZAR_ESTADO)) {
                    manejodb.actualizarEstado(Integer.parseInt(args[1]), Integer.parseInt(args[2]));
                } else if (flag.equals(Mensajes.PETICION_OBTENER_ESTADOS)) {
                    //envioObjetos = new ObjectOutputStream(socket.getOutputStream());

                    ArrayList<String> estados = manejodb.obtenerEstados();
                    // envioObjetos.writeObject(estados);

                    salida.println(Mensajes.PETICION_OBTENER_ESTADOS_CORRECTO + "--" + estados.size());
                    for (int i = 0; i < estados.size(); i++) {
                        salida.println(Mensajes.PETICION_OBTENER_ESTADOS_CORRECTO + "--" + estados.get(i));
                    }
                } else if (flag.equals(Mensajes.PETICION_RECARGAR_UBICACION)) {
                    manejodb.recargarUbicacion(Integer.parseInt(args[1]), Double.parseDouble(args[2]), Double.parseDouble(args[3]));
                } else if (flag.equals(Mensajes.PETICION_RESTAURANTE_REPARTIDOR)) {
                    Repartidor r = manejodb.obtenerRepartidorNombre(args[1]);
                    salida.println(Mensajes.PETICION_RESTAURANTE_REPARTIDOR_CORRECTO+"--"+r.getRestaurante());
                }

            }
        }/* catch (IOException ex) {
            Logger.getLogger(HiloCliente.class.getName()).log(Level.SEVERE, null, ex);
        }*/ catch (SQLException ex) {
            Logger.getLogger(HiloCliente.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NullPointerException ex) {
            System.out.println("Cliente desconectado.");
            try {
                //¿?
                socket.close();
            } catch (IOException ex1) {
                Logger.getLogger(HiloCliente.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } catch (Exception e) {
            System.out.println("Cliente desconectado");
        }
    }

    public String leerImagen(String idRestaurante, String nombre) {
        InputStream in = null;
        OutputStream out = null;
        ArrayList<Byte> bytesImagen = new ArrayList<>();
        String nombreImagen = "";
        try {
            in = socket.getInputStream();
            nombreImagen = nombre + idRestaurante + ".jpg";
            System.out.println(nombreImagen);
            out = new FileOutputStream("E:\\manuel\\Documents\\DAM\\2 DAM 2020\\Proyecto\\Servidor\\imagenescomida\\" + nombreImagen);
            byte[] bytes = new byte[8096];

            int count;
            /*while ((count = in.read(bytes))>0) {
                System.out.println(count);
                System.out.println("!!!");
                out.write(bytes, 0, count);
            }*/

            do {
                count = in.read(bytes);
                System.out.println(count);
                out.write(bytes, 0, count);
            } while (count == 8096);

            out.close();
            //in.close();
        } catch (IOException ex) {
            Logger.getLogger(HiloCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
        return nombreImagen;
    }

    public String leerImagenAnadir(String idRestaurante, String nombreProducto) {
        InputStream is = null;
        String nombreImagen = nombreProducto + idRestaurante + ".jpg";

        try {
            is = socket.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            //Lee la imagen del servidor y la guarda en un fichero (se sobreescribe cada vez que llega una nueva)
            String cadena = br.readLine();
            byte[] decodedString = Base64.getDecoder().decode(cadena);
            File imagen = new File(".\\imagenescomida\\" + nombreImagen);
            FileOutputStream out = new FileOutputStream(imagen);
            out.write(decodedString);
            out.close();
        } catch (IOException ex) {
            Logger.getLogger(HiloCliente.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
        }
        return nombreImagen;

    }

    public void enviarImagen(String imagen) {
        /*FileInputStream  in = null;*/
        try {
            /*   byte[] bytes = new byte[8096];
            System.out.println(imagen);*/
            File in = new File("E:\\manuel\\Documents\\DAM\\2 DAM 2020\\Proyecto\\Servidor\\imagenescomida\\" + imagen);
            OutputStream out = socket.getOutputStream();
            /*  int count;
            do {
                count = in.read(bytes);
                System.out.println("Count: " + count);
                out.write(bytes, 0, count);
            } while (count == 8096);*/
            PrintWriter dos = new PrintWriter(out);
            String encode = Base64.getEncoder().encodeToString(Files.readAllBytes(in.toPath()));
            dos.println(encode);
            dos.flush();
            //in.close();
            //out.close();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(HiloCliente.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(HiloCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /*public void enviarImagenEscritorio(String imagen) {
        FileInputStream  in = null;
        try {
            byte[] bytes = new byte[8096];
             in = new FileInputStream("E:\\manuel\\Documents\\DAM\\2 DAM 2020\\Proyecto\\Servidor\\imagenescomida\\" + imagen);
            OutputStream out = socket.getOutputStream();
            int count;
            do {
                count = in.read(bytes);
                System.out.println("Count: " + count);
                out.write(bytes, 0, count);
            } while (count == 8096);
           in.close();
            //out.close();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(HiloCliente.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(HiloCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }*/
}
