package ru.hpsm.email.view;

import ru.hpsm.email.Settings;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SettingsPage1 extends JPanel {


    public SettingsPage1(Dimension tabSize) {
        setLayout(null);
        setPreferredSize(tabSize);

        JLabel jLabel = new JLabel("---");
        jLabel.setBounds(100, 100, 100, 40);

        add(jLabel);


        Settings settings = Settings.getInstance();

        Boolean aBoolean = settings.getBoolean(Settings.KeysBoolean.FIRST_LAUNCH);

        jLabel.setText(aBoolean.toString());

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double width = screenSize.getWidth();
        double height = screenSize.getHeight();

        System.out.println(width);
        System.out.println(height);

        JCheckBox jCheckBox = new JCheckBox("first launch");
        jCheckBox.setBounds(100, 200, 100, 40);
        jCheckBox.setSelected(aBoolean);
        add(jCheckBox);

        jCheckBox.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                boolean selected = jCheckBox.isSelected();

                settings.setObject(Settings.KeysBoolean.FIRST_LAUNCH, selected);
            }
        });


        JButton button = new JButton("save");
        button.setBounds(100, 300, 100, 40);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                settings.save();
            }
        });
        add(button);



    }
}
