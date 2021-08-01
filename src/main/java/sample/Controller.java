package sample;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class Controller {

    public static final String c3 = "VARCHAR";  // Size Required
    private static final String c1 = "INTEGER SMALLINT BIGINT FLOAT DOUBLE DECIMAL CHAR";   // Size Optional
    private static final String c2 = "DATE TIME DATETIME YEAR E NUM";    // Size Don't Know
    // ---- Top Layout ----
    public ChoiceBox<String> choice_db_select;
    public ChoiceBox<String> choice_tb_select;
    public Button bt_refresh;
    public ListView<String> listview_columns;
    // ---- Middle Layout ----
    public TabPane tab_pane;
    public Tab tab_1;
    public Tab tab_2;
    public Tab tab_3;
    public Tab tab_4;
    public Tab tab_5;
    // -- Tab 1 --
    public TextField txfl_dbname;
    public Button bt_db_create;
    public Button bt_db_delete;
    // -- Tab 2 --
    public TextField txfl_tb_new_name;
    public TextField txfl_tb_new_column_name;
    public TextField txfl_tb_new_column_size;
    public ChoiceBox<String> choice_column_data_type;
    public ListView<String> listview_new_columns;
    public Button bt_tb_create;
    public Button bt_submit_column;
    public Button bt_tb_delete_sel_col;
    public Button bt_tb_delete_data;
    public Button bt_tb_delete_all;
    // -- Tab 3 --
    public TextField txfl_columns;
    public Label lb_columns_format;
    public Button bt_tb_view_table_all;
    public Button bt_tb_view_table_selected;
    // -- Tab 4 --
    public TextArea txar_insert_data;
    public Button bt_insert_data;
    // ---- Bottom Layout ----
    public ListView<String> listview_log;
    // ---- COMMON ----
    private DatabaseManager dbm;
    private String current_db;
    private String current_tb;
    private String currentDataType;
    private String newColumnSize;
    private ObservableList<String> newColumnsArray, logs;

    @FXML
    public void initialize() {
        choice_tb_select.setDisable(true);
        disableItems(true, true, true, true, true, true);

        // Open Database Connection
        dbm = DatabaseManager.getInstance();
        if (dbm.openConnection()) {
            AlertDialog.createDialog("Connection Error", "Error !");
            Platform.exit();
        } else {
            showDatabases();
            showDataTypes();
            choice_db_select.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                current_db = newValue;
                onDBSelected();
            });
            choice_tb_select.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                current_tb = newValue;
                onTBSelected();
            });
            choice_column_data_type.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                currentDataType = newValue;
                //getWhichDataTypeRequired();
            });
        }
    }

    /*
        -------- TOP LAYOUT --------
     */
    /*
        Show All Databases
     */
    public void showDatabases() {
        showLog("Showing Databases.");
        List<String> databases = dbm.getDatabases();
        choice_db_select.getItems().setAll(databases);
    }

    /*
        Set Data Types to ChoiceBox
     */
    public void showDataTypes() {
        showLog("Showing Data Types");
        List<String> dataTypes = dbm.getDataTypes();
        choice_column_data_type.getItems().setAll(dataTypes);
    }

    /*
        Refresh Window
     */
    public void onRefresh() {
        showLog("Refreshing List");
        choice_db_select.getItems().clear();
        choice_tb_select.getItems().clear();
        choice_tb_select.setDisable(true);
        listview_columns.getItems().clear();
        disableItems(true, true, true, true, true, true);
        showDatabases();
    }

    /*
        Selecting a Database, After for that
     */
    public void onDBSelected() {
        if (current_db == null || current_db.isEmpty()) {
            return;
        }
        choice_tb_select.getItems().clear();
        List<String> tables = dbm.getTables(current_db);
        for (String table : tables) {
            choice_tb_select.getItems().add(table);
        }
        choice_tb_select.setDisable(false);
        disableItems(false, false, false, true, true, true);
    }

    /*
        Selecting a Table, After for that
     */
    public void onTBSelected() {
        if (current_tb == null || current_tb.isEmpty()) {
            //showLog("Please Select a Table.");
            return;
        }
        listview_columns.getItems().clear();
        ObservableList<String> columns = dbm.getColumns(current_db, current_tb);
        listview_columns.getItems().setAll(columns);

        disableItems(false, false, false, false, false, false);
    }

    /*
        -------- MIDDLE LAYOUT --------
        ---- TAB 1 ----
     */
    /*
        Creating a Database
     */
    @FXML
    public void onCreateDatabase() {
        String newDbName = txfl_dbname.getText().trim().toLowerCase();
        dbm.createDatabase(newDbName);
        showLog("Database Created.");
        onRefresh();
    }

    /*
        Deleting a Database
     */
    @FXML
    public void onDeleteDatabase() {
        dbm.deleteDatabase(current_db);
        showLog("Database Deleted");
        onRefresh();
    }

    /*
        ---- TAB 2 ----
     */
    /*
        Add a new Column to View
     */
    @FXML
    public void onSubmitTbColumn() {
        String newColumnName = txfl_tb_new_column_name.getText().trim().toLowerCase();

        if (newColumnName.isEmpty()) {
            showLog("Please add a Column Name.");
            return;
        } else if (currentDataType == null || currentDataType.isEmpty()) {
            showLog("Please Select a Data Type.");
            return;
        }
        switch (getWhichDataTypeRequired()) {
            case 0:
                newColumnSize = null;
                break;
            case 1:
                newColumnSize = getColumnSize();
                break;
            case 2:
                if (getColumnSize() == null) {
                    showLog("Please add Valid Number");
                    return;
                }
                else {
                    newColumnSize = getColumnSize();
                }
                break;
        }
        if (newColumnsArray == null || newColumnsArray.isEmpty()) {
            newColumnsArray = FXCollections.observableArrayList();
        }
        if (newColumnSize == null) {
            newColumnsArray.add("`" + newColumnName + "` " + currentDataType);
        } else {
            newColumnsArray.add("`" + newColumnName + "` " + currentDataType + "(" + newColumnSize + ")");
        }
        listview_new_columns.setItems(newColumnsArray);
        showLog("Column added");
    }

    /*
        Delete a Table Column from View
     */
    @FXML
    public void onDeleteTbColumn() {
        int index = listview_new_columns.getSelectionModel().getSelectedIndex();
        if (index < 0 || index > listview_new_columns.getItems().size()) {
            return;
        }
        listview_new_columns.getItems().remove(index);
    }

    /*
        Create New Table
     */
    @FXML
    public void onCreateTable() {
        String newTableName = txfl_tb_new_name.getText().trim().toLowerCase();
        if (newTableName.isEmpty()) {
            showLog("Please add a Table name");
            return;
        }
        if (newColumnsArray.isEmpty()) {
            showLog("Please Add Some Columns.");
            return;
        }
        dbm.createTable(current_db, newTableName, newColumnsArray);
        showLog("Table Created.");
    }

    /*
        Check Which Data Type Required
     */
    public int getWhichDataTypeRequired() {
        // requiredIntOptional, requiredInt, requiredNothing = 1, 2, 0
        if (c1.contains(currentDataType)) {
            return 1;
        } else if (c2.contains(currentDataType)) {
            return 0;
        } else if (c3.contains(currentDataType)) {
            return 2;
        }
        return 0;
    }

     /*
        ---- TAB 3 ----
      */
    /*
        View Full Table
     */
    @FXML
    public void onViewTableAll() {
        dbm.viewTable(current_db, current_tb);
    }

    /*
        View Selected Columns
     */
    public void onViewTableSelected() {
        dbm.viewTableSelected(current_db, current_tb, txfl_columns.getText().trim());
    }

    /*
        ---- TAB 4 ----
     */

    @FXML
    public void onClickInsertData() {
        String data = txar_insert_data.getText().trim();
        dbm.insertData(current_db,current_tb, data);
        /*String[] dataLines = data.split("\n");
        System.out.println(data);
        for (String a : dataLines) {
            System.out.println(a + "\n");
        }*/

    }

    /*
        COMMON
     */
    @FXML
    private void showLog(String msg) {
        if (logs == null || logs.isEmpty()) {
            logs = FXCollections.observableArrayList();
        }
        logs.add(msg + "\n");
        listview_log.setItems(logs);
    }


    public void disableItems(boolean... state) {
        try {
            tab_pane.setDisable(state[0]);
            tab_1.setDisable(state[1]);
            tab_2.setDisable(state[2]);
            tab_3.setDisable(state[3]);
            tab_4.setDisable(state[4]);
            tab_5.setDisable(state[5]);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public String getColumnSize() {
        try {
            if (txfl_tb_new_column_size == null || txfl_tb_new_column_size.getText().isEmpty()) {
                return null;
            }
            return Integer.toString(Integer.parseInt(txfl_tb_new_column_size.getText()));
        } catch (NumberFormatException e) {
            showLog(e.getMessage());
        }
        return null;
    }

    public void exitApplication() {
        Platform.exit();
    }

}
