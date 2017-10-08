package pl.bartoszkomoder.snake.controller;


import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import pl.bartoszkomoder.snake.model.Apple;
import pl.bartoszkomoder.snake.model.Snake;


public class Controller {

    @FXML
    private Canvas canvas;
    @FXML
    private Label result;
    @FXML
    private VBox centerVBox;
    @FXML
    private Button start;
    @FXML
    private Label actualLevel;

    public static final int POINT_SIZE = 20;

    private int xMove;
    private int yMove;
    private int difficulty;
    private int score;
    private int level;

    private boolean gameOn;

    private Snake snake;
    private Apple apple;
    private GraphicsContext graphicsContext;


    public void initialize() {
        graphicsContext = canvas.getGraphicsContext2D();

        //setting arrows as a snake movements keys
        canvas.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.UP) {
                moveUp();
            } else if (keyEvent.getCode() == KeyCode.DOWN) {
                moveDown();
            } else if (keyEvent.getCode() == KeyCode.LEFT) {
                moveLeft();
            } else if (keyEvent.getCode() == KeyCode.RIGHT) {
                moveRight();
            }
        });

        //button for starting new game
        start.setOnAction(event -> {
            graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            restartGame();
            snakeStart();
        });
    }

    public void setXMove(int xMove) {
        this.xMove = xMove;
    }

    public void setYMove(int yMove) {
        this.yMove = yMove;
    }

    //method with a game thread
    public void snakeStart() {
        //setting a flag that indicates the game is running
        gameOn = true;
        //disable the start button when game is running
        start.setDisable(true);
        //creating snake object
        snake = new Snake();
        //creating apple object
        apple = new Apple(canvas);
        //setting initial movement to right
        moveRight();
        //creating first apple
        createApple();
        //staring thread with game
        Thread snakeThread = new Thread(() -> {
            while (gameOn) {
                try {
                    Thread.sleep(difficulty);
                } catch (InterruptedException e) {
                    gameOn = false;
                }

                snake.move(xMove, yMove);
                //checking whether the snake is "alive"
                if (isSnakeDead()) {
                    gameOn = false;
                } else {
                    //checking whether snake eats apple or not
                    extend();
                    //drawing atual situation on the screen
                    drawBoard();
                }
            }
            //drawing "GAME OVER" after snake is "dead"
            drawLose();
        });
        //setting deamon for thread(thread is killing when the main thread ends
        snakeThread.setDaemon(true);
        //starting a thread
        snakeThread.start();
    }

    //checking whether snake hit his body or walls
    public boolean isSnakeDead() {
        if (snake.getBody().contains(snake.getHead())) {
            return true;
        } else {
            return isWallHited();
        }
    }

    //checking whether snake git the wall
    public boolean isWallHited() {
        double xPos = snake.getHead().getX();
        double yPos = snake.getHead().getY();
        return (xPos >= canvas.getWidth()) || (xPos < 0) || (yPos >= canvas.getWidth()) || (yPos < 0);
    }

    //drawing board
    public void drawBoard() {
        //cleaning up  a canvas
        graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        //setting color for snake
        graphicsContext.setFill(new Color(0, 0, 0, 1));
        //drawing snake head
        graphicsContext.fillRect(snake.getHead().getX(), snake.getHead().getY(), POINT_SIZE - 1, POINT_SIZE - 1);

        //drawing snake body
        for (Point2D p : snake.getBody()) {
            graphicsContext.fillRect(p.getX(), p.getY(), POINT_SIZE - 1, POINT_SIZE - 1);
        }

        //setting color for apple
        graphicsContext.setFill(new Color(0, 0, 0, 1));
        //drawing apple
        graphicsContext.fillRect(apple.getApple().getX(), apple.getApple().getY(), POINT_SIZE - 1, POINT_SIZE - 1);
    }

    //drawing GameOver text when player lose
    public void drawLose() {
        graphicsContext.setFont(new Font(48));
        graphicsContext.fillText("GAME OVER", canvas.getWidth() / 4, canvas.getHeight() / 2);
        start.setDisable(false);
    }

    //logic for mowing up
    public void moveUp() {
        //statement for blocking snake to go reverse and "eat" itself
        if (yMove != POINT_SIZE) {
            setYMove(-POINT_SIZE);
            setXMove(0);
        }
    }

    //logic for moving down
    public void moveDown() {
        if (yMove != (-POINT_SIZE)) {
            setYMove(POINT_SIZE);
            setXMove(0);
        }
    }

    //logic for moving left
    public void moveLeft() {
        if (xMove != POINT_SIZE) {
            setYMove(0);
            setXMove(-POINT_SIZE);
        }
    }

    //logic for moving right
    public void moveRight() {
        if (xMove != (-POINT_SIZE)) {
            setYMove(0);
            setXMove(POINT_SIZE);
        }
    }


    public void extend() {
        //checking whether snake eat the apple
        if (snake.getHead().equals(apple.getApple())) {
            snake.setExtend(true);
            //creating new apple
            createApple();
            increaseDifficulty();
            //increasing score
            score += 10;
            //passing actual score to label
            Platform.runLater(() -> result.setText(score + ""));
        }
    }

    //creating new apple
    public void createApple() {
        apple.randomizeApple();
        if (apple.getApple().equals(snake.getHead())) {
            createApple();
        }
    }

    //setting initial values for a new game
    public void restartGame() {
        xMove = 0;
        yMove = 0;
        score = 0;
        difficulty = 200;
        level= 1;
        Platform.runLater(() -> result.setText(score + ""));
        Platform.runLater(() -> actualLevel.setText(level + ""));
    }

    //increasing difficulty after each 10 apples eaten
    public void increaseDifficulty() {
        //when Snake have 10 pieces with head to level is increasing
        if (snake.getBody().size() > 0 && (snake.getBody().size() + 1) % 10 == 0) {
            //statement for set a max refreshing rate for 50ms
            if (difficulty > 50) {
                //increasing movement speed by faster refreshing on canvas
                difficulty -= 50;
                //increasing level value
                level++;
                //updating level on te screen
                Platform.runLater(() -> actualLevel.setText(level + ""));
            //if resreshing speed is 50ms, screen starts to blink
            } else {
                //increasing level value
                level++;
                //updating level on te screen
                Platform.runLater(() -> actualLevel.setText(level + ""));
                //starting new thread for "blinking screen"
                Thread difficult = new Thread(() -> {
                    while (gameOn) {
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        centerVBox.setStyle("-fx-background-color: #000002");

                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        centerVBox.setStyle("-fx-background-color: #9AC503");
                    }
                });
                //setting deamon for thread(thread is killing when the main thread ends
                difficult.setDaemon(true);
                difficult.start();
            }
        }
    }


}
