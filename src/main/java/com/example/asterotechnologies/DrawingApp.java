package com.example.asterotechnologies;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class DrawingApp extends Application {
    private String currentTool = ""; // Track the current drawing tool
    private javafx.scene.shape.Shape selectedShape; // Track the selected shape

    @Override
    public void start(Stage primaryStage) {
        Pane drawingPane = new Pane();
        BorderPane root = new BorderPane();
        root.setCenter(drawingPane);

        // Buttons for tools
        Button rectButton = new Button("Rectangle");
        Button circleButton = new Button("Circle");
        Button deleteButton = new Button("Delete");

        rectButton.setOnAction(e -> currentTool = "Rectangle");
        circleButton.setOnAction(e -> currentTool = "Circle");
        deleteButton.setOnAction(e -> {
            if (selectedShape != null) {
                drawingPane.getChildren().remove(selectedShape);
                selectedShape = null;
            }
        });

        root.setTop(rectButton);
        root.setBottom(circleButton);
        root.setRight(deleteButton);

        // Add event handlers for drawing and selecting
        drawingPane.setOnMousePressed(event -> handleMousePressed(event, drawingPane));
        drawingPane.setOnMouseDragged(event -> handleMouseDragged(event));

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Drawing App");
        primaryStage.show();
    }

    private void handleMousePressed(MouseEvent event, Pane drawingPane) {
        if ("Rectangle".equals(currentTool)) {
            Rectangle rect = new Rectangle(event.getX(), event.getY(), 0, 0);
            rect.setOnMousePressed(e -> selectedShape = rect);
            drawingPane.getChildren().add(rect);
            selectedShape = rect;
        } else if ("Circle".equals(currentTool)) {
            Circle circ = new Circle(event.getX(), event.getY(), 0);
            circ.setOnMousePressed(e -> selectedShape = circ);
            drawingPane.getChildren().add(circ);
            selectedShape = circ;
        }
    }

    private void handleMouseDragged(MouseEvent event) {
        if (selectedShape instanceof Rectangle) {
            Rectangle rect = (Rectangle) selectedShape;
            rect.setWidth(event.getX() - rect.getX());
            rect.setHeight(event.getY() - rect.getY());
        } else if (selectedShape instanceof Circle) {
            Circle circ = (Circle) selectedShape;
            double radius = Math.hypot(event.getX() - circ.getCenterX(), event.getY() - circ.getCenterY());
            circ.setRadius(radius);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
