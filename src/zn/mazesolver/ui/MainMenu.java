package zn.mazesolver.ui;

import zn.mazesolver.Difficulty;
import zn.mazesolver.Settings;
import zn.mazesolver.world.Maze;

import javax.swing.*;
import java.awt.*;

public class MainMenu extends JFrame {
    public CardLayout cardLayout;
    public JPanel cardPanel;
    public static Maze maze;
    private JPanel mazePanel;
    private JPanel settingsPanel;
    private JPanel bossPanel;

    public     JCheckBox customDimension;

    public MainMenu() {
        maze = new Maze();

        setTitle("Maze Solver");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        initMainMenu();
        initMazePanel();
        initSettingsPanel();
        //initBossPanel();

        add(cardPanel);
    }

    private void initMainMenu() {
        JPanel mainMenuPanel = new JPanel(new BorderLayout());

        JLabel title = new JLabel("Maze Solver", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        mainMenuPanel.add(title, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        JButton startButton = new JButton("Start");
        startButton.addActionListener(e -> {
            cardLayout.show(cardPanel, "MazePanel");
            maze.requestFocusInWindow();
        });
        buttonPanel.add(startButton);

        JButton settingsButton = new JButton("Settings");
        settingsButton.addActionListener(e -> {
            cardLayout.show(cardPanel, "SettingsPanel");
        });
        buttonPanel.add(settingsButton);

        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(e -> {
            System.exit(0);
        });
        buttonPanel.add(exitButton);

        mainMenuPanel.add(buttonPanel, BorderLayout.CENTER);

        cardPanel.add(mainMenuPanel, "MainMenuPanel");
    }

    private void initMazePanel() {

        mazePanel = new Frame(maze);

        cardPanel.add(mazePanel, "MazePanel");
    }

    private void initBossPanel() {
       // bossPanel = new BossRoom();
       // cardPanel.add(bossPanel, "BossPanel");
    }

    private void initSettingsPanel() {
        settingsPanel = new JPanel(new BorderLayout());

        JLabel title = new JLabel("Settings", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        settingsPanel.add(title, BorderLayout.NORTH);

        JPanel settingsContentPanel = new JPanel();
        settingsContentPanel.setLayout(new BoxLayout(settingsContentPanel, BoxLayout.Y_AXIS));

        // Volume
        JLabel volumeLabel = new JLabel("Volume");
        JSlider volumeSlider = new JSlider(0, 100, Settings.volume);
        volumeSlider.addChangeListener(e -> Settings.volume = volumeSlider.getValue());
        settingsContentPanel.add(volumeLabel);
        settingsContentPanel.add(volumeSlider);

        // Generation Speed
        JLabel speedLabel = new JLabel("Generation Speed");
        JSlider speedSlider = new JSlider(0, 100, Settings.generationSpeed);
        speedSlider.addChangeListener(e -> Settings.generationSpeed = speedSlider.getValue());
        settingsContentPanel.add(speedLabel);
        settingsContentPanel.add(speedSlider);



        // Rows
        JLabel rowsLabel = new JLabel("Rows");
        JSpinner rowsSpinner = new JSpinner(new SpinnerNumberModel(Settings.rows, 1, 100, 1));
        rowsSpinner.addChangeListener(e -> Settings.rows = (int) rowsSpinner.getValue());
        settingsContentPanel.add(rowsLabel);
        settingsContentPanel.add(rowsSpinner);

        // Columns
        JLabel columnsLabel = new JLabel("Columns");
        JSpinner columnsSpinner = new JSpinner(new SpinnerNumberModel(Settings.columns, 1, 100, 1));
        columnsSpinner.addChangeListener(e -> Settings.columns = (int) columnsSpinner.getValue());
        settingsContentPanel.add(columnsLabel);
        settingsContentPanel.add(columnsSpinner);



        // Difficulty
        JLabel difficultyLabel = new JLabel("Difficulty");
        JComboBox<Difficulty> difficultyComboBox = new JComboBox<>(Difficulty.values());
        difficultyComboBox.setSelectedItem(Settings.difficulty);
        difficultyComboBox.addActionListener(e -> Settings.difficulty = (Difficulty) difficultyComboBox.getSelectedItem());
        settingsContentPanel.add(difficultyLabel);
        settingsContentPanel.add(difficultyComboBox);
        //custom dimension

        customDimension = new JCheckBox("Custom Dimension", false);
        customDimension.addActionListener(e -> {
            if (customDimension.isSelected()) {
                rowsSpinner.setEnabled(true);
                columnsSpinner.setEnabled(true);
            } else {
                rowsSpinner.setEnabled(false);
                columnsSpinner.setEnabled(false);
            }
        });

        // Sound
        JCheckBox soundCheckBox = new JCheckBox("Sound", Settings.sound);
        soundCheckBox.addActionListener(e -> Settings.sound = soundCheckBox.isSelected());
        settingsContentPanel.add(soundCheckBox);

        // Music
        JCheckBox musicCheckBox = new JCheckBox("Music", Settings.music);
        musicCheckBox.addActionListener(e -> Settings.music = musicCheckBox.isSelected());
        settingsContentPanel.add(musicCheckBox);

        JButton backButton = new JButton("Back to Main Menu");
        backButton.addActionListener(e -> {
            cardLayout.show(cardPanel, "MainMenuPanel");
        });
        settingsContentPanel.add(backButton);

        settingsPanel.add(settingsContentPanel, BorderLayout.CENTER);

        cardPanel.add(settingsPanel, "SettingsPanel");
    }


    public void showBossRoom() {
        cardLayout.show(cardPanel, "BossPanel");
    }
}
