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
import java.awt.*;

public class Player {
    private int reinforcement;
    private boolean computer;
    public Color color;
    public String action = "";
    public String name = "";
    private int id;

    public Player(Color color) {
        this.reinforcement = 0;
        this.computer = false;
        this.color = color;
    }

    public void reinforceTerritory(Territory t) {
        if (this.reinforcement != 0) {
            this.reinforcement -= 1;
            t.incrementArmy();
        }
    }

    public int getId() {
        return this.id;
    }

    public void setReinforcement(int n) {
        this.reinforcement = n;
    }

    public void addReinforcement(int n) {
        this.reinforcement += n;
    }

    public int getReinforcement() {
        return this.reinforcement;
    }

    public void setComputer(boolean b) {
        this.computer = b;
    }

    public boolean isComputer() {
        return this.computer;
    }
}
