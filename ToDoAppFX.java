package AppFXPack;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;                                 
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
               
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ToDoAppFX extends Application {
    private BorderPane root;
    private VBox sidebar;
    private VBox groupsContainer; // Properly initialized
    private StackPane mainPanel;
    private HashMap<String, VBox> groups = new HashMap<>();


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.getIcons().add(new Image("file:C:/Users/Dinesh B Pawar/Downloads/Languages/ToDo List/Images/icon3.png"));
        primaryStage.setWidth(1000);
        primaryStage.setHeight(700);
        
        primaryStage.setTitle("To-Do List App");
       
        root = new BorderPane();

        // Sidebar
        sidebar = new VBox(10);
        sidebar.setPadding(new Insets(10));
        sidebar.setStyle("-fx-background-color: #2D2D2D;");

        addSidebarOption("My Day");
        addSidebarOption("Important");
        addSidebarOption("Tasks");

        // Separator
        Separator separator = new Separator();
        separator.setStyle("-fx-background-color: #444;");
        separator.setPrefHeight(1); // Thin line
        sidebar.getChildren().add(separator);

        // Groups container for dynamic groups
        groupsContainer = new VBox(10);
        sidebar.getChildren().add(groupsContainer);

        // Spacer to push "+ New Group" button to the bottom
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        sidebar.getChildren().add(spacer);

        // "+ New Group" button at the bottom
        Button newGroupButton = createRoundedButton("+ New Group");
        newGroupButton.setOnAction(e -> createNewGroup());
        sidebar.getChildren().add(newGroupButton);

        // Main Panel
        mainPanel = new StackPane();
        mainPanel.setStyle("-fx-background-color: #232323;");

        // Top Right Buttons
        HBox topRightButtons = new HBox(10);
        topRightButtons.setPadding(new Insets(10));
        topRightButtons.setStyle("-fx-background-color: #1E1E1E;");

        Button restoreButton = createRoundedButton("Restore");
        restoreButton.setOnAction(e -> primaryStage.setWidth(800));

        Button listTasksButton = createRoundedButton("List Tasks");
        listTasksButton.setOnAction(e -> switchToPanel("Tasks"));

        Button settingsButton = createRoundedButton("Settings");
        settingsButton.setOnAction(e -> changeBackground(primaryStage));

        topRightButtons.getChildren().addAll(restoreButton, listTasksButton, settingsButton);

        root.setTop(topRightButtons);
        root.setLeft(sidebar);
        root.setCenter(mainPanel);

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void addSidebarOption(String optionName) {
        Button button = createRoundedButton(optionName);
        button.setOnAction(e -> switchToPanel(optionName));
        sidebar.getChildren().add(button);
    }

    private Button createRoundedButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: #414141; -fx-text-fill: white; -fx-font-size: 14px; -fx-border-radius: 20px; -fx-background-radius: 20px;");
        button.setMinWidth(180);
        return button;
    }

    private VBox createMyDayPanel() {
        VBox myDayPanel = new VBox(10);
        myDayPanel.setPadding(new Insets(10));
        myDayPanel.setStyle("-fx-background-color: #232323;"); // Dark background for the panel
        myDayPanel.setAlignment(Pos.TOP_CENTER);

        // Label at the top
        Label label = new Label("My Day");
        label.setFont(new Font("Arial", 24));
        label.setTextFill(Color.WHITE);

        // ScrollPane for task list
        VBox taskList = new VBox(10);
        taskList.setStyle("-fx-background-color: #383838; -fx-padding: 10; -fx-border-radius: 5; -fx-background-radius: 5;");

          // Creating the ScrollPane with custom CSS styles
        ScrollPane taskScroll = new ScrollPane(taskList);
        taskScroll.setFitToWidth(true);
        taskScroll.setStyle("-fx-background: #383838; -fx-border-color: transparent;");
        taskScroll.setVbarPolicy(ScrollBarPolicy.ALWAYS); // Always show vertical scrollbar if needed
        taskScroll.setHbarPolicy(ScrollBarPolicy.NEVER); // Disable horizontal scrolling

        // Apply the custom scrollbar CSS class
        taskScroll.getStyleClass().add("scroll-pane");

        // TextField and Button for adding tasks
        TextField taskInput = new TextField();
        taskInput.setPromptText("Add a new task");
        taskInput.setStyle("-fx-background-color: #505050; -fx-text-fill: white;");

        Button addTaskButton = createRoundedButton("Add Task");

        addTaskButton.setOnAction(e -> {
            String task = taskInput.getText().trim();
            if (!task.isEmpty()) {
                // Create a task label
                Label taskLabel = new Label(task);
                taskLabel.setFont(new Font("Arial", 16));
                taskLabel.setTextFill(Color.WHITE);

                // Create icons for rename, remove, and duplicate
                Button renameButton = createIconButton("Rename", "R");
                Button starButton = createIconButton("Marked As Star", "S");
                Button deleteButton = createIconButton("Remove", "C");

                // Arrange the label and buttons in an HBox
                HBox taskLabelContainer = new HBox(10, taskLabel, renameButton, starButton, deleteButton);
                taskLabelContainer.setAlignment(Pos.CENTER_LEFT);
                taskLabelContainer.setStyle("-fx-background-color: #444444; -fx-padding: 10; -fx-border-radius: 5;"); // Task container styling

                // Action for Rename Task
                renameButton.setOnAction(ev -> renameTask(taskLabel));

                // Action for Remove Task
                deleteButton.setOnAction(ev -> deleteTask(taskList, taskLabelContainer));
                
                // Action for Duplicate Task
                starButton.setOnAction(ev -> markTaskAsImportant(taskLabel.getText(),starButton));

                // Add the task label container to the task list
                taskList.getChildren().add(taskLabelContainer);

                // Clear the task input field
                taskInput.clear();
            }
        });

        // Task input area (TextField and Button combined)
        HBox inputArea = createMergedTaskInputArea(taskList);

        VBox.setVgrow(taskScroll, Priority.ALWAYS); // Task list grows to fill space
        myDayPanel.getChildren().addAll(label, taskScroll, inputArea);

        return myDayPanel;
    }

  

    private void markTaskAsImportant(String taskLabel,Button starButton) {
        if (starButton.getText()=="ST") {
            starButton.setText("S"); // Change the button text back to "S"
        } else {
            starButton.setText("ST"); // Change the button text to "ST"
        }
    }


    private Button createIconButton(String tooltipText, String iconText) {
        Button button = new Button(iconText);
        button.setStyle(
                "-fx-background-color: transparent; " +
                        "-fx-font-size: 14px; " + // Font size for the icon
                        "-fx-text-fill: #FFFFFF; " + // Default text color
                        "-fx-border-radius: 15; " + // Rounded corners
                        "-fx-padding: 5; " // Padding for click area
        );

        // Hover effect
        button.setOnMouseEntered(e -> button.setStyle(
                "-fx-background-color: #444444; " + // Slightly darker background
                        "-fx-font-size: 14px; " +
                        "-fx-text-fill: #FFFFFF; " +
                        "-fx-border-radius: 15; " +
                        "-fx-padding: 5;"
        ));

        button.setOnMouseExited(e -> button.setStyle(
                "-fx-background-color: transparent; " +
                        "-fx-font-size: 14px; " +
                        "-fx-text-fill: #FFFFFF; " +
                        "-fx-border-radius: 15; " +
                        "-fx-padding: 5;"
        ));

        // Add tooltip
        Tooltip tooltip = new Tooltip(tooltipText);
        Tooltip.install(button, tooltip);

        return button;
    }

    private HBox createMergedTaskInputArea(VBox taskList) {
        // Styled TextField
        TextField taskInput = new TextField();
        taskInput.setPromptText("Enter Tasks");
        taskInput.setStyle(
                "-fx-background-color: #1E1E1E; " +
                        "-fx-text-fill: white; " +
                        "-fx-prompt-text-fill: #AAAAAA; " +
                        "-fx-background-radius: 20 0 0 20; " + // Rounded left corners
                        "-fx-border-radius: 20 0 0 20; " +
                        "-fx-padding: 10 20; " +
                        "-fx-border-color: #444444; " +
                        "-fx-border-width: 1;"
        );

        

        // Styled Button
        Button addTaskButton = new Button("Add Task");
        addTaskButton.setStyle(
                "-fx-background-color: #2E2E2E; " +
                        "-fx-text-fill: white; " +
                        "-fx-background-radius: 0 20 20 0; " + // Rounded right corners
                        "-fx-border-radius: 0 20 20 0; " +
                        "-fx-padding: 10 20; " +
                        "-fx-border-color: #444444; " +
                        "-fx-border-width: 1;"
        );

        addTaskButton.setOnAction(e -> addTaskToList(taskList, taskInput));
        taskInput.setOnAction(e -> addTaskToList(taskList, taskInput));

        // Layout: Merge the TextField and Button
        HBox inputArea = new HBox(taskInput, addTaskButton);
        inputArea.setStyle("-fx-background-color: transparent; -fx-padding: 10;");
        inputArea.setAlignment(Pos.CENTER); // Center-align the elements
        return inputArea;
    }
    private void addTaskToList(VBox taskList, TextField taskInput) {
        String task = taskInput.getText().trim();
        if (!task.isEmpty()) {
            // Create a task label with a task number
            int taskNumber = taskList.getChildren().size() + 1; // Task number based on the current task count
            Label taskLabel = new Label(taskNumber + ". " + task);
            taskLabel.setFont(new Font("Arial", 16));
            taskLabel.setTextFill(Color.WHITE);
    
            // Create icons for rename, remove, and duplicate
            Button renameButton = createIconButton("Rename", "R");
            Button deleteButton = createIconButton("Remove", "C");
            Button starButton = createIconButton("Mark As Important", "S");
    
            // Create a container for the task label
            HBox taskLabelContainer = new HBox(10, taskLabel);
            taskLabelContainer.setAlignment(Pos.CENTER_LEFT);
            taskLabelContainer.setStyle("-fx-background-color: #444444; -fx-padding: 10; -fx-border-radius: 5;");
    
            // Create a container for the buttons
            HBox buttonContainer = new HBox(10, renameButton, starButton, deleteButton);
            buttonContainer.setAlignment(Pos.CENTER_RIGHT);
            buttonContainer.setStyle("-fx-padding: 10;");
    
            // Set the task label to grow to the left, and the buttons to grow to the right
            HBox fullTaskContainer = new HBox();
            HBox.setHgrow(taskLabelContainer, Priority.ALWAYS); // Allow task label to grow
            fullTaskContainer.getChildren().addAll(taskLabelContainer, buttonContainer);
    
            // Align the full task container in the center
            fullTaskContainer.setAlignment(Pos.CENTER_LEFT);
            fullTaskContainer.setStyle("-fx-background-color: #444444; -fx-padding: 10; -fx-border-radius: 5;");
    
            // Add actions for the buttons
            renameButton.setOnAction(ev -> renameTask(taskLabel));
            deleteButton.setOnAction(ev -> deleteTask(taskList, fullTaskContainer));
            starButton.setOnAction(ev -> markTaskAsImportant(taskLabel.getText(), starButton));


    
            // Add the full task container to the task list
            taskList.getChildren().add(fullTaskContainer);
    
            // Clear the task input field
            taskInput.clear();
        }
    }
    
    
    
    

    private void renameTask(Label taskLabel) {
        TextInputDialog dialog = new TextInputDialog(taskLabel.getText());
        dialog.setTitle("Rename Task");
        dialog.setHeaderText("Enter a new name for the task.");
        dialog.showAndWait().ifPresent(newName -> taskLabel.setText(newName));
    }

    private void deleteTask(VBox taskList, HBox taskLabelContainer) {
        taskList.getChildren().remove(taskLabelContainer);
    }



   
    private void switchToPanel(String panelName) {
        mainPanel.getChildren().clear();
        if (panelName.equals("My Day")) {
            mainPanel.getChildren().add(createMyDayPanel());

        } else if (panelName.equals("Important")) {
            System.out.println("Its Equals Imp and Register's to VM");
        } else if (panelName.equals("Obj")) {
            System.out.println("Its Equals Obj And Register's to VM");
        }
    }
    private void changeBackground(Stage primaryStage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.bmp", "*.gif"));
        File file = fileChooser.showOpenDialog(primaryStage);
        if (file != null) {
            primaryStage.getScene().setFill(Color.TRANSPARENT);
        }
    }
    // JavaFX.Merged(){}
    private void createNewGroup() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New Group");
        dialog.setHeaderText("Create a New Group");
        dialog.setContentText("Group Name:");
        
        dialog.showAndWait().ifPresent(groupName -> {
            if (!groupName.isEmpty() && !groups.containsKey(groupName)) {
                Button groupButton = createRoundedButton(groupName);
                groupButton.setOnAction(e -> switchToPanel(groupName));
                groupsContainer.getChildren().add(groupButton);

                VBox groupPanel = new VBox(10);
                groupPanel.setPadding(new Insets(10));
                groupPanel.setStyle("-fx-background-color: #232323;");
                groups.put(groupName, groupPanel);
            }
        });
    }
}
