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
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Map {
    private String filename;

    private HashMap<String, Territory> territories = new HashMap<String, Territory>();
    private HashMap<String, Continent> continents = new HashMap<String, Continent>();

    public int freeTerritoriesCount;
    private int territoriesCount;

    public Map(String filename) {
        System.out.println("Map: " + filename);

        this.filename = filename;
        this.territories = new LinkedHashMap<String, Territory>();
        this.continents = new LinkedHashMap<String, Continent>();
        this.territoriesCount = this.freeTerritoriesCount = 0;

        parseMap();

        this.freeTerritoriesCount = this.territoriesCount;
        System.out.println("Total number of territories: " + this.territoriesCount);
    }

    private void parseMap() {
        BufferedReader reader;
        String line;
        String[] fields;
        String type;
        String name;

        try {
            reader = new BufferedReader(new FileReader(this.filename));
        } catch (FileNotFoundException ex) {
            System.err.println("Error: Map file \"" + this.filename + "\"  could not be opened.");
            return;
        }

        try {
            while ((line = reader.readLine()) != null) {
                if (line.equals("")) {
                    continue;
                }
                Scanner scanner =  new Scanner(line);
                scanner.useDelimiter(" ");

                type = scanner.next();

                name = "";
                while (scanner.hasNext() && scanner.hasNextInt() == false) {
                    String s = scanner.next();
                    if (s.equals(":")) {
                        break;
                    }
                    if (name.equals("")) {
                        name = s;
                    } else {
                        name += " " + s;
                    }
                }

                if (type.equals("patch-of")) {

                    List<Integer> coordinates = new ArrayList<Integer>();

                    while (scanner.hasNextInt()) {
                        coordinates.add(scanner.nextInt());
                    }

                    if ((coordinates.size() % 2) != 0) {
                        System.err.println("Error in patch-of: Wrong number of coordinates.");
                        return;
                    }

                    int[] xPoints = new int[coordinates.size() / 2];
                    int[] yPoints = new int[coordinates.size() / 2];

                    int n = 0;
                    for (int i = 0; i < coordinates.size(); i += 2) {
                        xPoints[n] = coordinates.get(i);
                        yPoints[n] = coordinates.get(i+1);
                        n++;
                    }

                    this.addPatchToTerritory(name, xPoints, yPoints);
                } else if (type.equals("capital-of")) {
                    try {
                        this.addCapitalToTerritory(name, scanner.nextInt(), scanner.nextInt());
                    } catch (Exception e) {
                        System.err.println("Error in capital-of: Coordinates could not be read.");
                    }
                } else if (type.equals("neighbors-of")) {
                    List<Territory> neighbors = new ArrayList<Territory>();

                    scanner.useDelimiter(" - ");

                    while (scanner.hasNext()) {
                        String s = scanner.next();
                        s = s.trim();
                        if (this.territories.containsKey(s)) {
                            Territory t = this.territories.get(s);
                            neighbors.add(t);
                            t.addNeighbor(this.territories.get(name));
                        } else {
                            System.err.println("Error in neighbors-of: neighbor does not exist: " + s);
                        }
                    }
                    this.addNeighborsToTerritory(name, neighbors);
                } else if (type.equals("continent")) {
                    scanner.useDelimiter(" ");
                    int bonus;

                    if (scanner.hasNextInt()) {
                        bonus = scanner.nextInt();
                    } else {
                        System.err.println("Error in continent: Wrong format.");
                        continue;
                    }

                    if (!(scanner.hasNext() && scanner.next().equals(":"))) {
                        System.err.println("Error in continent: Wrong delimiter.");
                        continue;
                    }

                    scanner.useDelimiter(" - ");

                    List<Territory> t = new ArrayList<Territory>();

                    while (scanner.hasNext()) {
                        String s = scanner.next();
                        s = s.trim();
                        if (this.territories.containsKey(s)) {
                            t.add(this.territories.get(s));
                        } else {
                            System.err.println("Error in continent: Wrong format: " + s);
                        }
                    }

                    this.continents.put(name, new Continent(name, bonus, t));

                }
            }
        } catch(IOException ex) {
            System.err.println("Error: Line could not be read.");
            return;
        } finally {
            try {
                reader.close();
            } catch (IOException ex) {
                System.err.println("Error in close()");
                return;
            }
        }
    }

    public void addPatchToTerritory(String name, int[] xPoints, int[] yPoints) {
        Territory t;
        Patch p = new Patch(name, xPoints, yPoints);

        if (this.territories.containsKey(name)) {
            t = this.territories.get(name);
            t.addPatch(p);
        } else {
            t = new Territory(name);
            t.addPatch(p);
            this.territoriesCount++;
            this.territories.put(name, t);
        }
    }

    public boolean addCapitalToTerritory(String name, int x, int y) {
        Territory t;

        if (this.territories.containsKey(name)) {
            t = this.territories.get(name);
            t.setCapital(x, y);
            return true;
        } else {
            return false;
        }
    }

    public boolean addNeighborsToTerritory(String name, List<Territory> neighbors) {
        Territory t;

        if (this.territories.containsKey(name)) {
            t = this.territories.get(name);
            t.addNeighbor(neighbors);
            return true;
        } else {
            return false;
        }
    }

    public Territory[] getTerritories() {
        return this.territories.values().toArray(new Territory[0]);
    }

    public Continent[] getContinents() {
        return this.continents.values().toArray(new Continent[0]);
    }

    public Territory[] getFreeTerritories() {
        List <Territory> freeTerritories = new ArrayList<Territory>();

        for(Territory t : this.territories.values()) {
            if (!t.isClaimed()) {
                freeTerritories.add(t);
            }
        }
        return freeTerritories.toArray(new Territory[0]);
    }

    public boolean claimTerritory(Territory territory, Player player) {
        if (territory.isClaimed()) {
            return false;
        }

        territory.setClaimed(player);
        territory.incrementArmy();
        this.freeTerritoriesCount--;

        return true;
    }

    public Territory[] getClaimedTerritories(Player player) {
        List<Territory> claimedTerritories = new ArrayList<Territory>();

        for(Territory t : this.territories.values()) {
            if (t.isClaimed(player)) {
                claimedTerritories.add(t);
            }
        }

        return claimedTerritories.toArray(new Territory[0]);
    }

    public int getTerritoriesCount() {
        return this.territoriesCount;
    }
}
