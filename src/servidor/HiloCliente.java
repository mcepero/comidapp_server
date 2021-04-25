/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Cliente;
import model.Pedido;
import model.Producto;
import model.Repartidor;
import model.Restaurante;
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
                String received;
                String flag = "";
                String[] args;

                while (true) {
                    received = entrada.readLine();
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
                        if (manejodb.registroUsuario(args[1], args[2], args[2], args[3], args[4])) {
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
                        if (manejodb.registroRestaurante(args[1], args[2], args[3], args[4], args[5], args[6], args[7])) {
                            System.out.println("Registro correcto");
                            salida.println(Mensajes.PETICION_REGISTRO_RESTAURANTE_CORRECTO);
                        } else {
                            System.out.println("Error al registrarse");
                            salida.println(Mensajes.PETICION_REGISTRO_RESTAURANTE_ERROR);
                        }
                        //Modificar los datos de un restaurante
                    } else if (flag.equals(Mensajes.PETICION_MODIFICAR_RESTAURANTE)) {
                        if (args.length == 7) {
                            manejodb.modificarRestaurante(args[1], args[2], args[3], args[4], args[5], args[6]);
                        } else if (args.length == 8) {
                            manejodb.modificarRestauranteContrasena(args[1], args[2], args[3], args[4], args[5], args[6], args[7]);
                        }
                        //Mostrar la carta de un restaurante
                    } else if (flag.equals(Mensajes.PETICION_MOSTRAR_CARTA)) {
                        ArrayList<Producto> listaProductos = manejodb.obtenerCarta(Integer.parseInt(args[1]));
                        salida.println(Mensajes.PETICION_MOSTRAR_CARTA_CORRECTO + "--" + listaProductos.size());
                        for (int i = 0; i < listaProductos.size(); i++) {
                            salida.println(Mensajes.PETICION_MOSTRAR_CARTA_CORRECTO + "--" + listaProductos.get(i).getNombre() + "--" + listaProductos.get(i).getIngredientes() + "--" + listaProductos.get(i).getPrecio() + "--" + listaProductos.get(i).getId());
                            // enviarImagen(listaProductos.get(i).getImagen());                         
                        }
                        //Añadir un producto a la carta de un restaurante
                    } else if (flag.equals(Mensajes.PETICION_ANADIR_PRODUCTO)) {
                        String nombreImagen = leerImagen(args[1], args[2]);
                        if (manejodb.anadirProducto(Integer.parseInt(args[1]), args[2], args[3], Double.parseDouble(args[4]), nombreImagen)) {
                            salida.println(Mensajes.PETICION_ANADIR_PRODUCTO_CORRECTO);
                        } else {
                            salida.println(Mensajes.PETICION_ANADIR_PRODUCTO_ERROR);
                        }
                    } else if (flag.equals(Mensajes.PETICION_MODIFICAR_PRODUCTO)) {
                        if (args.length == 6) {
                            String nombreImagen = leerImagen(args[1], args[2]);
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
                        salida.println(Mensajes.PETICION_MOSTRAR_DETALLESPEDIDO_CORRECTO + "--" + c.getDireccion() + "--" + c.getNombre() + "--" + p.getFecha() + "--" + p.getPrecio() + "--" + p.getEstado() + "--" + p.getIdCliente() + "--" + p.getIdRepartidor() + "--" + p.getProductos().size());
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
                            salida.println(Mensajes.PETICION_MOSTRAR_REPARTIDORES_CORRECTO + "--" + listaRepartidores.get(i).getId() + "--" + listaRepartidores.get(i).getNombre() + "--" + listaRepartidores.get(i).getUsuario() + "--" + listaRepartidores.get(i).getEmail() + "--" + listaRepartidores.get(i).getDni() + "--" + listaRepartidores.get(i).getDireccion());
                        }
                        //Elimina a un repartidor de un restaurante (en la BD le deja el campo idRestaurante null)
                    } else if (flag.equals(Mensajes.PETICION_ELIMINAR_REPARTIDOR)) {
                        manejodb.eliminarRepartidor(Integer.parseInt(args[1]));
                        //COMPROBAR QUE SE ELIMINA
                        salida.println(Mensajes.PETICION_ELIMINAR_REPARTIDOR_CORRECTO);
                    } else if (flag.equals(Mensajes.PETICION_MOSTRAR_RESTAURANTES)) {
                        ArrayList<Restaurante> listaRestaurantes = manejodb.obtenerRestaurantes();
                        salida.println(Mensajes.PETICION_MOSTRAR_RESTAURANTES_CORRECTO + "--" + listaRestaurantes.size());
                        
                        for (int i = 0; i < listaRestaurantes.size(); i++) {
                            salida.println(Mensajes.PETICION_MOSTRAR_RESTAURANTES_CORRECTO + "--" + listaRestaurantes.get(i).getNombre() + "--" + listaRestaurantes.get(i).getEmail() + "--" + listaRestaurantes.get(i).getTelefono() + "--" + listaRestaurantes.get(i).getDireccion() + "--" + listaRestaurantes.get(i).getCiudad());
                        }
                        //Edita la foto del producto
                    } else if (flag.equals(Mensajes.PETICION_FOTO_PRODUCTO)) {
                        Producto p = manejodb.obtenerProducto(Integer.parseInt(args[1]));
                        enviarImagen(p.getImagen());
                    } else if (flag.equals(Mensajes.PETICION_EDITAR_PERFIL)) {
                        if (args.length == 7) {
                            manejodb.modificarPerfilUsuario(args[1], args[2], args[3], args[4], args[5], args[6]);
                        } else if (args.length == 8) {
                            manejodb.modificarPerfilUsuarioContrasena(args[1], args[2], args[3], args[4], args[5], args[6], args[7]);
                        }
                        //Envía un restaurante para abrirlo
                    } else if (flag.equals(Mensajes.PETICION_OBTENER_RESTAURANTE)) {
                        Restaurante r = manejodb.obtenerRestauranteMapa(args[1],args[2],args[3]);
                        salida.println(Mensajes.PETICION_OBTENER_RESTAURANTE_CORRECTO + "--" + r.getNombre() + "--" + r.getEmail() + "--" + r.getTelefono()+ "--" + r.getDireccion() + "--" + r.getCiudad() + "--" + r.getId());
                    }

                }
            } catch (IOException ex) {
                Logger.getLogger(HiloCliente.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(HiloCliente.class.getName()).log(Level.SEVERE, null, ex);
            }catch(NullPointerException ex){
                System.out.println("Cliente desconectado.");
                try {
                    //¿?
                    socket.close();
                } catch (IOException ex1) {
                    Logger.getLogger(HiloCliente.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }
            /*    } catch (IOException ex) {
            Logger.getLogger(HiloCliente.class.getName()).log(Level.SEVERE, null, ex);
             */ //} finally {
            /*      try {
                oos.close();
            } catch (IOException ex) {
                Logger.getLogger(HiloCliente.class.getName()).log(Level.SEVERE, null, ex);
            }*/
        //}
    }

    public String leerImagen(String idRestaurante, String nombre) {
        InputStream in = null;
        OutputStream out = null;
        ArrayList<Byte> bytesImagen = new ArrayList<>();
        String nombreImagen = "";
        try {
            in = socket.getInputStream();
            nombreImagen = nombre + idRestaurante + ".jpg";
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
                out.write(bytes, 0, count);
            } while (count == 8096);

            out.close();
            /*in.close();*/
        } catch (IOException ex) {
            Logger.getLogger(HiloCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
        return nombreImagen;
    }

    public void enviarImagen(String imagen) {
        FileInputStream in = null;
        try {
            byte[] bytes = new byte[8096];
            in = new FileInputStream("E:\\manuel\\Documents\\DAM\\2 DAM 2020\\Proyecto\\Servidor\\imagenescomida\\" + imagen);
            OutputStream out = socket.getOutputStream();
            int count;
            while ((count = in.read(bytes)) > 0) {
                out.write(bytes, 0, count);
            }

            in.close();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(HiloCliente.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(HiloCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
