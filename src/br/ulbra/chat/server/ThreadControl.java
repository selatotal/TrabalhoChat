package br.ulbra.chat.server;

import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ThreadControl {

    private static HashSet<Control> clientes = new HashSet<>();
    private static Logger logger = Logger.getLogger("server");

    private ThreadControl(){}

    public static boolean addClient(Control control) {
        if (clientes.size() < 5) {
            clientes.add(control);
            return true;
        }
        control.exitByMaxClients();
        logger.log(Level.SEVERE, "Max clients reached!");
        return false;
    }

    public static void removeClient(Control control){
        clientes.remove(control);
    }

    public static boolean hasNickname(String nickname){
        for (Control control : clientes){
            if (nickname.equalsIgnoreCase(control.getNickname())){
                logger.log(Level.SEVERE, "Nickname already exists: {0}", nickname);
                return true;
            }
        }
        return false;
    }

    public static void sendMessageToAll(String message){
        FileControl.getInstance().addMessage(message);
        logger.log(Level.INFO, "Sending message to all: {0}", message);
        for (Control control : clientes){
            control.sendMessage(message);
        }
    }

}
