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
import java.util.List;

public class Continent {
    private String name;
    public List<Territory> territories;
    public int bonus;

    public Continent(String name, int bonus, List<Territory> territories) {
        this.name = name;
        this.bonus = bonus;
        this.territories = territories;
    }
}
