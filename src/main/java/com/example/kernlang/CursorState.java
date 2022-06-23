package com.example.kernlang;

import javafx.scene.shape.Line;

/**
 * This class contains some data about the state of the cursor in the window of the application.
 * The object of this class can be passed around by reference as a means of mutating this state from different classes.
 */
public class CursorState {
    private enum State {
        DRAGGING_EDGE, DRAGGING_NODE, FREE
    }

    double startX, startY;
    double currentX, currentY;

    Line importLine;

    private State state = State.FREE;

    private final CodebaseViewer cbv;

    public CursorState(CodebaseViewer cbv) {
        this.cbv = cbv;
    }

    public void setStateDraggingEdge() {
        state = State.DRAGGING_EDGE;
        startX = currentX;
        startY = currentY;
        importLine = new Line();
        importLine.setStartX(startX);
        importLine.setStartY(startY);
        importLine.setEndX(currentX);
        importLine.setEndY(currentY);
        this.cbv.getChildren().add(importLine);
    }

    public void setStateDraggingNode() {
        state = State.DRAGGING_NODE;
    }

    public void setStateFree() {
        state = State.FREE;
    }

    public boolean isDraggingNode() {
        return state == State.DRAGGING_NODE;
    }

    public boolean isDraggingEdge() {
        return state == State.DRAGGING_EDGE;
    }

    public boolean isFree() {
        return state == State.FREE;
    }
}
