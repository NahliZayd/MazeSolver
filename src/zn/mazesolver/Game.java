package zn.mazesolver;

import com.formdev.flatlaf.FlatDarkLaf;
import zn.mazesolver.ui.Frame;
import zn.mazesolver.ui.MainMenu;
import zn.mazesolver.world.Maze;

public class Game {
    public static void main(String[] args) {
        FlatDarkLaf.install();
        new MainMenu().setVisible(true);

    }
}
