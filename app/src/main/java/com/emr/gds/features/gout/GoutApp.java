package com.emr.gds.features.gout;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class GoutApp extends Application {

    @Override
    public void start(Stage stage) {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-font-family: 'Segoe UI', sans-serif;");

        Label title = new Label("통풍(Gout) 진단 점수 계산기 (2015 ACR/EULAR)");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // 0. 확정적 진단 (Sufficient Criterion)
        CheckBox cbMsu = new CheckBox("증상 관절/점액낭에서 요산 결정(MSU) 확인됨");
        cbMsu.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");

        // 1. 임상적 기준
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        // 관절 침범
        grid.add(new Label("관절 침범:"), 0, 0);
        ComboBox<String> comboJoint = new ComboBox<>();
        comboJoint.getItems().addAll("해당 없음 (0점)", "발목 또는 발등 (1점)", "엄지발가락 MTP1 (2점)");
        comboJoint.getSelectionModel().selectFirst();
        grid.add(comboJoint, 1, 0);

        // 증상 특징
        grid.add(new Label("임상 특징 (발적, 압통, 보행장애):"), 0, 1);
        ComboBox<Integer> comboFeatures = new ComboBox<>();
        comboFeatures.getItems().addAll(0, 1, 2, 3);
        comboFeatures.getSelectionModel().selectFirst();
        grid.add(comboFeatures, 1, 1);

        // 시간적 양상
        grid.add(new Label("발작 양상:"), 0, 2);
        ComboBox<String> comboTime = new ComboBox<>();
        comboTime.getItems().addAll("없음 (0점)", "1회 전형적 발작 (1점)", "재발성 전형적 발작 (2점)");
        comboTime.getSelectionModel().selectFirst();
        grid.add(comboTime, 1, 2);

        // 통풍 결절
        CheckBox cbTophus = new CheckBox("통풍 결절(Tophus) 존재 (+4점)");
        grid.add(cbTophus, 1, 3);

        // 2. 검사실 기준
        grid.add(new Label("혈청 요산 농도 (mg/dL):"), 0, 4);
        ComboBox<String> comboUrate = new ComboBox<>();
        comboUrate.getItems().addAll("< 4 (-4점)", "4 ~ < 6 (0점)", "6 ~ < 8 (2점)", "8 ~ < 10 (3점)", "≥ 10 (4점)");
        comboUrate.getSelectionModel().select(1);
        grid.add(comboUrate, 1, 4);

        CheckBox cbFluidNeg = new CheckBox("관절액 검사 요산 결정 미검출 (-2점)");
        grid.add(cbFluidNeg, 1, 5);

        // 3. 영상 기준
        CheckBox cbImaging = new CheckBox("초음파 이중윤곽 또는 DECT 요산 확인 (+4점)");
        CheckBox cbErosion = new CheckBox("X-ray 골미란 확인 (+4점)");
        grid.add(cbImaging, 1, 6);
        grid.add(cbErosion, 1, 7);

        // 결과 영역
        Button btnCalc = new Button("점수 계산");
        btnCalc.setStyle("-fx-base: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold;");
        Label lblResult = new Label("결과: -");
        lblResult.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        btnCalc.setOnAction(e -> {
            if (cbMsu.isSelected()) {
                lblResult.setText("결과: 확정적 통풍 (Sufficient Criterion 충족)");
                lblResult.setStyle("-fx-text-fill: red; -fx-font-size: 16px; -fx-font-weight: bold;");
                return;
            }

            int score = 0;
            score += comboJoint.getSelectionModel().getSelectedIndex();
            score += comboFeatures.getValue();
            score += comboTime.getSelectionModel().getSelectedIndex();
            if (cbTophus.isSelected()) score += 4;

            int urateIdx = comboUrate.getSelectionModel().getSelectedIndex();
            if (urateIdx == 0) score -= 4;
            else if (urateIdx == 2) score += 2;
            else if (urateIdx == 3) score += 3;
            else if (urateIdx == 4) score += 4;

            if (cbFluidNeg.isSelected()) score -= 2;
            if (cbImaging.isSelected()) score += 4;
            if (cbErosion.isSelected()) score += 4;

            String diagnosis = (score >= 8) ? "[통풍 분류 가능]" : "[통풍 아님]";
            lblResult.setText(String.format("총점: %d점 - %s", score, diagnosis));
            lblResult.setStyle(score >= 8 ? "-fx-text-fill: red;" : "-fx-text-fill: black;");
        });

        root.getChildren().addAll(title, cbMsu, new Separator(), grid, btnCalc, lblResult);

        Scene scene = new Scene(root, 500, 550);
        stage.setTitle("Gout Diagnosis Calculator (ACR/EULAR)");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}