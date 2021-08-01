package sample;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class Table {
    private String title;
    private String sql;
    private int column_count;
    private TableView<ObservableList> tableview;

    public Table(String tb, String sql) {
        this.title = tb;
        this.sql = sql;
    }

    public void createTableView() {
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.initModality(Modality.NONE);

        tableview = new TableView<>();
        buildData(sql);

        stage.setScene(new Scene(tableview));
        stage.setOnCloseRequest(event -> {
            stage.close();
        });
        stage.show();
    }

    private void buildData(String sql) {
        ObservableList<ObservableList> data = FXCollections.observableArrayList();
        try {
            ResultSet rs = DatabaseManager.stmt.executeQuery(sql);
            ResultSetMetaData rm = rs.getMetaData();
            column_count = rm.getColumnCount();
            for (int i = 0; i < column_count; i++) {
                final int j = i;
                // Create Columns Ony by One
                TableColumn col = new TableColumn(rm.getColumnName(i + 1));
                col.setCellValueFactory((Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>) param ->
                        new SimpleStringProperty(param.getValue().get(j).toString()));
                tableview.getColumns().addAll(col);
            }
            while (rs.next()) {
                // Iterate Row
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= column_count; i++) {
                    //Iterate Column
                    row.add(rs.getString(i));
                }
                data.add(row);
            }
            tableview.setItems(data);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
