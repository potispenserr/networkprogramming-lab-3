package com.pixelgrid;

import javax.swing.*;
import javax.swing.JPanel;
import javax.swing.BorderFactory;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.SocketException;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * Main is where everything starts
 * JFrame jFrame
 * SquarePanel squarePanel
 * Ruben Wvwc
 * Version 1.0
 * 09-11-2021
 */
public class Main {

    public JFrame jFrame = new JFrame("Twitch plays pixel art");
    public SquarePanel squarePanel = new SquarePanel();

    /**
     * Starts up the GUI and UDP server
     * WHen the UDP server thread dies that is to say when it receives a valid package it gets the data from dataStringSplit from Echoserver
     * Then calls the changePixelColor on the squarePanel with the instructions in the packet and restarts the EchoServer thread
     * @param args
     * @throws SocketException
     */
    public static void main(String[] args) throws SocketException {
	// write your code here aka Do stuff
        Main mainObj = new Main();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                mainObj.createAndShowGUI();
            }

        });
        EchoServer echoServer = null;
        try {
            echoServer = new EchoServer();
        }
        catch (SocketException e) {
            e.printStackTrace();
        }
        echoServer.start();
        while (true) {

            if (echoServer.isAlive() == true) {
                //System.out.println("i'm still standing");
            } else {
                System.out.println("thread is kill");
                try {
                    echoServer.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                String[] udpStringSplit = echoServer.getDataString();
                System.out.println(echoServer.isAlive());
                System.out.println("udpStringSplit is this long " + udpStringSplit.length);
                Color udpColor = null;
                switch (udpStringSplit[3]){
                    case "Black": udpColor = Color.BLACK; break;
                    case "Blue": udpColor = Color.BLUE; break;
                    case "Magenta": udpColor = Color.MAGENTA; break;
                    case "Orange": udpColor = Color.ORANGE; break;
                    case "Green": udpColor = Color.GREEN; break;
                    case "Yellow": udpColor = Color.YELLOW; break;
                    case "Red": udpColor = Color.red; break;
                    case "Cyan": udpColor = Color.cyan; break;


                }
                if(udpStringSplit[0].equals("Leave")){
                    System.out.println("Player left");
                    mainObj.squarePanel.removePlayer(udpColor);
                }
                else if(udpStringSplit[0].equals("Action")) {
                    System.out.println("Changing pixel at X: " + udpStringSplit[1] + " Y: " + udpStringSplit[2] + " with the color " + udpColor.toString());
                    mainObj.squarePanel.changePixelColorUDP(Integer.parseInt(udpStringSplit[1]), Integer.parseInt(udpStringSplit[2]), udpColor);
                }

                echoServer = new EchoServer();
                echoServer.start();

            }

        }
    }

    /**
     * Starts up and initializes the GUI
     */
    private void createAndShowGUI() {
        System.out.println("Created GUI on EDT? "+
                SwingUtilities.isEventDispatchThread());

        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setSize(1200, 700);
        jFrame.add(squarePanel);
        jFrame.pack();
        jFrame.setVisible(true);
        jFrame.setLocationRelativeTo(null);
        jFrame.setResizable(false);
    }
}

class Player {
    Color color;
    int x;
    int y;
    Player(Color newColor, int newX, int newY){
        color = newColor;
        x = newX;
        y = newY;
    }
}

/**
 * SquarePanel contains the grid of pixels and methods to manipulate them
 * int squareW: square width
 * Pixel[][] grid: holds all pixels
 * Ruben Wvwc
 * Version 1.0
 * 09-11-2021
 */
class SquarePanel extends JPanel {
    private int squareW = 3;
    private Pixel[][] grid = new Pixel[201][201];
    public Dictionary<Color, Player> playerList = new Hashtable<Color, Player>();


    /**
     * initializes all Pixels in grid and adds mouse control
     */
    SquarePanel() {
        //setBorder(BorderFactory.createLineBorder(Color.black));
        int startX = 0, startY = 0, counter = 1;
        for(int row = 0; row < 201; row++){
            for(int col = 0; col < 201; col++){
                grid[row][col] = new Pixel();
                grid[row][col].count = counter;
                grid[row][col].x = startX;
                grid[row][col].y = startY;
                grid[row][col].color = Color.WHITE;
                grid[row][col].width = squareW;
                //System.out.println("created pixel at X: " + grid[row][col].x + " Y: " + grid[row][col].y);
                startY += squareW;
                counter++;
            }
            startY = 0;
            startX += squareW;

        }
        playerList.put(Color.BLACK, new Player(Color.BLACK, -1, -1));
        playerList.put(Color.BLUE, new Player(Color.BLUE, -1, -1));
        playerList.put(Color.MAGENTA, new Player(Color.MAGENTA, -1, -1));
        playerList.put(Color.white, new Player(Color.white, -1, -1));
        playerList.put(Color.green, new Player(Color.green, -1, -1));
        playerList.put(Color.yellow, new Player(Color.YELLOW, -1, -1));
        playerList.put(Color.red, new Player(Color.red, -1, -1));
        playerList.put(Color.cyan, new Player(Color.cyan, -1, -1));
        playerList.put(Color.ORANGE, new Player(Color.ORANGE, -1, -1));



        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                //moveSquare(e.getX(),e.getY());
                changePixelColor(e.getX(), e.getY());
                int clickedX = Math.floorDiv(e.getX(), squareW);
                int clickedY = Math.floorDiv(e.getY(), squareW);

                System.out.println("ClickedX: " + Integer.toString(e.getX()) + " " + " ClickedY: " + Integer.toString(e.getY()) + " Count: " + Integer.toString(grid[clickedX][clickedY].count));


            }
        });

        addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent e) {

            }
        });
    }

    /**
     * Takes clicked x and y coordinate and assings that pixel a random color
     * @param x coordinate
     * @param y coordinate
     */
    private void changePixelColor(int x, int y) {
        int clickedX = Math.floorDiv(x, squareW);
        int clickedY = Math.floorDiv(y, squareW);
        int max = 3;
        int b = (int)(Math.random()*(max-0+1)+0);
        Color randomColor = null;
        switch(b){
            case 0: {
                randomColor = Color.CYAN;
                break;
            }
            case 1: {
                randomColor = Color.RED;
                break;
            }
            case 2: {
                randomColor = Color.magenta;
                break;
            }
            case 3: {
                randomColor = Color.BLACK;
                break;
            }
        }
        grid[clickedX][clickedY].color = randomColor;
        repaint(grid[clickedX][clickedY].x, grid[clickedX][clickedY].y, grid[clickedX][clickedY].width, grid[clickedX][clickedY].width);

    }

    /**
     * Changes the pixel at the x and y coordinates to the provided color
     * @param x coordinate from UDP packet
     * @param y coordinate from UDP packet
     * @param color from UDP packet
     */
    public void changePixelColorUDP(int x, int y, Color color){
        Player player = playerList.get(color);
        int oldX = player.x;
        int oldY = player.y;

        player.x = x;
        player.y = y;
        grid[x][y].color = color;
        repaint(grid[x][y].x, grid[x][y].y, grid[x][y].width, grid[x][y].width);

        if(oldX >= 0 || oldY >= 0){
            grid[oldX][oldY].color = Color.BLUE;
            repaint(grid[oldX][oldY].x, grid[oldX][oldY].y, grid[oldX][oldY].width, grid[oldX][oldY].width);
        }

    }
    public void removePlayer(Color color){
        Player player = playerList.get(color);

        System.out.println("Removing player color" + player.color);

        int oldX = player.x;
        int oldY = player.y;

        player.x = -1;
        player.y = -1;

        grid[oldX][oldY].color = Color.BLUE;
        repaint(grid[oldX][oldY].x, grid[oldX][oldY].y, grid[oldX][oldY].width, grid[oldX][oldY].width);

    }

    /**
     *
     * @return appropriate window size
     */
    public Dimension getPreferredSize() {
        return new Dimension(600,600);
    }


    /**
     * Draws all pixels
     * @param g
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw Text
        g.drawString("This is my custom Panel!",10,20);

       /* g.setColor(Color.RED);
        g.fillRect(squareX,squareY,squareW,squareH);
        g.setColor(Color.BLACK);
        g.drawRect(squareX,squareY,squareW,squareH);*/

        for (int i = 0; i < grid.length; i++){
            for (int j = 0; j < grid.length; j++){
                Pixel pixel = grid[i][j];
                g.setColor(pixel.color);
                g.fillRect(pixel.x, pixel.y, pixel.width, pixel.width);
            }
        }
        /*for (Player player : playerlist){
            if(player.x == -1 || player.y == -1){
                continue;
            }
            grid[player.x][player.y].color = player.color;
        }*/
        for (Enumeration<Player> players = playerList.elements(); players.hasMoreElements();) {
            Player player = players.nextElement();
            if(player.x == -1 || player.y == -1){
                continue;
            }
            grid[player.x][player.y].color = player.color;
            System.out.println("PlayerColor: " + player.color + " X:" + player.x + " Y: " + player.y);
        }
    }
}