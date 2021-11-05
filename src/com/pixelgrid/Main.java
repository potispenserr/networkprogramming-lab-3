package com.pixelgrid;

import javax.swing.*;
import javax.swing.JPanel;
import javax.swing.BorderFactory;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Main {

    public static void main(String[] args) {
	// write your code here aka Do stuff
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    private static void createAndShowGUI() {
        System.out.println("Created GUI on EDT? "+
                SwingUtilities.isEventDispatchThread());
        JFrame f = new JFrame("Swing Paint Demo");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(1200, 700);
        f.add(new KankerPanel());
        f.pack();
        f.setVisible(true);
        f.setLocationRelativeTo(null);
        f.setResizable(false);
    }
}

class KankerPanel extends JPanel {
    private int squareX = 50;
    private int squareY = 50;
    private int squareW = 3;
    private int squareH = 20;
    private Pixel[][] grid = new Pixel[201][201];

    public KankerPanel() {
        setBorder(BorderFactory.createLineBorder(Color.black));
        int startX = 0, startY = 0, counter = 1;
        for(int row = 0; row < 201; row++){
            for(int col = 0; col < 201; col++){
                grid[row][col] = new Pixel();
                grid[row][col].count = counter;
                grid[row][col].x = startX;
                grid[row][col].y = startY;
                grid[row][col].color = Color.BLUE;
                grid[row][col].width = squareW;
                System.out.println("created pixel at X: " + grid[row][col].x + " Y: " + grid[row][col].y);
                startY += squareW;
                counter++;
            }
            startY = 0;
            startX += squareW;

        }

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
                moveSquare(e.getX(),e.getY());
            }
        });
    }

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

    private void moveSquare(int x, int y) {
        int OFFSET = 1;
        if ((squareX!=x) || (squareY!=y)) {
            repaint(squareX,squareY,squareW+OFFSET,squareH+OFFSET);
            squareX=x;
            squareY=y;
            repaint(squareX,squareY,squareW+OFFSET,squareH+OFFSET);
        }
    }

    public Dimension getPreferredSize() {
        return new Dimension(600,600);
    }

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
    }
}