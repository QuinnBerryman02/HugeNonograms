package src.main;

import javax.swing.*;

import src.main.Nonogram.ColorAmount;
import src.main.Nonogram.Hints;
import java.awt.image.BufferedImage;
import java.nio.Buffer;
import java.time.Instant;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.List;

public class Window extends JFrame implements MouseListener, MouseMotionListener, MouseWheelListener {  
    Nonogram nonogram;
    int bx=50,by=50;
    int mx,my;
    float scale = 1;
    int cellSize = 10;
    boolean dragging = false;
    boolean once = false;

    public Window(Nonogram nono) {
        super("Nonogram Program");
        nonogram = nono;
        bx = ((int)(-nonogram.hintW * cellSize * scale) + bx);
        by = ((int)(-nonogram.hintH * cellSize * scale) + by);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000,1000);
        setLayout(null);
        setVisible(true);
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.setFont(new Font("my font",Font.ITALIC,cellSize));
        int w = (int)(nonogram.W * cellSize * scale);
        int h = (int)(nonogram.H * cellSize * scale);
        int hw = (int)(nonogram.hintW * cellSize * scale);
        int hh = (int)(nonogram.hintH * cellSize * scale);
        g.drawRect(bx, by+hh, hw, h);
        g.drawRect(bx+hw, by, w, hh);
        g.drawRect(bx+hw, by+hh, w, h);
        Hints hint = nonogram.obscuredHints;
        BufferedImage lhi = nonogram.leftHintImage;
        BufferedImage thi = nonogram.topHintImage;
        BufferedImage image = nonogram.image; 
        g.drawImage(lhi, bx, by+hh, bx+hw, by+hh+h, 0, 0, lhi.getWidth(), lhi.getHeight(), Color.magenta, getContentPane());
        g.drawImage(thi, bx+hw, by, bx+hw+w, by+hh, 0, 0, thi.getWidth(), thi.getHeight(), Color.magenta, getContentPane());
        g.drawImage(image, bx+hw, by+hh, bx+hw+w, by+hh+h, 0, 0, image.getWidth(), image.getHeight(), Color.magenta, getContentPane());
        // int x0 = (bx + hw);
        // int y0 = (by + hh);
        // for(int i=0;i<image.getHeight();i++) {  //64x64 = 3600 ish
        //     for(int j=0;j<image.getWidth();j++) {
        //         g.setColor(new Color(image.getRGB(j, i)));
        //         g.fillRect(x0+(j)*cellSize, y0+(i)*cellSize, cellSize, cellSize);
        //     }
        // }
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

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if(e.getPreciseWheelRotation() < 0) scale*=1.05;
        if(e.getPreciseWheelRotation() > 0) scale*=0.95;
        repaint();
    }
}  