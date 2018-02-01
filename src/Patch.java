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

public class Patch implements Sprite {
    private String name;
    private Polygon polygon;
    public Color backgroundColor;
    public Color borderColor;

    public Patch(String name, int[] xPoints, int[] yPoints, Color backgroundColor, Color borderColor) {
        this.name = name;
        this.polygon = new Polygon (xPoints, yPoints, xPoints.length);
        this.backgroundColor = backgroundColor;
        this.borderColor = borderColor;
    }

    public Patch(String name, int[] xPoints, int[] yPoints) {
        this(name, xPoints, yPoints, new Color(255,255,255,255), new Color(255,255,255,255));
    }

    public void draw(Graphics2D g) {
        g.setColor(this.backgroundColor);
        g.fillPolygon(this.polygon);
        g.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND));
        g.setColor(this.borderColor);
        g.drawPolygon(this.polygon);
    }

    public String getName() {
        return this.name;
    }

    public boolean contains(Point p) {
        return this.polygon.contains(p);
    }
}
