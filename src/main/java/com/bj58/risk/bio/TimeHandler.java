package com.bj58.risk.bio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;

public class TimeHandler implements Runnable {

    private Socket socket;

    public TimeHandler(Socket socket) {

        this.socket = socket;
    }

    @Override
    public void run() {
        BufferedReader reader = null;
        PrintWriter printWriter = null;
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            printWriter = new PrintWriter(socket.getOutputStream());
            String body = null;
            String currentTime = null;
            while (true) {
                body = reader.readLine();
                if (body == null) {
                    break;
                }
                System.out.println("the time server receiver order : " + body);
                currentTime = "query time order".equals(body) ? new Date(System.currentTimeMillis()).toString() : "BAD ORDER";
                printWriter.print(currentTime);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (printWriter != null) {
                printWriter.close();
            }
            if (this.socket != null) {
                try {
                    System.out.println("close the socket");
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
