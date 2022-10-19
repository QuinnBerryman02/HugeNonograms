package src.main;

import javax.swing.*;

import src.main.Nonogram.ColorAmount;
import src.main.Nonogram.Hints;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class Window extends JFrame implements MouseListener, MouseMotionListener{  
    Nonogram nonogram;
    int bx=50,by=50;
    int mx,my;
    boolean dragging = false;

    public Window(Nonogram nono) {
        super("Nonogram Program");
        nonogram = nono;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000,1000);
        setLayout(null);
        setVisible(true);
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        int cellSize = 5;
        g.setFont(new Font("my font",Font.ITALIC,cellSize));
        int w = nonogram.W * cellSize;
        int h = nonogram.H * cellSize;
        int hw = nonogram.hintW * cellSize;
        int hh = nonogram.hintH * cellSize;
        g.drawRect(bx, by+hh, hw, h);
        g.drawRect(bx+hw, by, w, hh);
        g.drawRect(bx+hw, by+hh, w, h);
        Hints hint = nonogram.obscuredHints;
        
        // for(int i=0;i<hint.rows.size();i++) {
        //     int rowMax = hint.rows.get(i).size();
        //     int x0 = (bx + hw) - (rowMax) * cellSize;
        //     int y0 = (by + hh) + (i) * cellSize;
        //     for(int j=0;j<rowMax;j++) {
        //         ColorAmount ca = hint.rows.get(i).get(j);
        //         g.setColor(ca.color.toColor());
        //         g.fillRect(x0+(j)*cellSize, y0, cellSize, cellSize);
        //         g.setColor(ca.color.isLight() ? Color.BLACK : Color.WHITE);
        //         char[] chars = Integer.toString(ca.amount).toCharArray();
        //         g.drawChars(chars, 0, chars.length, x0+(j)*cellSize, y0+cellSize);
        //     }
        // }
        // for(int j=0;j<hint.cols.size();j++) {
        //     int colMax = hint.cols.get(j).size();
        //     int x0 = (bx + hw) + (j) * cellSize;
        //     int y0 = (by + hh) - (colMax) * cellSize;
        //     for(int i=0;i<colMax;i++) {
        //         ColorAmount ca = hint.cols.get(j).get(i);
        //         g.setColor(ca.color.toColor());
        //         g.fillRect(x0, y0+(i)*cellSize, cellSize, cellSize);
        //         g.setColor(ca.color.isLight() ? Color.BLACK : Color.WHITE);
        //         char[] chars = Integer.toString(ca.amount).toCharArray();
        //         g.drawChars(chars, 0, chars.length, x0, y0+(i+1)*cellSize);
        //     }
        // }
        BufferedImage image = PixelColorCounter.stringToImage("resources/middle/hit1_temporary.png");
        int x0 = (bx + hw);
        int y0 = (by + hh);
        for(int i=0;i<image.getHeight();i++) {
            for(int j=0;j<image.getWidth();j++) {
                g.setColor(new Color(image.getRGB(j, i)));
                g.fillRect(x0+(j)*cellSize, y0+(i)*cellSize, cellSize, cellSize);
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void mouseMoved(MouseEvent e) {}

    @Override
    public void mouseDragged(MouseEvent e) 
    {
        if (dragging) {
        int newx = e.getX();
        int newy = e.getY();
        int dx = newx - mx;
        int dy = newy - my;
        bx += dx;
        by += dy;
        mx = newx;
        my = newy;
        repaint();  
        }

    }

    @Override
    public void mousePressed(MouseEvent e) {
        dragging = true;
        mx = e.getX();
        my = e.getY();

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        dragging = false;
    }
}  