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
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;

public class Label implements Sprite {
    private String text;
    private int x;
    private int y;
    private int width;
    private int height;
    private int padding;
    public boolean visible;
    private Polygon polygon;
    private Color backgroundColor;
    private Color fontColor;
    private int fontSize;
    private Font font;

    public Label(String text, int x, int y, Color fontColor, Color backgroundColor, int padding) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.width = 0;
        this.height = 0;
        this.visible = false;
        this.polygon = null;
        this.fontColor = fontColor;
        this.backgroundColor = backgroundColor;
        this.fontSize = 20;
        this.padding = padding;

        this.font = new Font("Serif", Font.PLAIN, this.fontSize);
    }

    public void draw(Graphics2D g) {
        if (! this.visible) {
            return;
        }

        FontRenderContext frc = g.getFontRenderContext();
        TextLayout layout = new TextLayout(this.text, font, frc);

        Rectangle2D bounds = layout.getBounds();
        this.width = (int) bounds.getWidth();
        this.height = (int) bounds.getHeight();

        this.setPolygon();

        /*
        this.x += bounds.getX();
        this.y += bounds.getY();
        */

        /*
        FontMetrics fontMetrics = g.getFontMetrics();
        this.width = fontMetrics.stringWidth(this.text) + 2 * this.padding;
        this.height = fontMetrics.getHeight();

        System.out.println("height: " + this.height);
        */

        g.setColor(this.backgroundColor);

        /*
        bounds.setRect(bounds.getX() + this.x,
                bounds.getY() + this.y,
                bounds.getWidth(),
                bounds.getHeight());
        g.draw(bounds);
        */

        g.fillRect((int)bounds.getX() + this.x - this.padding,
                (int) bounds.getY() + this.y - this.padding,
                (int) bounds.getWidth() + this.padding * 2,
                (int) bounds.getHeight() + this.padding * 2);

        g.setColor(this.fontColor);

        layout.draw(g, this.x, this.y);

        /*
        g.setColor(this.backgroundColor);
        g.fillPolygon(this.polygon);
        g.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND));
        g.setColor(this.borderColor);
        g.drawPolygon(this.polygon);
        */

        /*
        g.setColor(Color.DARK_GRAY);
        Font font = new Font("Serif", Font.PLAIN, this.fontSize);
        g.setFont(font);

        g.drawString(this.text, this.x + this.padding, this.y + this.height);
        */
    }

    private void setPolygon() {
        if (this.polygon == null) {
            int[] xPoints = new int[]{this.x - this.padding, this.x - this.padding,               this.x + this.width + this.padding,  this.x + this.width + this.padding};
            int[] yPoints = new int[]{this.y + this.padding, this.y - this.height - this.padding, this.y - this.height - this.padding, this.y + this.padding};
            this.polygon = new Polygon (xPoints, yPoints, xPoints.length);
        }
    }

    public boolean contains(Point p) {
        if (this.polygon == null) {
            return false;
        }
        return this.polygon.contains(p);
    }

    public void setText(String text) {
        this.text = text;
    }
}
