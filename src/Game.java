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
import java.util.Random;

public class Game {
    private String phases[];
    private int phase = 0;
    private Player[] player;
    private int currentPlayer;
    public String action = "";
    public int attackPhaseRounds = 0;
    public int reinforcePhaseRounds = 0;
    public int moveRounds = 0;
    private int numberOfPlayers;

    public Game(int numberOfPlayers) {
        this.player = new Player[2];
        this.player[0] = new Player(Color.BLUE);
        this.player[1] = new Player(Color.RED);

        this.currentPlayer = 0;

        if (numberOfPlayers == 1) {
            this.player[0].name = "Player";
            this.player[1].setComputer(true);
            this.player[1].name = "Computer";
            System.out.println("Mode: Singleplayer");
        } else {
            this.player[0].name = "Player 1";
            this.player[1].name = "Player 2";
            System.out.println("Mode: Multiplayer");
        }

        this.numberOfPlayers = numberOfPlayers;

        this.phases = new String[]{"Claim territories", "Reinforce territories", "Attack territories", "Free move", "Game over"};

        this.setPhase(0);
    }

    public int getNumberOfPlayers() {
        return this.numberOfPlayers;
    }

    public Player getComputer() {
        if (this.getNumberOfPlayers() == 1 && this.player[1].isComputer()) {
            return this.player[1];
        } else {
            return null;
        }
    }

    public int getPhase() {
        return this.phase;
    }

    public String getPhaseName() {
        return this.phases[this.phase];
    }

    public void setPhase(int p) {
        this.phase = p;
        System.out.println("Phase: " + this.phases[p]);
    }

    public Player getCurrentPlayer() {
        return this.player[this.currentPlayer];
    }

    public Player getPlayerById(int id) {
        return this.player[id];
    }

    public void setCurrentPlayerById(int id) {
        this.currentPlayer = id;
    }

    public Player nextPlayer() {
        if (this.numberOfPlayers > 1) {
            if (this.currentPlayer < (this.player.length - 1)) {
                this.currentPlayer++;
            } else {
                this.currentPlayer = 0;
            }
            return this.player[this.currentPlayer];
        } else {
            return null;
        }
    }

    public Player[] getPlayer() {
        return this.player;
    }

    public boolean reinforcementsLeft() {
        int reinforcements = 0;

        for (Player p : this.player) {
            reinforcements += p.getReinforcement();
        }

        if (reinforcements > 0) {
            return true;
        } else {
            return false;
        }
    }
}
