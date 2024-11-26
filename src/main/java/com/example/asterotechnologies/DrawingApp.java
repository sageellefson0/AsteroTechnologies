package com.example.asterotechnologies;

//Imports
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import java.util.UUID;


//Future Improvements:
// 1. Add additional shapes and drawing tools. This could be done by adding additional private classes to "DrawingApp".

// 2. Add an undo/redo feature that would allow the user to revert to previous work without fully deleting a shape.
// A new class, "ShapeHistory" could be created using Stack<> logic to "log" shapes, and the order which they were placed.
// This class would need methods to go with it, "undoAction" and "redoAction" which would remove the most recent shape, and add back the most recent shape from the stack.

// 3. Provide additional coloring manipulation, such as opacity of the shape.

// 4. A method, "imageExport(File file)" could be added to save the project to a jpg, png, etc. This would also require a button being added within the application.


/** JavaFX drawing application. Tools for creating rectangles and circles in different colors and the ability to delete selected shapes. */
public class DrawingApp extends Application {
    private String activeTool = "";
    private Shape selectedShape;
    private Color rectColor = Color.LIGHTBLUE;
    private Color circColor = Color.LIGHTGREEN;
    private Color currentColor = rectColor;
    private final double paneWidth = 600;
    private final double paneHeight = 400;

    @Override
    public void start(Stage primaryStage) {
        Pane drawingPane = new Pane();
        drawingPane.setMinSize(paneWidth, paneHeight);
        drawingPane.setMaxSize(paneWidth, paneHeight);
        drawingPane.setStyle("-fx-border-color: black; -fx-border-width: 2;");

        BorderPane root = new BorderPane();
        root.setCenter(drawingPane);

        //Buttons for the rectangle, circle, and delete operations
        Button rectButton = new Button("Rectangle");
        Button circleButton = new Button("Circle");
        Button deleteButton = new Button("Delete");

        //Color picker for changing the tool color
        ColorPicker colorPicker = new ColorPicker(currentColor);
        colorPicker.setOnAction(e -> {
            if ("Rectangle".equals(activeTool)) {
                rectColor = colorPicker.getValue();
                rectButton.setStyle("-fx-background-color: " + toRgbString(rectColor) + ";");
            } else if ("Circle".equals(activeTool)) {
                circColor = colorPicker.getValue();
                circleButton.setStyle("-fx-background-color: " + toRgbString(circColor) + ";");
            }
            currentColor = colorPicker.getValue();
        });

        //Action for the rectangle button
        rectButton.setOnAction(e -> {
            activeTool = "Rectangle";
            currentColor = rectColor;
            colorPicker.setValue(currentColor);
        });

        //Action for the circle button
        circleButton.setOnAction(e -> {
           activeTool = "Circle";
            currentColor = circColor;
            colorPicker.setValue(currentColor);
        });

        //Action for the delete button
        deleteButton.setOnAction(e -> deleteSelectedShape(drawingPane));

        //Styling for the tool buttons and color picker
        HBox buttonBox = new HBox(10, rectButton, circleButton, colorPicker, deleteButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10, 10, 20, 10));
        root.setBottom(buttonBox);

        //Default button styling
        rectButton.setStyle("-fx-background-color: " + toRgbString(rectColor) + ";");
        circleButton.setStyle("-fx-background-color: " + toRgbString(circColor) + ";");
        deleteButton.setStyle("-fx-background-color: lightcoral;");

        //Drawing pane for creating the shapes
        drawingPane.setOnMousePressed(event -> {
            if (isWithinPane(event)) {
            //Creates new rectangle shape
                if ("Rectangle".equals(activeTool)) {
                    Rectangle rect = new Rectangle(event.getX(), event.getY(), 0, 0);
                    rect.setFill(rectColor);
                    rect.setId(UUID.randomUUID().toString());
                    drawingPane.getChildren().add(rect);

                    drawingPane.setOnMouseDragged(dragEvent -> {
                        double x1 = Math.max(0, Math.min(event.getX(), paneWidth));
                        double y1 = Math.max(0, Math.min(event.getY(), paneHeight));
                        double x2 = Math.max(0, Math.min(dragEvent.getX(), paneWidth));
                        double y2 = Math.max(0, Math.min(dragEvent.getY(), paneHeight));

                        rect.setX(Math.min(x1, x2));
                        rect.setY(Math.min(y1, y2));
                        rect.setWidth(Math.abs(x2 - x1));
                        rect.setHeight(Math.abs(y2 - y1));
                    });

                    drawingPane.setOnMouseReleased(releaseEvent -> {
                        addDragHandlers(rect, drawingPane);
                        selectShape(releaseEvent, rect);
                        drawingPane.setOnMouseDragged(null);
                        drawingPane.setOnMouseReleased(null);
                    });
                  //Creates new circle shape
                } else if ("Circle".equals(activeTool)) {
                    Circle circ = new Circle(event.getX(), event.getY(), 0);
                    circ.setFill(circColor);
                    circ.setId(UUID.randomUUID().toString());
                    drawingPane.getChildren().add(circ);

                    drawingPane.setOnMouseDragged(dragEvent -> {
                        double centerX = Math.max(0, Math.min(circ.getCenterX(), paneWidth));
                        double centerY = Math.max(0, Math.min(circ.getCenterY(), paneHeight));
                        double edgeX = Math.max(0, Math.min(dragEvent.getX(), paneWidth));
                        double edgeY = Math.max(0, Math.min(dragEvent.getY(), paneHeight));

                        double radius = Math.min(
                                Math.sqrt(Math.pow(edgeX - centerX, 2) + Math.pow(edgeY - centerY, 2)),
                                Math.min(Math.min(centerX, paneWidth - centerX), Math.min(centerY, paneHeight - centerY))
                        );

                        circ.setRadius(radius);
                    });

                    drawingPane.setOnMouseReleased(releaseEvent -> {
                        addDragHandlers(circ, drawingPane);
                        selectShape(releaseEvent, circ);
                        drawingPane.setOnMouseDragged(null);
                        drawingPane.setOnMouseReleased(null);
                    });
                }
            }
        });

        // Sets the window size and title
        Scene scene = new Scene(root, 900, 700);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Drawing App");
        primaryStage.show();
    }

    //Checks if the mouse event is within the boundaries of the pane
    private boolean isWithinPane(MouseEvent event) {
        return event.getX() >= 0 && event.getX() <= paneWidth && event.getY() >= 0 && event.getY() <= paneHeight;
    }

    //Allows for dragging of the shapes within the pane boundaries
    private void addDragHandlers(Shape shape, Pane drawingPane) {
        final double[] dragDelta = new double[2];

        shape.setOnMousePressed(event -> {
            selectShape(event, shape);
            if (shape instanceof Rectangle) {
                Rectangle rect = (Rectangle) shape;
                dragDelta[0] = event.getX() - rect.getX();
                dragDelta[1] = event.getY() - rect.getY();
            } else if (shape instanceof Circle) {
                Circle circ = (Circle) shape;
                dragDelta[0] = event.getX() - circ.getCenterX();
                dragDelta[1] = event.getY() - circ.getCenterY();
            }
            event.consume();
        });

        shape.setOnMouseDragged(event -> {
            double newX = event.getX() - dragDelta[0];
            double newY = event.getY() - dragDelta[1];

            if (shape instanceof Rectangle) {
                Rectangle rect = (Rectangle) shape;
                newX = Math.max(0, Math.min(newX, paneWidth - rect.getWidth()));
                newY = Math.max(0, Math.min(newY, paneHeight - rect.getHeight()));
                rect.setX(newX);
                rect.setY(newY);
            } else if (shape instanceof Circle) {
                Circle circ = (Circle) shape;
                double radius = circ.getRadius();
                newX = Math.max(radius, Math.min(newX, paneWidth - radius));
                newY = Math.max(radius, Math.min(newY, paneHeight - radius));
                circ.setCenterX(newX);
                circ.setCenterY(newY);
            }
            event.consume();
        });
    }
    //Selects shape and highlights in red
    private void selectShape(MouseEvent event, Shape shape) {
        if (selectedShape != null && selectedShape != shape) {
            selectedShape.setStroke(null);
        }
        selectedShape = shape;
        shape.setStroke(Color.RED);
    }

    //Deletes the selected shape
    private void deleteSelectedShape(Pane drawingPane) {
        if (selectedShape != null) {
            drawingPane.getChildren().remove(selectedShape);
            selectedShape = null;
        }
    }

    //Converts color to RGB values
    private String toRgbString(Color color) {
        return "rgb(" + (int)(color.getRed() * 255) + ", " + (int)(color.getGreen() * 255) + ", " + (int)(color.getBlue() * 255) + ")";
    }

    public static void main(String[] args) {
        launch(args);
    }
}
