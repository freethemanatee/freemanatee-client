package me.zeroeightsix.kami;

import com.google.common.hash.Hashing;

import javax.swing.*;


public class Framer extends JFrame {
    public Framer() {
        this.setTitle("manatee client uuid check");
        this.setDefaultCloseOperation(2);
        this.setLocationRelativeTo(null);
        String message = "uuid check go brrrrrrrrrrrrrrrrrr" + "\n" ;
        JOptionPane.showMessageDialog(this, message, "free da manatee", -1, UIManager.getIcon("OptionPane.warningIcon"));
    }

}