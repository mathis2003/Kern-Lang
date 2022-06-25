package com.example.kernlang;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

import java.util.ArrayList;

/**
 * This class contains some data about the state of the cursor in the window of the application.
 * It acts as the model of the MVC, the CodebaseViewer on the other hand is the viewer and controller.
 */
public class CursorState implements Observable {

    private final ArrayList<InvalidationListener> listeners;

    @Override
    public void addListener(InvalidationListener invalidationListener) {
        listeners.add(invalidationListener);
    }

    @Override
    public void removeListener(InvalidationListener invalidationListener) {
        listeners.remove(invalidationListener);
    }

    private void fireInvalidationEvent() {
        for (InvalidationListener l : listeners) {
            l.invalidated(this);
        }
    }

    private enum State {
        DRAGGING_EDGE, DRAGGING_NODE, FREE
    }

    private double clickedX, clickedY;
    private double currentX, currentY;

    Line importLine;

    private State state = State.FREE;

    private final CodebaseViewer cbv;

    public CursorState(CodebaseViewer cbv) {
        this.listeners = new ArrayList<>();
        this.cbv = cbv;
    }

    public void setStateDraggingEdge() {
        state = State.DRAGGING_EDGE;
        importLine = new Line();
        importLine.setStartX(clickedX);
        importLine.setStartY(clickedY);
        importLine.setEndX(clickedX);
        importLine.setEndY(clickedY);
        this.cbv.getChildren().add(importLine);
    }

    public void setStateFree() {
        state = State.FREE;
        fireInvalidationEvent();
    }

    public boolean isDraggingEdge() {
        return state == State.DRAGGING_EDGE;
    }

    public void updatePosition(double x, double y) {
        currentX = x;
        currentY = y;
        if (isDraggingEdge()) {
            importLine.setEndX(currentX);
            importLine.setEndY(currentY);
        }
    }

    public void updateClickedPosition(double x, double y) {
        clickedX = x;
        clickedY = y;
    }

    public void drawCircle() {
        Circle c = new Circle();
        c.setCenterX(clickedX);
        c.setCenterY(clickedY);
        c.setRadius(20);
        c.setFill(Color.GRAY);
        cbv.getChildren().add(c);
    }
}
