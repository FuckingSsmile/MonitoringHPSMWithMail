package ru.hpsm.email.view;

import ru.hpsm.email.Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainWindow {
    int i = 0;

    public MainWindow() {
        Settings settings = Settings.getInstance();
//        if (settings.getBoolean(Settings.KeysBoolean.FIRST_LAUNCH)) {
//            LoginDialog loginDialog = new LoginDialog();
//            loginDialog.show();
//        }


        JFrame frame = new JFrame("HPSM");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setResizable(false);
        Dimension tabSize = new Dimension(600, 600);
        JPanel panel1 = new SettingsPage1(tabSize);
        JPanel panel2 = new JPanel(null);
        JPanel panel3 = new JPanel(null);


        panel2.setPreferredSize(tabSize);
        panel3.setPreferredSize(tabSize);

        panel2.setBackground(Color.green);
        panel3.setBackground(Color.blue);


        Font font = new Font("Verdana", Font.PLAIN, 34);
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(font);

        JPanel content = new JPanel();
        content.setPreferredSize(tabSize);
        content.setLayout(new BorderLayout());

        tabbedPane.addTab("красный ", panel1);
        tabbedPane.addTab("зелёный ", panel2);
        tabbedPane.addTab("синий ", panel3);


        content.add(tabbedPane, BorderLayout.CENTER);

        frame.add(content);


        frame.pack();
        frame.setVisible(true);


    }
}
