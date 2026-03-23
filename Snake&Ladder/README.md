# Snake & Ladder

## Overview

This repository contains a Java implementation of Snake & Ladder (Snakes and Ladders), designed as a low-level design practice project. The game supports multiple players, a configurable square board of size `n`, randomized generation of `n` snakes and `n` ladders through a `Factory`, and two movement rule strategies: `easy` and `hard`.

Core behavior:
- Players play in turns and roll one die using `Dice.throwDice()`.
- In the `Easy` strategy, rolling `6` allows extra rolls, and all rolls are added for that turn.
- In the `Hard` strategy, getting three consecutive `6`s cancels the move for that turn.
- `Board` stores snakes and ladders, and `checkSnakeAndLadder` applies jumps when a player lands on either.

## How to run

Use Java 8 or above to compile and run.

On Windows (from the project directory):

```powershell
cd src
javac *.java
java Main
```

Running this starts `Main`, which initializes a `Game` on a 10x10 board with three players by default.

## Project structure

- `Main` - entry point containing `main()`.
- `Game` - coordinates players, board state, and movement strategy.
- `Board` - keeps snakes and ladders and evaluates landing outcomes.
- `Factory` - populates snakes/ladders and returns the requested strategy implementation.
- `IRuleStratergy` - strategy contract implemented by `Easy` and `Hard`.
- `Player` - player entity.
- `Snake`, `Ladder` - simple models for start and end positions.
- `Dice` - helper for die rolls.

## Class Diagram

The following diagram includes classes/interfaces, key members, and their relationships.

```mermaid
classDiagram
    class Game {
        - Queue<Player> players
        - Board board
        - IRuleStratergy ruleStratergy
        + Game(int n, String rule)
        + addPlayer(Player p)
        + run()
    }

    class Board {
        - int n
        - Map<Integer, Snake> snakes
        - Map<Integer, Ladder> ladders
        + Board(int n)
        + int checkSnakeAndLadder(Player p)
    }

    class Player {
        - String name
        - int position
        + Player(String name)
    }

    class Snake {
        - int start
        - int end
    }

    class Ladder {
        - int start
        - int end
    }

    class Factory {
        - static Set<Integer> used
        + IRuleStratergy getStratergy(String rule)
        + populateSnakes(Map<Integer, Snake> snakes, int n)
        + populateLadders(Map<Integer, Ladder> ladders, int n)
    }

    interface IRuleStratergy {
        + int makeMove(Player p)
    }

    class Easy {
        + int makeMove(Player p)
    }

    class Hard {
        + int makeMove(Player p)
    }

    class Dice {
        + static int throwDice()
    }

    class Main {
        + main(String[] args)
    }

    %% Relationships
    Game *-- Board : owns
    Game o-- Player : players (aggregation)
    Game o-- IRuleStratergy : uses

    Board *-- Snake : contains
    Board *-- Ladder : contains
    Board --> Factory : uses

    Factory --> Snake : creates
    Factory --> Ladder : creates
    Factory --> IRuleStratergy : returns

    IRuleStratergy <|.. Easy
    IRuleStratergy <|.. Hard

    Easy --> Dice : uses
    Hard --> Dice : uses

    Main --> Game : starts
    Main --> Player : creates

```

Relationship notation used:
- Association: `-->` (plain arrow)
- Aggregation: `o--` (open diamond)
- Composition: `*--` (filled diamond)

## Class & Method Summary

- `Main`
  - `main(String[] args)` - creates the `Game`, adds `Player` objects, and starts execution.

- `Game`
  - Fields: `players`, `board`, `ruleStratergy`
  - `Game(int n, String rule)` - creates board and strategy through `Factory`.
  - `addPlayer(Player p)` - enqueues a player.
  - `run()` - executes turns, applies strategy moves, processes snakes/ladders, and checks win condition.

- `Board`
  - Fields: `n`, `snakes`, `ladders`
  - `Board(int n)` - initializes state and asks `Factory` to populate snakes/ladders.
  - `int checkSnakeAndLadder(Player p)` - returns updated position if the player hits a snake or ladder.

- `Player`
  - Fields: `name`, `position`
  - `Player(String name)` - constructor with initial position set to `0`.

- `Snake` / `Ladder`
  - Fields: `start`, `end` (integers).

- `Factory`
  - `getStratergy(String rule)` - returns an `Easy` or `Hard` strategy.
  - `populateSnakes(Map<Integer, Snake> snakes, int n)` - randomly inserts `n` snakes.
  - `populateLadders(Map<Integer, Ladder> ladders, int n)` - randomly inserts `n` ladders.

- `IRuleStratergy`
  - `int makeMove(Player p)` - strategy method contract.

- `Easy` / `Hard`
  - `int makeMove(Player p)` - rule-specific movement logic, both based on `Dice.throwDice()`.

- `Dice`
  - `static int throwDice()` - returns a random value from 1 to 6.

## Notes / Possible Enhancements

- Make board size and winning target fully configurable instead of fixed to 10x10 and 100.
- Add deterministic random seeding in `Factory` for repeatable test scenarios.
- Add unit tests for `Factory` population, `IRuleStratergy` behavior, and `Board` transitions.