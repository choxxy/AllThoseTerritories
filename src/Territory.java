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
import java.util.List;
import java.util.ArrayList;

public class Territory {
    private String name;
    private List<Patch> patches;
    private Point capital;
    private int army;
    private List<Territory> neighbors = null;
    private Player player = null;
    public boolean selected = false;

    private Label label = null;

    public Territory(String name) {
        this.name = name;
        this.patches = new ArrayList<Patch>();
        this.army = 0;
        this.neighbors = new ArrayList<Territory>();
    }

    public void addPatch(Patch p) {
        this.patches.add(p);
    }

    public List<Patch> getPatches() {
        return this.patches;
    }

    public void setCapital(int x, int y) {
        this.capital = new Point(x, y);
        this.label = new Label("0", x , y, Color.WHITE, Color.BLACK, 2);
        this.label.visible = true;
    }

    public Point getCapital() {
        return this.capital;
    }

    public void drawCapitalConnections(Graphics2D g) {
        if (this.neighbors != null) {
            for (Territory neighbor : this.neighbors) {
                Point c = neighbor.getCapital();
                g.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND,
                        BasicStroke.JOIN_ROUND));
                g.setColor(Color.lightGray);

                int x1 = (int) this.capital.getX();
                int y1 = (int) this.capital.getY();
                int x2 = (int) c.getX();
                int y2 = (int) c.getY();

                /* TODO: generalize */
                int xdiff = x1 - x2;;

                xdiff = xdiff < 0 ? -xdiff : xdiff;
                if (xdiff > 500) {

                    if (x2 > 500) {
                        g.drawLine(x1, y1, 0, y2);
                    } else {
                        g.drawLine(x1, y1, 1250, y2);
                    }
                    return;
                }

                g.drawLine(x1, y1, x2, y2);
            }
        }
    }

    public void draw(Graphics2D g) {
        Color background;
        Color border;

        if (this.player != null) {
            background = this.player.color;
        } else {
            background = new Color(109, 109, 109);
        }

        if (this.selected) {
            border = Color.magenta;
        } else {
            border = new Color(47, 47, 47);
        }


        for (Patch p : this.patches) {
            p.backgroundColor = background;
            p.borderColor = border;
            p.draw(g);
        }

        g.setColor(new Color(255, 255, 255));

        /*
        Font font = new Font("Serif", Font.PLAIN, 22);
        g.setFont(font);

        g.drawString("" + this.army, (int) this.capital.getX(), (int) this.capital.getY());
        */
    }

    public void drawCapital(Graphics2D g) {
        if (this.label != null) {
            this.label.draw(g);
        }
    }

    public boolean contains(Point point) {
        for (Patch patch : this.patches) {
            if (patch.contains(point)) {
                return true;
            }
        }
        return false;
    }

    public String getName() {
        return this.name;
    }

    public void addNeighbor(List<Territory> neighbors) {
        this.neighbors.addAll(neighbors);
    }

    public void addNeighbor(Territory t) {
        this.neighbors.add(t);
    }

    public void setClaimed(Player player) {
        this.player = player;
    }

    public boolean isClaimed() {
        if (this.player == null) {
            return false;
        }
        return true;
    }

    public boolean isClaimed(Player player) {
        if (this.player.equals(player)) {
            return true;
        }
        return false;
    }

    public Player getPlayer() {
        return this.player;
    }

    public int getArmy() {
        return this.army;
    }

    public void incrementArmy() {
        this.army++;
        this.label.setText("" + this.army);
    }
    public void decrementArmy() {
        if (this.army != 0) {
            this.army--;
            this.label.setText("" + this.army);
        }
    }

    public void addArmy(int n) {
        this.army += n;
        this.label.setText("" + this.army);
    }

    public Territory[] getNeighbors() {
        return this.neighbors.toArray(new Territory[0]);
    }
}
