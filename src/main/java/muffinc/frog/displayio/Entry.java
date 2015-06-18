package muffinc.frog.displayio;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
public class Entry {
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                PlatFrame frame = new PlatFrame();
                frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                frame.setVisible(true);
            }
        });
    }

}


class PlatFrame extends JFrame {
    private JPanel buttonPanel;

    public PlatFrame() {

        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension dimension = kit.getScreenSize();
        int screenHeight = dimension.height;
        int screenWidth = dimension.width;

        setTitle("FROG Test");
        setSize(screenWidth / 2, screenHeight / 2);

        buttonPanel = new JPanel();

        UIManager.LookAndFeelInfo[] infos = UIManager.getInstalledLookAndFeels();
        for (UIManager.LookAndFeelInfo info : infos) {
            makeButton(info.getName(), info.getClassName());
        }

        add(buttonPanel);

    }


    void makeButton(String name, final String plafName) {
        JButton button = new JButton(name);
        buttonPanel.add(button);

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    UIManager.setLookAndFeel(plafName);
                    SwingUtilities.updateComponentTreeUI(PlatFrame.this);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    class ColorAction extends AbstractAction {
        public ColorAction(String name, Icon icon, Color c) {
            putValue(Action.NAME, name);
            putValue(Action.SMALL_ICON, icon);
            putValue("color", c);
            putValue(Action.SHORT_DESCRIPTION, "Set Panel Color to" + name.toLowerCase());
        }


        @Override
        public void actionPerformed(ActionEvent e) {
            Color c = (Color) getValue("color");
            buttonPanel.setBackground(c);
        }
    }


}

