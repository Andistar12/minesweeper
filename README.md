# Minesweeper Java

This is my quick implmentation of the game Minesweeper, in Java. It currently only supports a 10x10 grid with 10 bombs, though it can be modified to support a grid of any size. Flags may be placed to indicate bomb locations. Once all bomb locations have been marked, a win message pops up, and the game may be replayed or reset by hitting reset.

This project was made using Intellij. A runnable JAR form of the program is available under out/artifacts/Minesweeper_jar. I used the built-in Swing libraries to create the GUI and the buttons. Iteration is used to generate the board, but recursion is the main programming tool used to clear the board.