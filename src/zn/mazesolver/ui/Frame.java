package zn.mazesolver.ui;


import zn.mazesolver.world.Maze;

import javax.swing.*;
import java.awt.*;

public class Frame extends JPanel {

    Maze maze;

    public Frame(Maze maze) {
        this.maze = maze;
        initComponent();
        setLocation(120, 80);


    }

    private void initComponent() {
    JPanel main = new JPanel();
    main.setLayout(new BorderLayout());
    this.add(main);
    main.add(maze, BorderLayout.CENTER);
    //on veut aussi un bouton generer qui genere un nouveau maze et un slider vitesse pour affluer sur la vitesse de creation et de solve
    JPanel control = new JPanel();
    main.add(control, BorderLayout.SOUTH);
    JButton generate = new JButton("Generate");
    control.add(generate);
    //on veut un label qui affiche la valeur

    JSlider speed = new JSlider(0, 100, 50);
    control.add(speed);
    JLabel speedLabel = new JLabel("Speed Factor : " +speed.getValue());
    control.add(speedLabel);
        generate.addActionListener(e -> {
            maze.generate();
        });
    speed.addChangeListener(e -> {
        speedLabel.setText("Speed : " + speed.getValue());
        maze.setSpeed(getAdjustedValue(speed));
    });

    //on rajoute un bouton solve
    JButton solve = new JButton("Solve");
    control.add(solve);
    solve.addActionListener(e -> maze.runSolve());

    //on mets tout a setFocusable(false) sauf maze
        solve.setFocusable(false);
        generate.setFocusable(false);
        speed.setFocusable(false);
        speedLabel.setFocusable(false);




    }

    private int getAdjustedValue(JSlider speed) {
        //en gros on veut que plus la vitesse augmente plus le temps de sleep diminue
        //on veut que la vitesse soit entre 0 et 100
        return 101-speed.getValue();
    }


}


