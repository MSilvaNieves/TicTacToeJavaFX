package com.example;

import java.util.Scanner;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/** JavaFX application for playing Tic-Tac-Toe against the CPU. */
public class TicTacToeApplication extends Application {
    private final Board board = new Board();
    private final HumanPlayer human = new HumanPlayer('X', new Scanner(""));
    private final CpuPlayer cpu = new CpuPlayer('O');
    private final Button[][] cells = new Button[3][3];
    private final boolean[][] winningCells = new boolean[3][3];
    private final Label status = new Label();
    private boolean gameOver;

    /** Builds and displays the game window. */
    @Override
    public void start(Stage stage) {
        Label eyebrow = new Label("HUMAN VS CPU");
        eyebrow.getStyleClass().add("eyebrow");

        Label title = new Label("Tic-Tac-Toe");
        title.getStyleClass().add("title");

        status.getStyleClass().add("status");

        GridPane grid = new GridPane();
        grid.getStyleClass().add("board");
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(12);
        grid.setVgap(12);
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                Button cell = new Button();
                cell.getStyleClass().add("cell");
                cell.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                int selectedRow = row;
                int selectedColumn = column;
                cell.setOnAction(event -> playHumanMove(selectedRow, selectedColumn));
                cells[row][column] = cell;
                grid.add(cell, column, row);
            }
        }

        Label hint = new Label("Choose an empty square to place your X.");
        hint.getStyleClass().add("hint");

        Button restart = new Button("New game");
        restart.getStyleClass().add("restart-button");
        restart.setOnAction(event -> resetGame());

        VBox content = new VBox(18, eyebrow, title, status, grid, hint, restart);
        content.getStyleClass().addAll("root", "content");
        content.setAlignment(Pos.CENTER);
        content.setFillWidth(true);

        resetGame();
        Scene scene = new Scene(content, 460, 620);
        var stylesheet = getClass().getResource("/app.css");
        if (stylesheet != null) {
            scene.getStylesheets().add(stylesheet.toExternalForm());
        }
        stage.setTitle("Tic-Tac-Toe");
        stage.setMinWidth(400);
        stage.setMinHeight(540);
        stage.setScene(scene);
        stage.show();
    }

    private void playHumanMove(int row, int column) {
        if (gameOver || !human.makeMove(board, row, column)) {
            return;
        }
        refreshCells();
        if (finishIfGameOver(human)) {
            return;
        }

        cpu.makeMove(board);
        refreshCells();
        if (!finishIfGameOver(cpu)) {
            status.setText(human.getName() + " (" + human.getMarker() + "), make your move.");
        }
    }

    private boolean finishIfGameOver(Player lastPlayer) {
        if (board.checkWin()) {
            gameOver = true;
            markWinningLine();
            status.setText(lastPlayer.getName() + " (" + lastPlayer.getMarker() + ") wins!");
        } else if (board.isFull()) {
            gameOver = true;
            status.setText("It's a draw!");
        } else {
            return false;
        }
        refreshCells();
        return true;
    }

    private void refreshCells() {
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                char marker = board.getCell(row, column);
                Button cell = cells[row][column];
                cell.setText(marker == ' ' ? "" : Character.toString(marker));
                cell.getStyleClass().removeAll("x-cell", "o-cell", "winning-cell");
                if (marker == 'X') {
                    cell.getStyleClass().add("x-cell");
                } else if (marker == 'O') {
                    cell.getStyleClass().add("o-cell");
                }
                if (winningCells[row][column]) {
                    cell.getStyleClass().add("winning-cell");
                }
                cell.setDisable(gameOver || marker != ' ');
            }
        }
    }

    private void markWinningLine() {
        for (int index = 0; index < 3; index++) {
            if (sameNonEmpty(board.getCell(index, 0), board.getCell(index, 1), board.getCell(index, 2))) {
                for (int column = 0; column < 3; column++) {
                    winningCells[index][column] = true;
                }
                return;
            }
            if (sameNonEmpty(board.getCell(0, index), board.getCell(1, index), board.getCell(2, index))) {
                for (int row = 0; row < 3; row++) {
                    winningCells[row][index] = true;
                }
                return;
            }
        }
        if (sameNonEmpty(board.getCell(0, 0), board.getCell(1, 1), board.getCell(2, 2))) {
            for (int index = 0; index < 3; index++) {
                winningCells[index][index] = true;
            }
        } else if (sameNonEmpty(board.getCell(0, 2), board.getCell(1, 1), board.getCell(2, 0))) {
            for (int index = 0; index < 3; index++) {
                winningCells[index][2 - index] = true;
            }
        }
    }

    private boolean sameNonEmpty(char first, char second, char third) {
        return first != ' ' && first == second && second == third;
    }

    private void resetGame() {
        board.initialize();
        gameOver = false;
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                winningCells[row][column] = false;
            }
        }
        status.setText(human.getName() + " (" + human.getMarker() + "), make your move.");
        refreshCells();
    }
}
