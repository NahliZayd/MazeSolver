package zn.mazesolver.entity.entitys;

import zn.mazesolver.Game;
import zn.mazesolver.entity.Entity;
import zn.mazesolver.ui.MainMenu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;

public class Monster extends Entity {

    int size=0;
    public Monster(int row, int col, int health, int damage,Color color) {
        super(row, col, health, damage,color);
        this.addTarget(Player.class);

    }

    public int getSize() {
       if(size==0) {
           int w = world.totalWidth /  world.numColumns;
           int h =  world.totalHeight /  world.numRows;
           int monsterSize = Math.min(w, h) / 2;
           return monsterSize;
       }
         else {
              return size;
    }
         }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public void draw(Graphics g) {
       super.draw(g);
        int w = world.totalWidth /  world.numColumns;
        int h =  world.totalHeight /  world.numRows;
        g.setColor(getColor());
        g.fillRect(world.left + getCol() * w + w / 4, world.top + getRow() * h + h / 4, getSize(), getSize());
    }

    @Override
    protected double getAttackRadius() {
        return 2;
    }

    @Override
    public void move() {
        new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                updateMonster();
                attack();
                world.repaint(); // Assurez-vous de redessiner le jeu après avoir mis à jour le monstre

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // Préserver l'état d'interruption
                }
            }
        }).start();
    }

    @Override
    public void performAttackAnimation(Graphics2D g, Entity e) {
        new Thread(() -> {

            int w = world.totalWidth /  world.numColumns;
            int h =  world.totalHeight /  world.numRows;
            int originalSize = Math.min(w, h) / 2;
            int enlargedSize = originalSize*2;
            // Enlarge the monster
            g.setColor(getColor());
            g.fillRect(getCol() * enlargedSize, getRow() * enlargedSize, enlargedSize, enlargedSize);
            MainMenu.maze.repaint();
            // Sleep for 100 milliseconds
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            MainMenu.maze.repaint();
            // Return the monster to its original size
            g.setColor(getColor());
            g.fillRect(getCol() * originalSize, getRow() * originalSize, originalSize, originalSize);
        }).start();
    }

    public synchronized void updateMonster() {
        if (MainMenu.maze.getPlayer() != null) {
            int playerRow = MainMenu.maze.getPlayer().getRow();
            int playerCol = MainMenu.maze.getPlayer().getCol();
            if (this != null && playerRow >= 0 && playerCol >= 0) {
                // Obtenir le chemin vers le joueur
                ArrayList<Point> path = MainMenu.maze.findPath(this.getRow(), this.getCol(), playerRow, playerCol);

                if (path != null && !path.isEmpty()) {
                    // Se déplacer vers le joueur si un chemin est trouvé
                    for (Point nextMove : path) {
                        if(playerCol != MainMenu.maze.getPlayer().getCol() || playerRow != MainMenu.maze.getPlayer().getRow()) {
                            moveRandomly();

                            updateMonster();
                            break;
                        }
                        try {
                            this.setRow(nextMove.x);
                            this.setCol(nextMove.y);
                            world.repaint(); // Redessiner le monstre à sa nouvelle position
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt(); // Préserver l'état d'interruption
                            return;
                        }
                    }
                } else {
                    // Sinon, se déplacer aléatoirement
                    moveRandomly();
                }
            }
        }
    }

    private void moveRandomly() {
        Random random = new Random();
        boolean validMoveFound = false;

        while (!validMoveFound) {
            int randomDirection = random.nextInt(4); // 0: haut, 1: bas, 2: gauche, 3: droite

            int newRow = this.getRow();
            int newCol = this.getCol();

            switch (randomDirection) {
                case 0:
                    newRow--;
                    break;
                case 1:
                    newRow++;
                    break;
                case 2:
                    newCol--;
                    break;
                case 3:
                    newCol++;
                    break;
                default:
                    // Ce cas ne devrait normalement pas être atteint
                    break;
            }

            if (isValidMove(newRow, newCol)) {
                // Déplacer le monstre de manière aléatoire
                this.setRow(newRow);
                this.setCol(newCol);
                validMoveFound = true;
                world.repaint(); // Redessiner le monstre à sa nouvelle position
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // Préserver l'état d'interruption
                }
            }
        }
    }



    private int calculateDirectionToPlayer(int currentRow, int currentCol) {
        // Calculer la direction vers laquelle le monstre doit se déplacer pour s'approcher du joueur
        // Par exemple, un calcul basé sur la différence entre les coordonnées du joueur et du monstre
        int rowDiff = MainMenu.maze.getPlayer().getRow() - currentRow;
        int colDiff = MainMenu.maze.getPlayer().getCol() - currentCol;

        if (Math.abs(rowDiff) > Math.abs(colDiff)) {
            // Se déplacer en direction verticale (haut ou bas)
            if (rowDiff > 0) {
                return 1; // Bas
            } else {
                return 0; // Haut
            }
        } else {
            // Se déplacer en direction horizontale (gauche ou droite)
            if (colDiff > 0) {
                return 3; // Droite
            } else {
                return 2; // Gauche
            }
        }
    }
}

