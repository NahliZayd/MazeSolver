package zn.mazesolver.world;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import zn.mazesolver.entity.Entity;
import zn.mazesolver.entity.entitys.Monster;
import zn.mazesolver.entity.entitys.Player;
import zn.mazesolver.ui.MainMenu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

import static java.lang.Thread.sleep;


public class Maze extends JPanel implements Runnable {

    public final static int WALL_CODE = 1;
    public final static int PATH_CODE = 2;
    public final static int EMPTY_CODE = 3;
    public final static int VISITED_CODE = 4;
    final static int BACKGROUND_CODE = 0;
    public final int numRows = 41;
    public final int numColumns = 51;
    final int border = 0;
    final int sleepTime = 5000;
    final int blockSize = 12;

    final ArrayList<Entity> entities = new ArrayList<>();
    final Color backgroundColor = getRandomColor();
    final Color wallColor = backgroundColor;
    final Color pathColor = getRandomColor();
    final Color emptyColor = Color.WHITE;
    final Color visitedColor = new Color(200, 200, 200);
    public int[][] maze;
    public int totalWidth;
    public int totalHeight;
    public int left;
    public int top;
    int speedSleep = 30;
    int width = -1;
    int height = -1;
    boolean mazeExists = false;
    Player player;

    public Maze() {
        setBackground(backgroundColor);
        setPreferredSize(new Dimension(blockSize * numColumns, blockSize * numRows));
        setFocusable(true);
        requestFocusInWindow();
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(@NotNull KeyEvent e) {
                if (player == null) return;
                System.out.println("Key pressed");
                player.movePlayer(e.getKeyCode());
                handleVictory();
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(@NotNull MouseEvent e) {
                if (player == null) return;
                if (e.getButton() == MouseEvent.BUTTON1) {
                    player.performAttackAnimation((Graphics2D) MainMenu.maze.getGraphics(), player);
                    player.attack();
                }
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(@NotNull MouseEvent e) {
                if (player == null) return;
                player.updatePlayerDirection(e.getX(), e.getY());
            }
        });


        checkSize();


    }

    private @NotNull Color getRandomColor() {
        Random random = new Random();
        return new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255));
    }

    private void handleVictory() {
        if (player.getRow() == numRows - 2 && player.getCol() == numColumns - 2) {

            int choice = JOptionPane.showConfirmDialog(this, "You won! Do you want to play again?", "Victory", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                entities.clear();
                generate();

            } else {
                System.exit(0);
            }


        }
    }

    public void updateMonsters() {
        for (Entity entity : entities) {
            if (entity instanceof Monster) {
                ((Monster) entity).updateMonster();
            }
        }
    }

    private void spawnEntities() {
        Random random = new Random();
        while (entities.size() < 4) {
            int row = random.nextInt(numRows);
            int col = random.nextInt(numColumns);
            if (maze[row][col] == EMPTY_CODE) {
                entities.add(new Monster(row, col, 20, 2, getRandomColor()));
                System.out.println("Monster spawned at " + row + ", " + col);
            }
        }

        new Thread(() -> {
            while (true) {
                ArrayList<Entity> toMove = new ArrayList<>(getEntities());
                for (Entity entity : getEntities()) {
                    if (entity == player) continue;
                    if (entity == null) continue;
                    if (entity.isDead()) continue;
                    toMove.add(entity);
                }
                for (Entity entity : toMove) {
                    entity.move();
                }
                try {
                    sleep(1000);
                } catch (InterruptedException ignored) {
                }
            }
        }).start();


        new Thread(() -> {
            ArrayList<Entity> toRemove = new ArrayList<>();
            while (true) {

                for (Entity entity : entities) {
                    if (entity == player) continue;
                    if (entity == null) continue;
                    if (entity.isDead()) {
                        toRemove.add(entity);
                    }
                }
                entities.removeAll(toRemove);
                try {
                    sleep(1500);
                } catch (InterruptedException ignored) {
                }
            }
        }).start();
    }

    void checkSize() {

        if (getWidth() != width || getHeight() != height) {
            width = getWidth();
            height = getHeight();
            int w = (width) / numColumns;
            int h = (height) / numRows;
            left = (width - w * numColumns) / 2;
            top = (height - h * numRows) / 2;
            totalWidth = w * numColumns;
            totalHeight = h * numRows;
        }
    }


    synchronized protected void paintComponent(@NotNull Graphics g) {
        super.paintComponent(g);
        checkSize();
        redrawMaze(g);

        drawEntities(g);
    }

    private void drawEntities(@NotNull Graphics g) {

        ArrayList<Entity> entitiesCopy = new ArrayList<>(getEntities());

        for (Entity entity : entitiesCopy) {
            if (entity == null) continue;
            if (entity.isDead()) continue;
            entity.draw(g);
        }
    }


    void redrawMaze(@NotNull Graphics g) {
        if (mazeExists) {
            int w = totalWidth / numColumns;
            int h = totalHeight / numRows;


            for (int j = 0; j < numColumns; j++) {
                for (int i = 0; i < numRows; i++) {
                    if (!(i == 0 && j == 0) && !(i == numRows - 1 && j == numColumns - 1)) {
                        if (maze[i][j] < 0) {
                            g.setColor(emptyColor);
                        } else {
                            g.setColor(getColorFromCode(maze[i][j]));
                        }
                        g.fillRect((j * w) + left, (i * h) + top, w, h);
                    }
                }
            }


            g.setColor(pathColor);
            g.fillRect(left, top, w, h);
            g.fillRect(left + w, top, w, h);
            g.fillRect(left, top + h, w, h);


            g.fillRect((numColumns - 1) * w + left, (numRows - 1) * h + top, w, h);

            g.fillRect((numColumns - 2) * w + left, (numRows - 1) * h + top, w, h);
            g.fillRect((numColumns - 1) * w + left, (numRows - 2) * h + top, w, h);

            repaint();

        }
    }


    private Color getColorFromCode(int code) {
        return switch (code) {
            case BACKGROUND_CODE -> backgroundColor;
            case WALL_CODE -> wallColor;
            case PATH_CODE -> pathColor;
            case EMPTY_CODE -> emptyColor;
            case VISITED_CODE -> visitedColor;
            default -> Color.BLACK;
        };
    }

    public void run() {

        try {
            sleep(1000);
        } catch (InterruptedException ignored) {
        }

        entities.clear();
        makeMaze();
        player = new Player(1, 1, 20, 10, pathColor.darker().darker());
        entities.add(player);

        spawnEntities();


        synchronized (this) {
            try {
                wait(sleepTime);
            } catch (InterruptedException ignored) {
            }
        }
        repaint();

    }


    void makeMaze() {


        if (maze == null)
            maze = new int[numRows][numColumns];
        int i, j;
        int emptyCt = 0;
        int wallCt = 0;
        int[] wallrow = new int[(numRows * numColumns) / 2];
        int[] wallcol = new int[(numRows * numColumns) / 2];
        for (i = 0; i < numRows; i++)
            for (j = 0; j < numColumns; j++)
                maze[i][j] = WALL_CODE;
        for (i = 1; i < numRows - 1; i += 2)
            for (j = 1; j < numColumns - 1; j += 2) {
                emptyCt++;
                maze[i][j] = -emptyCt;
                if (i < numRows - 2) {
                    wallrow[wallCt] = i + 1;
                    wallcol[wallCt] = j;
                    wallCt++;
                }
                if (j < numColumns - 2) {
                    wallrow[wallCt] = i;
                    wallcol[wallCt] = j + 1;
                    wallCt++;
                }
            }
        mazeExists = true;
        repaint();
        int r;
        for (i = wallCt - 1; i > 0; i--) {
            r = (int) (Math.random() * i);
            tearDown(wallrow[r], wallcol[r]);
            wallrow[r] = wallrow[i];
            wallcol[r] = wallcol[i];
        }
        for (i = 1; i < numRows - 1; i++)
            for (j = 1; j < numColumns - 1; j++)
                if (maze[i][j] < 0)
                    maze[i][j] = EMPTY_CODE;
    }


    synchronized void tearDown(int row, int col) {


        if (row % 2 == 1 && maze[row][col - 1] != maze[row][col + 1]) {

            fill(row, col - 1, maze[row][col - 1], maze[row][col + 1]);
            maze[row][col] = maze[row][col + 1];
            repaint();
            try {
                wait(speedSleep);
            } catch (InterruptedException ignored) {
            }
        } else if (row % 2 == 0 && maze[row - 1][col] != maze[row + 1][col]) {

            fill(row - 1, col, maze[row - 1][col], maze[row + 1][col]);
            maze[row][col] = maze[row + 1][col];
            repaint();
            try {
                wait(speedSleep);
            } catch (InterruptedException ignored) {
            }
        }
    }

    void fill(int row, int col, int replace, int replaceWith) {

        if (maze[row][col] == replace) {
            maze[row][col] = replaceWith;
            fill(row + 1, col, replace, replaceWith);
            fill(row - 1, col, replace, replaceWith);
            fill(row, col + 1, replace, replaceWith);
            fill(row, col - 1, replace, replaceWith);
        }
    }

    public @NotNull ArrayList<Point> findPath(int startRow, int startCol, int endRow, int endCol) {
        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        PriorityQueue<Node> openList = new PriorityQueue<>(Comparator.comparingInt(Node::f));
        boolean[][] closedList = new boolean[numRows][numColumns];

        openList.add(new Node(startRow, startCol, null, 0, calculateHeuristic(startRow, startCol, endRow, endCol)));

        while (!openList.isEmpty()) {
            Node current = openList.poll();
            closedList[current.row][current.col] = true;

            if (current.row == endRow && current.col == endCol) {
                return constructPath(current);
            }

            for (int[] direction : directions) {
                int newRow = current.row + direction[0];
                int newCol = current.col + direction[1];

                if (isValidMove(newRow, newCol) && !closedList[newRow][newCol]) {
                    int g = current.g + 1;
                    int h = calculateHeuristic(newRow, newCol, endRow, endCol);
                    openList.add(new Node(newRow, newCol, current, g, h));
                }
            }
        }

        return new ArrayList<>();
    }

    private boolean isValidMove(int row, int col) {
        return row >= 0 && row < numRows && col >= 0 && col < numColumns && (maze[row][col] == EMPTY_CODE || maze[row][col] == PATH_CODE || maze[row][col] == VISITED_CODE);
    }

    private int calculateHeuristic(int row, int col, int endRow, int endCol) {
        return Math.abs(row - endRow) + Math.abs(col - endCol);
    }

    private @NotNull ArrayList<Point> constructPath(@Nullable Node node) {
        ArrayList<Point> path = new ArrayList<>();
        while (node != null) {
            path.add(new Point(node.row, node.col));
            node = node.parent;
        }
        Collections.reverse(path);
        return path;
    }


    boolean solveMaze(int row, int col) {
        if (maze[row][col] == EMPTY_CODE) {
            maze[row][col] = PATH_CODE;
            repaint();
            if (row == numRows - 2 && col == numColumns - 2)
                return true;
            try {
                sleep(speedSleep);
            } catch (InterruptedException ignored) {
            }
            if (solveMaze(row - 1, col) ||
                    solveMaze(row, col - 1) ||
                    solveMaze(row + 1, col) ||
                    solveMaze(row, col + 1))
                return true;

            maze[row][col] = VISITED_CODE;
            repaint();
            synchronized (this) {
                try {
                    wait(speedSleep);
                } catch (InterruptedException ignored) {
                }
            }
        }
        return false;
    }

    public void generate() {
        new Thread(this).start();


    }

    public void setSpeed(int value) {
        this.speedSleep = value;
    }

    public void runSolve() {

        new Thread(() -> {
            solveMaze(1, 1);


        }).start();
    }


    public ArrayList<Entity> getEntities() {
        return entities;
    }

    public Entity getPlayer() {
        return player;
    }
}
