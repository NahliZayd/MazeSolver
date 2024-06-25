# Maze Solver

This is a maze solver application written in Java. The application generates a maze and solves it using a pathfinding algorithm. It also includes entities such as a player and monsters so you can play.

## Features

- Maze generation and solving
- Player and monster entities with health and attack capabilities
- Player movement and attack controls
- Visibility checks and attack radius for entities
- Health bars for entities

## How to Run

1. Clone the repository to your local machine.
2. Open the project in IntelliJ IDEA 2024.1.3 or any other Java IDE.
3. Run the `Game.java` file to start the application.

## Controls

- Use the arrow keys to move the player.
- Click the left mouse button to attack.

## Code Structure

The main classes in the project are:

- `Maze`: This class represents the maze. It includes methods for generating and solving the maze, as well as handling player and monster entities.
- `Entity`: This is the base class for all entities in the game. It includes properties like health and position, and methods for movement and attack.
- `Player` and `Monster`: These classes extend the `Entity` class and represent the player and monster entities in the game.

```plantuml
@startuml

class Maze {
  -numRows: int
  -numColumns: int
  -maze: int[][]
  -entities: ArrayList<Entity>
  +Maze()
  +run(): void
  +generate(): void
  +solveMaze(int, int): boolean
  +getEntities(): ArrayList<Entity>
}

abstract class Entity {
  -row: int
  -col: int
  -health: int
  +Entity(int, int, int)
  +move(): void
  +attack(): void
  +draw(Graphics): void
  +isDead(): boolean
}

class Player {
  +Player(int, int, int, int, Color)
  +movePlayer(int): void
  +performAttackAnimation(Graphics2D, Player): void
  +updatePlayerDirection(int, int): void
}

class Monster {
  +Monster(int, int, int, int, Color)
  +updateMonster(): void
}

Maze "1" *-- "many" Entity : contains
Entity <|-- Player : extends
Entity <|-- Monster : extends

@enduml```

## Contributing

Contributions are welcome. Please open an issue to discuss your ideas or submit a pull request with your changes.

## License

This project is licensed under the terms of the MIT license.
