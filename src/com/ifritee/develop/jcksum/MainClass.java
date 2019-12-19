package com.ifritee.develop.jcksum;

import javax.swing.*;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

public class MainClass {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel( new NimbusLookAndFeel());
        } catch( Exception ex ) {
            System.err.println( "Failed to initialize LaF" );
        }

        JFrame rootFrame = new JFrame();
        rootFrame.setContentPane(new RootWindow().getRootPanel());
        // Если нужно, то добавляй еще
        rootFrame.setIconImage(new ImageIcon("res/folder_32.png").getImage());
        rootFrame.setTitle("Подмена J");
        rootFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        rootFrame.pack();
        rootFrame.setVisible(true);
    }
}
