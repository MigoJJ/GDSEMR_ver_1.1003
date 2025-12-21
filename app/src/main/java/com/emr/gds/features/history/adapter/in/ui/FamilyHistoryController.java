package com.emr.gds.features.history.adapter.in.ui;

import com.emr.gds.features.history.application.FamilyHistoryService;
import com.emr.gds.features.history.domain.ConditionCategory;
import com.emr.gds.input.IAITextAreaManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.util.Collections;
import java.util.Map;

public class FamilyHistoryController {

    @FXML private ComboBox<String> relationshipComboBox;
    @FXML private TextArea notesTextArea;
    
    @FXML private ListView<String> endocrineList;
    @FXML private ListView<String> cancerList;
    @FXML private ListView<String> cardioList;
    @FXML private ListView<String> geneticList;
    
    @FXML private TextField searchField;
    @FXML private TextArea historyTextArea;

    private IAITextAreaManager textAreaManager;
    private Map<String, String> abbrevMap;
    private FamilyHistoryService service;

    private ObservableList<String> endocrineData = FXCollections.observableArrayList();
    private ObservableList<String> cancerData = FXCollections.observableArrayList();
    private ObservableList<String> cardioData = FXCollections.observableArrayList();
    private ObservableList<String> geneticData = FXCollections.observableArrayList();

    public void setManagers(IAITextAreaManager manager, Map<String, String> abbrevMap, FamilyHistoryService service) {
        this.textAreaManager = manager;
        this.abbrevMap = (abbrevMap != null) ? abbrevMap : Collections.emptyMap();
        this.service = service;
        loadAllConditions();
    }

    @FXML
    public void initialize() {
        // Init ComboBox
        relationshipComboBox.setItems(FXCollections.observableArrayList(
                "Mother", "Father", "Sister", "Brother",
                "Grandmother", "Grandfather", "Aunt", "Uncle", "Cousin", "Child"
        ));

        // Bind Lists
        setupListView(endocrineList, endocrineData);
        setupListView(cancerList, cancerData);
        setupListView(cardioList, cardioData);
        setupListView(geneticList, geneticData);

        // Search
        searchField.textProperty().addListener((obs, old, val) -> filterLists(val));
        
        // Abbreviation expansion
        setupAbbreviation(notesTextArea);
        setupAbbreviation(historyTextArea);
    }

    private void setupListView(ListView<String> listView, ObservableList<String> data) {
        FilteredList<String> filtered = new FilteredList<>(data, p -> true);
        listView.setItems(filtered);
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    private void loadAllConditions() {
        if (service == null) return;

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                var endocrine = service.getConditions(ConditionCategory.ENDOCRINE);
                var cancer = service.getConditions(ConditionCategory.CANCER);
                var cardio = service.getConditions(ConditionCategory.CARDIOVASCULAR);
                var genetic = service.getConditions(ConditionCategory.GENETIC);

                Platform.runLater(() -> {
                    endocrineData.setAll(endocrine);
                    cancerData.setAll(cancer);
                    cardioData.setAll(cardio);
                    geneticData.setAll(genetic);
                });
                return null;
            }
        };
        new Thread(task).start();
    }

    private void filterLists(String filter) {
        String lower = filter.toLowerCase();
        updateFilter(endocrineList, lower);
        updateFilter(cancerList, lower);
        updateFilter(cardioList, lower);
        updateFilter(geneticList, lower);
    }

    @SuppressWarnings("unchecked")
    private void updateFilter(ListView<String> listView, String filter) {
        FilteredList<String> fl = (FilteredList<String>) listView.getItems();
        fl.setPredicate(s -> s.toLowerCase().contains(filter));
    }

    @FXML
    private void handleAddEntry() {
        String relationship = relationshipComboBox.getValue();
        if (relationship == null || relationship.isBlank()) {
            showAlert(Alert.AlertType.WARNING, "Input Required", "Please select a relationship.");
            return;
        }

        StringBuilder entry = new StringBuilder();
        entry.append(relationship).append(":\n");

        String notes = notesTextArea.getText().trim();
        if (!notes.isEmpty()) {
            entry.append("  Notes: ").append(notes).append("\n");
        }

        boolean hasCondition = false;
        hasCondition |= appendSelected(entry, "Endocrine", endocrineList);
        hasCondition |= appendSelected(entry, "Cancer", cancerList);
        hasCondition |= appendSelected(entry, "Cardiovascular", cardioList);
        hasCondition |= appendSelected(entry, "Genetic", geneticList);

        if (!hasCondition && notes.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "No Data", "Please select at least one condition or add notes.");
            return;
        }

        historyTextArea.appendText(entry.toString().trim() + "\n\n");
        clearFormInputs();
    }

    private boolean appendSelected(StringBuilder sb, String title, ListView<String> listView) {
        ObservableList<String> selected = listView.getSelectionModel().getSelectedItems();
        if (!selected.isEmpty()) {
            sb.append("  ").append(title).append(": ")
              .append(String.join("; ", selected)).append("\n");
            return true;
        }
        return false;
    }

    private void clearFormInputs() {
        relationshipComboBox.setValue(null);
        notesTextArea.clear();
        endocrineList.getSelectionModel().clearSelection();
        cancerList.getSelectionModel().clearSelection();
        cardioList.getSelectionModel().clearSelection();
        geneticList.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleAddCondition() {
        // Find focused list view to add to
        ListView<String> target = null;
        ConditionCategory category = null;
        
        if (endocrineList.isFocused()) { target = endocrineList; category = ConditionCategory.ENDOCRINE; }
        else if (cancerList.isFocused()) { target = cancerList; category = ConditionCategory.CANCER; }
        else if (cardioList.isFocused()) { target = cardioList; category = ConditionCategory.CARDIOVASCULAR; }
        else if (geneticList.isFocused()) { target = geneticList; category = ConditionCategory.GENETIC; }

        if (target == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Click on a condition list first (to give it focus).");
            return;
        }
        
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add New Condition");
        dialog.setHeaderText("Add to " + category);
        dialog.setContentText("Condition name:");
        
        ListView<String> finalTarget = target;
        ConditionCategory finalCategory = category;

        dialog.showAndWait().ifPresent(name -> {
            String trimmed = name.trim();
            if (!trimmed.isEmpty()) {
                // Async add to DB
                Task<Void> task = new Task<>() {
                    @Override
                    protected Void call() {
                        service.addCondition(finalCategory, trimmed);
                        return null;
                    }
                };
                task.setOnSucceeded(e -> { 
                     // Update UI
                    FilteredList<String> fl = (FilteredList<String>) finalTarget.getItems();
                    @SuppressWarnings("unchecked")
                    ObservableList<String> source = (ObservableList<String>) fl.getSource();
                    if (!source.contains(trimmed)) {
                        source.add(trimmed);
                    }
                });
                new Thread(task).start();
            }
        });
    }

    @FXML
    private void handleSaveLists() {
         showAlert(Alert.AlertType.INFORMATION, "Auto-Saved", "Conditions are now saved automatically to the database.");
    }

    @FXML
    private void handleClear() {
        historyTextArea.clear();
    }

    @FXML
    private void handleSaveToEMR() {
        if (textAreaManager == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "EMR Manager not connected.");
            return;
        }
        String text = historyTextArea.getText();
        if (text.trim().isEmpty()) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "History is empty. Save anyway?", ButtonType.YES, ButtonType.NO);
            if (confirm.showAndWait().orElse(ButtonType.NO) != ButtonType.YES) {
                return;
            }
        }
        
        textAreaManager.insertBlockIntoArea(IAITextAreaManager.AREA_PMH, text, true);
        showAlert(Alert.AlertType.INFORMATION, "Success", "Family History saved to EMR.");
        handleClose();
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) historyTextArea.getScene().getWindow();
        stage.close();
    }

    private void setupAbbreviation(TextArea ta) {
        ta.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.SPACE) {
                expandAbbreviation(ta);
            }
        });
    }

    private void expandAbbreviation(TextArea ta) {
        int caret = ta.getCaretPosition();
        String text = ta.getText();
        String upToCaret = text.substring(0, caret);
        int start = Math.max(upToCaret.lastIndexOf(' '), upToCaret.lastIndexOf('\n')) + 1;
        
        if (start >= caret) return; 
        
        String word = upToCaret.substring(start).trim();
        if (word.startsWith(":") && abbrevMap.containsKey(word.substring(1))) {
            String replacement = abbrevMap.get(word.substring(1));
            ta.deleteText(start, caret);
            ta.insertText(start, replacement + " ");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}