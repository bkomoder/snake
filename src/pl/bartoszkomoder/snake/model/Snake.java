package pl.bartoszkomoder.snake.model;


import javafx.geometry.Point2D;
import pl.bartoszkomoder.snake.controller.Controller;

import java.util.ArrayList;
import java.util.List;

public class Snake {

    private Point2D head;
    private List<Point2D> body;
    private boolean extend;

    public Snake() {
        this.head = new Point2D(Controller.POINT_SIZE, Controller.POINT_SIZE);
        body = new ArrayList<>();
        extend = false;
    }

    public Point2D getHead() {
        return head;
    }

    private void setHead(double xMove, double yMove) {
        Point2D newPoint = new Point2D(getHead().getX() + xMove, getHead().getY() + yMove);
        this.head = newPoint;
    }

    public List<Point2D> getBody() {
        return body;
    }

    //flag for extending Snake
    public void setExtend(boolean extend) {
        this.extend = extend;
    }

    //method for snake move
    public void move(double xMove, double yMove) {
        //"transfering" head to body
        Point2D head = new Point2D(getHead().getX(), getHead().getY());
        //setting new head
        setHead(xMove, yMove);
        //adding "old" head to body as a first element
        body.add(0, head);
        //if snake ate apple we don't erase last element, if he eats we erase last element to create a movement
        if(!extend) {
            body.remove(body.size() - 1);
        }
        setExtend(false);
    }
}
