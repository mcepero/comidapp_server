/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
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
        try {
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
                            salida.println(Mensajes.PETICION_LOGIN_CORRECTO+"--"+c.getId()+"--"+c.getEmail()+"--"+c.getNombre()+"--"+c.getDireccion());
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
                        }
                        //Añadir un producto a la carta de un restaurante
                    } else if (flag.equals(Mensajes.PETICION_ANADIR_PRODUCTO)) {
                        if (manejodb.anadirProducto(Integer.parseInt(args[1]), args[2], args[3], Double.parseDouble(args[4]))) {
                            salida.println(Mensajes.PETICION_ANADIR_PRODUCTO_CORRECTO);
                        } else {
                            salida.println(Mensajes.PETICION_ANADIR_PRODUCTO_ERROR);
                        }
                    } else if (flag.equals(Mensajes.PETICION_MODIFICAR_PRODUCTO)) {
                        manejodb.modificarProducto(Integer.parseInt(args[1]), args[2], args[3], Double.parseDouble(args[4]));
                        //COMPROBAR QUE SE AÑADE
                        salida.println(Mensajes.PETICION_MODIFICAR_PRODUCTO_CORRECTO);
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
                    } else if (flag.equals(Mensajes.PETICION_MOSTRAR_REPARTIDORES)) {
                        ArrayList<Repartidor> listaRepartidores = manejodb.obtenerRepartidores(Integer.parseInt(args[1]));
                        salida.println(Mensajes.PETICION_MOSTRAR_REPARTIDORES_CORRECTO + "--" + listaRepartidores.size());
                        for (int i = 0; i < listaRepartidores.size(); i++) {
                            salida.println(Mensajes.PETICION_MOSTRAR_REPARTIDORES_CORRECTO + "--" + listaRepartidores.get(i).getId() + "--" + listaRepartidores.get(i).getNombre() + "--" + listaRepartidores.get(i).getUsuario() + "--" + listaRepartidores.get(i).getEmail() + "--" + listaRepartidores.get(i).getDni() + "--" + listaRepartidores.get(i).getDireccion());
                        }
                    } else if (flag.equals(Mensajes.PETICION_ELIMINAR_REPARTIDOR)) {
                        manejodb.eliminarRepartidor(Integer.parseInt(args[1]));
                        //COMPROBAR QUE SE ELIMINA
                        salida.println(Mensajes.PETICION_ELIMINAR_REPARTIDOR_CORRECTO);
                    }else if (flag.equals("imagen")) {
                        leerImagen();
                        //salida.println(Mensajes.PETICION_ANADIR_PRODUCTO_CORRECTO);
                    }

                }
            } catch (IOException ex) {
                Logger.getLogger(HiloCliente.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(HiloCliente.class.getName()).log(Level.SEVERE, null, ex);
            }
            /*    } catch (IOException ex) {
            Logger.getLogger(HiloCliente.class.getName()).log(Level.SEVERE, null, ex);
             */ } finally {
            /*      try {
                oos.close();
            } catch (IOException ex) {
                Logger.getLogger(HiloCliente.class.getName()).log(Level.SEVERE, null, ex);
            }*/
        }
    }

    public void leerImagen() {
        DataInputStream dis = null;
        try {
            dis = new DataInputStream(socket.getInputStream());
            FileOutputStream fos = new FileOutputStream("testfile.jpg");
            byte[] buffer = new byte[4096];
            int filesize = 15123; // Send file size in separate msg
            int read = 0;
            int totalRead = 0;
            int remaining = filesize;
            while ((read = dis.read(buffer, 0, Math.min(buffer.length, remaining))) > 0) {
                totalRead += read;
                remaining -= read;
                System.out.println("read " + totalRead + " bytes.");
                fos.write(buffer, 0, read);
            }   fos.close();
            dis.close();
        } catch (IOException ex) {
            Logger.getLogger(HiloCliente.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                dis.close();
            } catch (IOException ex) {
                Logger.getLogger(HiloCliente.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
