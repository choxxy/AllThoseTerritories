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
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.JPanel;

public class Board extends JPanel implements MouseListener{
    private Map map;
    private Game game;
    private Territory selectedTerritory = null;
    private Territory destinationTerritory = null;
    private Label label;
    private int reinforcmentQuotient;

    Board(String map, int numberOfPlayers) {
        super();

        addMouseListener(this);

        setBackground(Color.WHITE);

        this.addMap(map);

        if (this.map.getTerritoriesCount() > 6) {
            this.reinforcmentQuotient = 3;
        } else {
            this.reinforcmentQuotient = 2;
        }

        System.out.println("Reinforcment Quotient: " + this.reinforcmentQuotient);

        this.label = new Label("End this round", 1100, 600, Color.BLACK, Color.LIGHT_GRAY, 10);

        this.startGame(numberOfPlayers);
    }

    public void startGame(int numberOfPlayers) {
        this.game = new Game(numberOfPlayers);
    }
    public void addMap(String filename) {
        this.map = new Map(filename);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        RenderingHints rh = new RenderingHints(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHints(rh);

        this.setBackground(new Color(81, 151, 214));

        for (Territory t : this.map.getTerritories()) {
            t.drawCapitalConnections(g2d);
        }

        this.drawTerritories(g2d);

        for (Territory t : this.map.getTerritories()) {
            if (t.selected == true) {
                t.draw(g2d);
                t.drawCapital(g2d);
            }
        }

        g2d.setColor(Color.BLACK);
        g2d.drawString(this.game.getCurrentPlayer().name + ": " + this.game.getPhaseName(), 20, 610);

        String action = this.game.action;
        int width = g2d.getFontMetrics().stringWidth(action);
        g2d.drawString(action, 1250/2 - width/2, 610);

        this.label.draw(g2d);
    }

    private void drawTerritories(Graphics2D g) {
        for (Territory t : this.map.getTerritories()) {
            t.draw(g);
        }

        for (Territory t : this.map.getTerritories()) {
            t.drawCapital(g);
        }
    }

    public void mousePressed(MouseEvent me) {
        Territory currentTerritory = null;
        Boolean buttonPressed = false;
        Boolean leftMouseButtonPressed = false;
        Boolean rightMouseButtonPressed = false;

        for (Territory t : this.map.getTerritories()) {
            if (t.contains(me.getPoint())) {
                currentTerritory = t;
            }
        }

        switch (me.getButton()) {
            case MouseEvent.BUTTON1:
                leftMouseButtonPressed = true;
                break;
            case MouseEvent.BUTTON3:
                rightMouseButtonPressed = true;
                break;
        }

        if (this.label.visible) {
            buttonPressed = this.label.contains(me.getPoint());
        }

        if (currentTerritory == null && buttonPressed == false) {
            return;
        }

        Player p = this.game.getCurrentPlayer();

        switch (this.game.getPhase()) {
            case 0:
                this.claimTerritoriesPhase(p, currentTerritory);
                break;
            case 1:
                this.reinforcePhase(p, currentTerritory, buttonPressed);
                break;
            case 2:
                this.attackPhase(p, currentTerritory, buttonPressed);
                break;
            case 3:
                this.freeMovePhase(p, currentTerritory, buttonPressed, leftMouseButtonPressed, rightMouseButtonPressed);
                break;
            default:
                break;
        }
        this.repaint();
    }

    public void mouseClicked(MouseEvent me) {}
    public void mouseReleased(MouseEvent me) {}
    public void mouseEntered(MouseEvent me) {}
    public void mouseExited(MouseEvent me) {}

    public void calculateReinforcement() {
        Territory[] territories =  this.map.getTerritories();

        for (Player p : this.game.getPlayer()) {
            List<Territory> playerTerritories = new ArrayList<Territory>();

            for (Territory t : territories) {
                if (t.isClaimed(p)) {
                    playerTerritories.add(t);
                }
            }

            int allBonus = 0;

            for (Continent c : this.map.getContinents()) {
                int bonus = c.bonus;
                for (Territory t : c.territories) {
                    if (! t.isClaimed(p)) {
                        bonus = 0;
                        break;
                    }
                }
                allBonus += bonus;
            }

            System.out.println(p.name + ": Territories: " + playerTerritories.size() + " (Bonus: " + allBonus + ")");

            p.addReinforcement((playerTerritories.size() / this.reinforcmentQuotient) + allBonus);
        }

        System.out.println(this.game.getPlayerById(0).name + ": " + "Reinforcements: " + this.game.getPlayerById(0).getReinforcement());
        System.out.println(this.game.getPlayerById(1).name + ": " + "Reinforcements: " + this.game.getPlayerById(1).getReinforcement());

        int reinforcement = this.game.getCurrentPlayer().getReinforcement();

        this.game.action =  reinforcement + " reinforcements";
    }

    /* returns true if territory conquered */
    public boolean attack(Territory attacker, Territory defender) {
        Player p1 = attacker.getPlayer();
        Player p2 = defender.getPlayer();

        if (attacker.getArmy() <= 1) {
            System.out.println(attacker.getName() + ": too view armies");
            return false;
        }

        int attackerHighestRand = 0;
        int attackerSecondHighestRand = 0;
        int defenderHighestRand = 0;
        int defenderSecondHighestRand = 0;
        int attackerDiceCount = 1;
        int defenderDiceCount = 1;

        if (attacker.getArmy() >= 4) {
            attackerDiceCount = 3;
        } else if (attacker.getArmy() == 3) {
            attackerDiceCount = 2;
        }

        int rand = 0;

        for (int i = 0; i < attackerDiceCount; i++) {
            rand = Util.randomInt(1, 6);
            if (rand > attackerHighestRand) {
                attackerSecondHighestRand = attackerHighestRand;
                attackerHighestRand = rand;
            } else if (rand > attackerSecondHighestRand) {
                attackerSecondHighestRand = rand;
            }
        }

        if (defender.getArmy() >= 2) {
            defenderDiceCount = 2;
        }

        for (int i = 0; i < defenderDiceCount; i++) {
            rand = Util.randomInt(1, 6);
            if (rand > defenderHighestRand) {
                defenderSecondHighestRand = defenderHighestRand;
                defenderHighestRand = rand;
            } else if (rand > defenderSecondHighestRand){
                defenderSecondHighestRand = rand;
            }
        }

        System.out.println("Attacker (" + attacker.getName() + "): dice throws: " + attackerDiceCount + ", highest dice: " + attackerHighestRand + ", second highest dice: " + attackerSecondHighestRand);
        System.out.println("Defender (" + defender.getName() +"): dice throws: " + defenderDiceCount + ", highest dice: " + defenderHighestRand + ", second highest dice: " + defenderSecondHighestRand);

        if (attackerHighestRand > defenderHighestRand) {
            defender.decrementArmy();
            System.out.println("Attacker defeats one army");
        } else {
            attacker.decrementArmy();
            System.out.println("Defender defeats on army");

            if (attacker.getArmy() > 1) {
                // System.out.println("Second dice");
                if (defenderDiceCount >= 2 && attackerDiceCount >= 2) {
                    if (attackerSecondHighestRand > defenderSecondHighestRand) {
                        defender.decrementArmy();
                        System.out.println("Attacker defeats second army");
                        return true;
                    } else {
                        attacker.decrementArmy();
                        System.out.println("Defender defeats second army");
                    }
                }
            }
        }

        if (defender.getArmy() == 0) {
            defender.setClaimed(attacker.getPlayer());
            defender.addArmy(attackerDiceCount);
            attacker.addArmy(-attackerDiceCount);
            System.out.println("Attacker defeats Defender");
            return true;
        }

        return false;
    }

    public void claimTerritoriesPhase(Player p, Territory currentTerritory) {
        if (this.map.claimTerritory(currentTerritory, p)) {
            this.game.action = "Claimed Territory " + currentTerritory.getName();
        } else {
            return;
        }

        Player comp = this.game.getComputer();

        if (comp != null) {
            Territory[] freeTerritories = this.map.getFreeTerritories();

            if (freeTerritories != null && freeTerritories.length >= 1) {
                int r = Util.randomInt(0, freeTerritories.length - 1);

                this.map.claimTerritory(freeTerritories[r], comp);

                System.out.println("Computer claimed territory " + freeTerritories[r].getName());
            }
        } else {
            this.game.nextPlayer();
        }

        // last territory claimed
        if (this.map.freeTerritoriesCount == 0) {
            this.game.setPhase(1);
            this.label.visible = true;
            this.calculateReinforcement();
        }
    }

    public void reinforcePhase(Player p, Territory currentTerritory, boolean buttonPressed) {
        if (p.getReinforcement() == 0 && currentTerritory != null) {
            return;
        }

        if (p.getReinforcement() == 0 || buttonPressed) {
            Player comp = this.game.getComputer();

            if (comp != null) {
                int r, i, max;

                Territory[] claimedTerritories = this.map.getClaimedTerritories(comp);

                int compReinforcementCount = comp.getReinforcement();

                while (compReinforcementCount > 0) {
                    if (compReinforcementCount >= 2) {
                        max = 2;
                    } else {
                        max = 1;
                    }
                    r = Util.randomInt(1, max);
                    i = Util.randomInt(1, claimedTerritories.length - 1);
                    claimedTerritories[i].addArmy(r);
                    compReinforcementCount -= r;
                    comp.setReinforcement(compReinforcementCount);
                    System.out.println(comp.name + " placed " + r + " reinforcement" + (r > 1 ? "s" : "") + " in " + claimedTerritories[i].getName());
                }
                this.game.setPhase(2);
                this.game.action = "";
            } else {
                Player nx = this.game.nextPlayer();

                this.game.reinforcePhaseRounds++;

                if (this.game.reinforcePhaseRounds == this.game.getNumberOfPlayers()) {
                    this.game.reinforcePhaseRounds = 0;
                    this.game.setPhase(2);
                    this.game.action = "";
                } else {
                    int reinforcement = this.game.getCurrentPlayer().getReinforcement();

                    this.game.action =  reinforcement + " reinforcements";
                }
            }
        } else if (currentTerritory.isClaimed(p)) {
            p.reinforceTerritory(currentTerritory);

            this.game.action = p.getReinforcement() + " reinforcements, placed reinforcement in " + currentTerritory.getName();
        }
    }

    public void attackPhase(Player p, Territory currentTerritory, boolean buttonPressed) {

        if (buttonPressed) {
            if (this.selectedTerritory != null) {
                this.selectedTerritory.selected = false;
                this.selectedTerritory = null;
            }

            Player comp = this.game.getComputer();

            if (comp != null) {
                this.computerAttack();

                if (isGameOver()) {
                    this.game.setPhase(4);
                } else {
                    this.game.setPhase(3);
                    this.game.action = "";
                }
            } else {
                this.game.attackPhaseRounds++;

                if (this.game.attackPhaseRounds == this.game.getNumberOfPlayers()) {
                    this.game.attackPhaseRounds = 0;
                    this.game.setPhase(3);
                    this.game.action = "";
                }

                this.game.nextPlayer();
            }
            return;
        }

        if (currentTerritory != null) {
            if (currentTerritory.isClaimed(p)) {
                if (this.selectedTerritory != null) {
                    this.selectedTerritory.selected = false;
                }
                this.selectedTerritory = currentTerritory;
                this.selectedTerritory.selected = true;
                this.game.action= "Selected " + this.selectedTerritory.getName();
                return;
            } else {
                if (this.selectedTerritory != null) {
                    if (this.selectedTerritory.getArmy() > 1) {
                        Territory[] neighbors = this.selectedTerritory.getNeighbors();

                        for (Territory t: neighbors) {
                            if (currentTerritory.equals(t)) {
                                this.game.action = "Attack " + t.getName() + " from " + this.selectedTerritory.getName();
                                if (this.attack(this.selectedTerritory, t)) {
                                    this.game.action = this.selectedTerritory.getName() + " defeated one army from " + t.getName();
                                } else {
                                    this.game.action = t.getName() + " defeated one army from " + this.selectedTerritory.getName();
                                }
                                if (isGameOver()) {
                                    this.game.setPhase(4);
                                    return;
                                }
                                break;
                            }
                        }
                    } else {
                        this.game.action = "too few armies";
                    }
                }
            }
        }
    }

    public void freeMovePhase(Player p, Territory currentTerritory, boolean buttonPressed, boolean leftMouseButtonPressed, boolean rightMouseButtonPressed) {
        if (buttonPressed) {
            if (this.selectedTerritory != null) {
                this.selectedTerritory.selected = false;
                this.selectedTerritory = null;
                this.destinationTerritory = null;
            }

            Player comp = this.game.getComputer();

            if (comp != null) {
                this.game.setPhase(1);
                this.calculateReinforcement();
                this.label.visible = true;
            } else {
                this.game.moveRounds++;

                if (this.game.moveRounds == this.game.getNumberOfPlayers()) {
                    this.game.moveRounds = 0;
                    this.game.setPhase(1);
                    this.game.nextPlayer();
                    this.calculateReinforcement();
                    this.label.visible = true;
                } else {
                    this.game.nextPlayer();
                }
            }
            return;
        }

        if (!currentTerritory.isClaimed(p)) {
            return;
        }

        if (leftMouseButtonPressed) {
            if (this.destinationTerritory != null) {
                if (this.destinationTerritory == currentTerritory) {
                    this.destinationTerritory = this.selectedTerritory;
                } else {
                    return;
                }
            }

            if (this.selectedTerritory != null) {
                this.selectedTerritory.selected = false;
            }

            this.selectedTerritory = currentTerritory;
            this.selectedTerritory.selected = true;
            this.game.action = "Selected " + this.selectedTerritory.getName() + ", right click to move army";
        } else if (rightMouseButtonPressed && this.selectedTerritory != null) {
            if (this.destinationTerritory != null) {
                if (this.destinationTerritory != currentTerritory) {
                    return;
                }
                if (this.selectedTerritory.getArmy() <= 1) {
                    this.game.action = "too few armies in " + currentTerritory.getName();
                    return;
                }
                this.game.action = "Move one army from " + this.selectedTerritory.getName()  + " to " + this.destinationTerritory.getName();
                this.destinationTerritory.incrementArmy();
                this.selectedTerritory.decrementArmy();
                return;
            }
            Territory[] neighbors = this.selectedTerritory.getNeighbors();

            for (Territory t: neighbors) {
                if (currentTerritory.equals(t) && t.isClaimed(p)) {
                    if (this.selectedTerritory.getArmy() <= 1) {
                        this.game.action = "too few armies in " + currentTerritory.getName();
                        return;
                    }
                    this.destinationTerritory = currentTerritory;
                    this.game.action = "Move one army from " + this.selectedTerritory.getName()  + " to " + t.getName();
                    currentTerritory.incrementArmy();
                    this.selectedTerritory.decrementArmy();
                    break;
                }
            }
        }
    }

    public void computerAttack() {
        System.out.println("Computer attacks");

        Player comp = this.game.getComputer();

        if (comp == null) {
            System.out.println("assertion: no computer");
            return;
        }

        Territory[] territories =  this.map.getTerritories();

        List<Territory> playerTerritories = new ArrayList<Territory>();

        for (Territory t : territories) {
            if (t.isClaimed(comp) && t.getArmy() > 1) {
                playerTerritories.add(t);
            }
        }

        if (playerTerritories.size() == 0) {
            return;
        }

        int attackCount = 1;

        if (playerTerritories.size() >= 3) {
            attackCount = Util.randomInt(1, 3);
        }

        for (Territory t : playerTerritories) {
            if (t.getArmy() > 1) {
                for (Territory n : t.getNeighbors()) {
                    if (!n.isClaimed(comp)) {
                        this.attack(t, n);
                        attackCount--;
                        if (t.getArmy() <= 1) {
                            break;
                        }
                    }

                    if (attackCount == 0) {
                        break;
                    }
                }
            }
        }
    }

    public boolean isGameOver() {
        Territory[] territories =  this.map.getTerritories();
        Player p = this.game.getPlayerById(0);

        int count = 0;

        for (Territory t : territories) {
            if (t.isClaimed(p)) {
                count++;
            }
        }

        if (count == 0) {
            this.game.action = this.game.getPlayerById(1).name + " won";

            if (this.selectedTerritory != null) {
                this.selectedTerritory.selected = false;
                this.selectedTerritory = null;
            }
            this.label.visible = false;
        } else if (count == territories.length) {
            this.game.action = this.game.getPlayerById(0).name + " won";

            if (this.selectedTerritory != null) {
                this.selectedTerritory.selected = false;
                this.selectedTerritory = null;
            }
            this.label.visible = false;
        } else {
            return false;
        }

        return true;
    }
}
