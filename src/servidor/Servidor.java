/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Manuel
 */
public class Servidor {

    public static final int PORT = 4444;
    private ArrayList<HiloCliente> hilos;
    
    public Servidor() {
        hilos = new ArrayList<>();
    }

    public static void main(String[] args) {
        Servidor s = new Servidor();
        s.ejecutar();
    }

    public void ejecutar() {
        ServerSocket socketServidor = null;

        try {
            socketServidor = new ServerSocket(PORT);
        } catch (IOException e) {
            System.out.println("No puede escuchar en el puerto: " + PORT);
        }
        Socket socketCliente = null;

        System.out.println("Escuchando: " + socketServidor);

        while (true) {
            try {
                socketCliente = socketServidor.accept();
                System.out.println("Conexión aceptada: " + socketCliente);

                HiloCliente h = new HiloCliente(socketCliente, this);
                h.start();
                hilos.add(h);
            } catch (IOException ex) {
                Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
