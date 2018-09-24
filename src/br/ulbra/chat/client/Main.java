package br.ulbra.chat.client;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    private static Logger logger = Logger.getLogger("client");

    public static void main(String[] args){

        Scanner input = new Scanner(System.in);
        logger.setLevel(Level.ALL);

        System.out.print("Server IP: ");
        String serverIP = input.nextLine();
        System.out.print("Server Port: ");
        int serverPort = Integer.parseInt(input.nextLine());

        try (Socket socket = new Socket(serverIP, serverPort)){

            Scanner inputStream = new Scanner(socket.getInputStream());
            PrintWriter outputStream = new PrintWriter(socket.getOutputStream());
            boolean nicknameOK = false;
            String nickname = "";
            do {
                System.out.print("Choose Nickname: ");
                nickname = input.nextLine();
                outputStream.println("ENTRAR " + nickname);
                outputStream.flush();
                String response = inputStream.nextLine();
                if (!response.startsWith("ERROR")){
                    nicknameOK = true;
                } else {
                    System.out.println(response);
                }
            } while (!nicknameOK);

            Receiver receiver = new Receiver(inputStream);
            receiver.start();

            boolean shouldExit = false;
            System.out.println("Write your message or SAIR to exit:");
            do {
                String message = input.nextLine();
                if ("SAIR".equals(message)){
                    outputStream.println("SAIR");
                    shouldExit = true;
                } else {
                    outputStream.println("MSG " + message);
                }
                outputStream.flush();
            } while (!shouldExit);
            receiver.exitThread();
            receiver.join();
        } catch (IOException | InterruptedException e) {
            logger.log(Level.SEVERE, e.getMessage());
        }

    }
}
