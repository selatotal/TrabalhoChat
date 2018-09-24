package br.ulbra.chat.client;

import java.util.Scanner;

public class Receiver extends Thread {

    private Scanner inputStream;
    private boolean shouldExit = false;

    public Receiver(Scanner inputStream){
        this.inputStream = inputStream;
    }

    @Override
    public void run(){

        do {
            String message = inputStream.nextLine();
            System.out.println(message);
        } while(!shouldExit);
    }

    public void exitThread(){
        this.shouldExit = true;
    }
}
