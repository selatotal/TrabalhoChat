package br.ulbra.chat.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Control extends Thread {

    private Scanner inputStream;
    private PrintWriter outputStream;
    private String nickname;
    private Socket socket;

    private static Logger logger = Logger.getLogger("server");
    private static final String INVALID_COMMAND = "Invalid Command";

    public Control(Socket socket) throws IOException {
        this.inputStream = new Scanner(socket.getInputStream());
        this.outputStream = new PrintWriter(socket.getOutputStream());
        this.socket = socket;
    }

    private void signin(String commandLine){
        String[] line = commandLine.split(" +", 2);
        if (line.length < 2 || line[1].length() == 0) {
            sendError(INVALID_COMMAND);
        } else {
            if (ThreadControl.hasNickname(line[1].trim())){
                sendError("Nickname existente");
            } else {
                nickname = line[1];
                List<String> lastMessages = FileControl.getInstance().getLastMessages();
                for(String message : lastMessages){
                    outputStream.println("MSG " + message);
                    outputStream.flush();
                }
                ThreadControl.sendMessageToAll(nickname + " entrou no grupo");
            }
        }
    }

    private void exitChat(){
        if (nickname != null) {
            ThreadControl.sendMessageToAll(nickname + " saiu do grupo");
        }
        ThreadControl.removeClient(this);
    }

    private void message(String commandLine){
        String[] line = commandLine.split(" +", 2);
        if (nickname == null || line.length < 2 || line[1].length() == 0) {
            sendError(INVALID_COMMAND);
        } else {
            String message = line[1];
            ThreadControl.sendMessageToAll(nickname + " " + message);
        }
    }

    @Override
    public void run(){
        boolean shouldContinue = true;

        do {
            try {
                String commandLine = inputStream.nextLine();
                if (commandLine.startsWith("ENTRAR ")) {
                    signin(commandLine);
                } else if (commandLine.startsWith("SAIR")) {
                    exitChat();
                    shouldContinue = false;
                } else if (commandLine.startsWith("MSG ")) {
                    message(commandLine);
                } else {
                    sendError(INVALID_COMMAND);
                }
            } catch (NoSuchElementException e){
                ThreadControl.sendMessageToAll(nickname + " SAIU DO GRUPO COM ERRO!");
                logger.log(Level.SEVERE, "CLIENT DISCONNECTED: " + e.getMessage());
                exitChat();
                shouldContinue = false;
            }
        } while (shouldContinue);
        try {
            socket.close();
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
    }

    public String getNickname(){
        return this.nickname;
    }

    private void sendError(String message){
        outputStream.println("ERROR " + message);
        outputStream.flush();
    }

    public void sendMessage(String message){
        outputStream.println("MSG " + message);
        outputStream.flush();
    }

    public void exitByMaxClients(){
        try {
            sendError("Max clients Reached");
            this.socket.close();
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
    }
}
