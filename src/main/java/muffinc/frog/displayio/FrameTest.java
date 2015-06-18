package muffinc.frog.displayio;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * FROG, a Face Recognition Gallery in Java
 * Copyright (C) 2015 Jun Zhou
 * <p/>
 * This file is part of FROG.
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 * zj45499 (at) gmail (dot) com
 */
public class FrameTest {
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
                public void run() {
                FrameT ft = new FrameT();
                ft.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                ft.setVisible(true);

            }
        });
    }
}

class FrameT extends JFrame {
    public FrameT(){
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension dimension = kit.getScreenSize();
        int screenHeight = dimension.height;
        int screenWidth = dimension.width;

        setSize(screenWidth / 2, screenHeight / 2);
        setLocationByPlatform(true);

//        Image img = kit.getImage("/Users/Meth/Dropbox/Thesis/FROG/src/test/resources/test/00002fb010_940422.tif");
//        setIconImage(img);
        setTitle("吃了么");

        drawable draw = new drawable();
//        draw.setBackground(SystemColor.activeCaption);
        add(draw);


    }
}

class drawable extends JComponent {
    @Override
    protected void paintComponent(Graphics g) {
        g.drawString("吃了么3", MESSAGE_X, MESSAGE_Y);

        Graphics2D graphic = ((Graphics2D) g);

        Rectangle2D rect = new Rectangle2D.Double();
        rect.setFrameFromDiagonal(10,10,80,80);
        graphic.draw(rect);
        graphic.setPaint(SystemColor.activeCaption);
        graphic.fill(rect);
    }

    public static final int MESSAGE_X = 75;
    public static final int MESSAGE_Y = 150;
}
