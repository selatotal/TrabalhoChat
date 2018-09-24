package br.ulbra.chat.server;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileControl {

    private static String filename = "messages.txt";
    private static Logger logger = Logger.getLogger("server");

    private static FileControl instance;
    private PrintWriter outputStream;
    private LinkedList<String> messages = new LinkedList<>();

    private FileControl(){
        try (Scanner inputStream = new Scanner(new File(filename))){
            while (inputStream.hasNextLine()){
                messages.add(inputStream.nextLine());
            }
        } catch (FileNotFoundException e){
            logger.log(Level.SEVERE, e.getMessage());
        }
        try {
            this.outputStream = new PrintWriter(new FileOutputStream(new File(filename), true));
        } catch (FileNotFoundException e) {
            logger.log(Level.SEVERE, e.getMessage());
        }

    }

    public static synchronized FileControl getInstance(){
        if (instance == null){
            instance = new FileControl();
        }
        return instance;
    }

    public synchronized void addMessage(String message){
        this.messages.add(message);
        this.outputStream.println(message);
        this.outputStream.flush();
    }

    public List<String> getLastMessages(){
        return this.messages.subList(Math.max(this.messages.size()-20, 0), this.messages.size());
    }

}
