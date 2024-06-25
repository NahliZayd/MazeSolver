package zn.mazesolver.entity;

import zn.mazesolver.Game;
import zn.mazesolver.ui.MainMenu;
import zn.mazesolver.world.Maze;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class Entity {
    private int row;
    private int col;
    private int health;
    List<Class<Entity>> targets = new ArrayList<>();
    private int damage;
    private boolean isDead = false;
    private Color color;
    public Maze world;
    int maxHealth;


    public Entity(int row, int col,int health,int damage,Color color) {
        this.row = row;
        this.col = col;
        this.health = health;
        this.damage = damage;
        this.color = color;
        world = MainMenu.maze;
        this.maxHealth = health;
    }


    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }


    public void hurt(int damage) {
        if(isDead) {
            return;
        }
        health -= damage;
        if(health <= 0) {
            isDead = true;
        }
        performHurtAnimation((Graphics2D) MainMenu.maze.getGraphics());
        System.out.println("Entity "+ getClass().getName() + " " + getCol() + " " + getRow() +"is hurt");
    }

    private void performHurtAnimation(Graphics2D g) {
        //on crée un nouveau thread pour l'animation

        new Thread(() -> {
            Color originalColor = getColor();

            // Blink for a total of 5 times
            for (int i = 0; i < 5; i++) {
                // Set the color to red
                g.setColor(new Color(179, 179, 179));
                draw(g);
                MainMenu.maze.repaint();
                // Sleep for 100 milliseconds
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // Set the color back to the original color
                g.setColor(originalColor);
                draw(g);
                MainMenu.maze.repaint();
                // Sleep for 100 milliseconds
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        // Save the original color

    }

    public boolean canAttack(Entity target) {
        int dx = col - target.col;
        int dy = row - target.row;
        double distance = Math.sqrt(dx * dx + dy * dy);

        //isVisible check
        //en gros si la target est pas dasn le champs de vision de l'entité on return false , pour ça on verifie si la target est pas de l'autre coté d'un mur
        if(!isVisible(target)) {
            return false;
        }



        return distance <= getAttackRadius();
    }
    private boolean isVisible(Entity target) {
        int dx = target.getCol() - this.getCol();
        int dy = target.getRow() - this.getRow();
        int steps = Math.max(Math.abs(dx), Math.abs(dy));

        for (int i = 0; i < steps; i++) {
            int x = this.getCol() + dx * i / steps;
            int y = this.getRow() + dy * i / steps;

            // Check if the current position is a wall
            if (world.maze[y][x] == Maze.WALL_CODE) {
                return false;
            }
        }

        return true;
    }


    public void addTarget(Class target) {
        targets.add(target);
    }

    public void attack() {
        Entity closest = null;
        double closestDistance = Double.MAX_VALUE;

        for (Entity e : MainMenu.maze.getEntities()) {
            if (targets.contains(e.getClass())) {
                double distance = Math.sqrt(Math.pow(e.getRow() - this.getRow(), 2) + Math.pow(e.getCol() - this.getCol(), 2));
                if(distance < getAttackRadius() && distance < closestDistance) {
                    closest = e;
                    closestDistance = distance;
                }
            }
        }

        if (closest != null && canAttack(closest)) {
            attack(closest);
        }
    }

    private void attack(Entity e) {
        performAttackAnimation((Graphics2D) MainMenu.maze.getGraphics(),e);
        e.hurt(damage);
    }


    public void draw(Graphics g) {
        int cellWidth = world.totalWidth / world.numColumns;
        int cellHeight = world.totalHeight / world.numRows;
        int barWidth = 40;
        int barHeight = 5;

        int barX = col * cellWidth + (cellWidth - barWidth) / 2;
        int barY = row * cellHeight - barHeight ;

        g.setColor(Color.GREEN);
        g.fillRect(barX, barY, barWidth, barHeight);

        int currentBarWidth = (int) ((health / (float) maxHealth) * barWidth);

        g.setColor(Color.RED);
        g.fillRect(barX, barY, currentBarWidth, barHeight);
    }


    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public int getHealth() {
        return health;
    }

    public int getDamage() {
        return damage;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public boolean isDead() {
        return isDead;
    }

    public void setDead(boolean dead) {
        isDead = dead;
        System.out.println("Entity "+ getClass().getName() + " " + getCol() + " " + getRow() +"is dead");
    }

    public boolean isValidMove(int row, int col) {
        // Check if the row and column are within the bounds of the MainMenu.maze
        if (row < 0 || row >= world.numRows || col < 0 || col >= world.numColumns) {
            return false;
        }

        // Check if the entity can move to the specified location
        // This depends on your game logic. For example, you might check if the location is not a wall
        if (world.maze[row][col] != Maze.EMPTY_CODE && world.maze[row][col] != Maze.PATH_CODE && world.maze[row][col] != Maze.VISITED_CODE) {
            return false;
        }

        return true;
    }


    protected abstract double getAttackRadius();

    public abstract void move();

    public abstract void performAttackAnimation(Graphics2D g, Entity e);
}
