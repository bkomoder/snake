package pl.bartoszkomoder.snake.model;

import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import pl.bartoszkomoder.snake.controller.Controller;

import java.util.Random;

public class Apple {

    Point2D apple;
    Random r = new Random();
    Canvas canvas;

    public Apple(Canvas canvas) {
        this.canvas = canvas;
    }

    //randomizing apple position on the board
    public void randomizeApple() {
        //setting point size
        int pointSize = Controller.POINT_SIZE;
        double xPos = r.nextInt((int) (canvas.getWidth() / pointSize)) * pointSize;
        double yPos = r.nextInt((int) (canvas.getHeight() / pointSize)) * pointSize;
        apple = new Point2D(xPos, yPos);
    }

    public Point2D getApple() {
        return apple;
    }
}
