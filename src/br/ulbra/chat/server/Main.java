package br.ulbra.chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    private static Logger logger = Logger.getLogger("server");
    private static int port = 8976;

    public static void main(String[] args) {

        logger.setLevel(Level.ALL);

        // Start server
        try (ServerSocket serverSocket = new ServerSocket(port)){

            logger.log(Level.INFO, "Server started at port {0} ", port);
            do {

                logger.log(Level.INFO, "Waiting connection..." );
                Socket socket = serverSocket.accept();
                logger.log(Level.INFO, "Client Connected from " + socket.getInetAddress().getHostAddress());

                // Create new client
                Control control = new Control(socket);
                if (ThreadControl.addClient(control)) {
                    control.start();
                } else {
                    control = null;
                }

            } while (true);
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
    }
}
