package zn.mazesolver.entity.entitys;

import zn.mazesolver.entity.Entity;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class Player extends Entity {


    double playerAngle = 0; // Angle in radians for the player's direction
    int w;
    int h;
    int playerSize;
    double playerArmLength;
    double armLength;
    
    public Player(int row, int col, int health, int damage, Color color) {
        super(row, col, health, damage, color);
        w = world.totalWidth /  world.numColumns;
        h =  world.totalHeight /  world.numRows;
        playerSize = Math.min(w, h) / 2; // Calculate playerSize here after checkSize()
        armLength = playerSize+10; // Now set armLength properly
        playerArmLength = armLength;
        addTarget(Monster.class);
    }

    @Override
    public void draw(Graphics g) {
        super.draw(g);
        g.setColor(getColor());
        g.fillOval(world.left + getCol() * w + w / 4, world.top + getRow() * h + h / 4, playerSize, playerSize);

        // Draw the arm based on the player's angle
        int armWidth = playerSize / 4;
        int armX = world.left + getCol() * w + w / 2;
        int armY = world.top + getRow() * h + h / 2;

        int endX = (int) (armX + playerArmLength * Math.cos(playerAngle));
        int endY = (int) (armY - playerArmLength * Math.sin(playerAngle));

        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(armWidth));
        g2d.drawLine(armX, armY, endX, endY);
    }
    @Override
    protected double getAttackRadius() {
        return playerArmLength;
    }

    @Override
    public void move() {
      return;
    }
    public void movePlayer(int keyCode) {
        int newRow = getRow();
        int newCol = getCol();

        switch (keyCode) {
            case KeyEvent.VK_UP:
                newRow--;
                break;
            case KeyEvent.VK_DOWN:
                newRow++;
                break;
            case KeyEvent.VK_LEFT:
                newCol--;
                break;
            case KeyEvent.VK_RIGHT:
                newCol++;
                break;
        }

        if (isValidMove(newRow, newCol)) {
            setRow(newRow);
            setCol(newCol);
            world.repaint();
        }
    }
    public void updatePlayerDirection(int mouseX, int mouseY) {
        int playerX =    world.left + getCol() * (   world.totalWidth /    world.numColumns) + (   world.totalWidth /    world.numColumns) / 2;
        int playerY =    world.top + getRow() * (   world.totalHeight /    world.numRows) + (   world.totalHeight /   world.numRows) / 2;

        double deltaX = mouseX - playerX;
        double deltaY = playerY - mouseY; // Inverted because the screen's Y-axis goes down

        playerAngle = Math.atan2(deltaY, deltaX);
        world.repaint();
    }
    
    

    @Override
    public void performAttackAnimation(Graphics2D g, Entity e) {
            // Simulate an attack by temporarily extending the arm in the current direction
            Timer attackTimer = new Timer(100, new ActionListener() {
                private int step = 0;
                private final int attackSteps = 5;

                @Override
                public void actionPerformed(ActionEvent e) {

                    step++;
                    if (step <= attackSteps) {
                        // Increase the arm length for the attack effect
                        playerArmLength = (step % 2 == 0) ? armLength + 13 : armLength - 13;
                        world.repaint();
                    } else {
                        // End of attack animation, reset the arm length
                        playerArmLength = armLength;
                        ((Timer)e.getSource()).stop();
                        world.repaint();
                    }
                }


            });
            attackTimer.start();
        
    }
}
