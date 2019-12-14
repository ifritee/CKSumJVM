import com.sun.java.swing.plaf.gtk.GTKLookAndFeel;
import com.sun.java.swing.plaf.motif.MotifLookAndFeel;
import com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel;
import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;

import javax.swing.*;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import javax.swing.plaf.synth.SynthLookAndFeel;
import java.awt.*;
import java.util.ArrayList;

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
