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

public class Window extends JFrame {
    public Window(String filename, int numberOfPlayers) {
        super();

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.setTitle("AllThoseTerritories");

        Board board = new Board(filename, numberOfPlayers);

        this.add(board);
        this.pack();

        this.setSize(1250, 650);
    }
}
