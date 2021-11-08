package com.pixelgrid;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;

public class EchoServer extends Thread {

    private DatagramSocket socket;
    private boolean running;
    private byte[] buf = new byte[256];
    private volatile String[] dataStringSplit;

    public EchoServer() throws SocketException {
        socket = new DatagramSocket(4445);
    }
    @Override
    public void run() {
        running = true;

        while (running) {
            System.out.println("running server");
            DatagramPacket packet
                    = new DatagramPacket(buf, buf.length);
            try {
                socket.receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }

            InetAddress address = packet.getAddress();
            System.out.println("Address " + address.toString());
            int port = packet.getPort();
            packet = new DatagramPacket(buf, buf.length, address, port);
            String received
                    = new String(packet.getData(), 0, packet.getLength()).trim();
            dataStringSplit = received.split(":");
            System.out.println("received: " + received);
            for(String data : dataStringSplit){
                System.out.println("dataStringSplit: " + data);
            }
            System.out.println(dataStringSplit.length);

            if (received.equals("end")) {
                System.out.println("ending");
                running = false;
                continue;
            }
            if (dataStringSplit[0].equals("Action")){
                System.out.println("It's action time");
                running = false;
                continue;
            }
            try {
                socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Arrays.fill(buf, (byte) '\n');
        }
        socket.close();
    }
    public String[] getDataString() {
        return dataStringSplit;
    }
}
