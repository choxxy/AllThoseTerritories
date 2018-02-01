/**
 * AllThoseTerritories - A strategy game similar to the board game Risk
 * Copyright (C) 2016 Lukas Zronek
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class OptionDialog extends JFrame implements WindowListener,ActionListener {
    private JComboBox<Integer> choosePlayersComboBox;
    private JComboBox<String> chooseMapComboBox;

    private JButton startButton;
    private JButton quitButton;

    private HashMap<String, String> maps;

    private final String DATA_PATH = "data";

    public OptionDialog() {
        super();

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.setTitle("AllThoseTerritories");

        JPanel panel = new JPanel();

        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        panel.setLayout(new GridLayout(3, 2));

        panel.add(new JLabel("Number of players:"));

        final Integer modes[] = {1, 2};
        this.choosePlayersComboBox = new JComboBox(modes);
        panel.add(this.choosePlayersComboBox);

        panel.add(new JLabel("Map:"));

        this.maps = this.getMaps(this.DATA_PATH);

        if (this.maps == null) {
            System.err.print("Could not open maps directory.");
            this.quit();
        } else if (this.maps.isEmpty()) {
            System.err.print("No maps found.");
            this.quit();
        }

        this.chooseMapComboBox = new JComboBox(maps.keySet().toArray());
        panel.add(this.chooseMapComboBox);

        this.startButton = new JButton("Start Game");
        this.startButton.addActionListener(this);
        panel.add(this.startButton);

        this.quitButton = new JButton("Quit");
        this.quitButton.addActionListener(this);
        panel.add(this.quitButton);

        this.add(panel);

        this.pack();
    }

    private HashMap<String, String> getMaps(String path) {
        File dir = new File(path);
        String[] files = dir.list();

        if (files == null) {
            return null;
        }

        HashMap<String, String> maps = new LinkedHashMap<>();

        for (int i = 0; i < files.length; i++) {
            String[] parts = files[i].split("\\.");

            if (parts.length == 2 && parts[1].equals("map")) {
                maps.put(parts[0], new File(dir, files[i]).getAbsolutePath().toString());
            }
        }

        return maps;
    }

    private void quit() {
        this.dispose();
        System.exit(0);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == this.quitButton) {
            this.quit();
        } else if (source == this.startButton) {
            Integer numberOfPlayers = (Integer)this.choosePlayersComboBox.getSelectedItem();
            String filename = this.maps.get(this.chooseMapComboBox.getSelectedItem());

            this.setVisible(false);
            new Window(filename, numberOfPlayers.intValue()).setVisible(true);
            this.dispose();
        }
    }

    @Override
    public void windowOpened(WindowEvent e) {
    }

    @Override
    public void windowClosing(WindowEvent e) {
    }

    @Override
    public void windowClosed(WindowEvent e) {
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }
}
