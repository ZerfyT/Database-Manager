package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DatabaseManager {
    public static final String DB_URL = "jdbc:mysql://localhost";
    public static final String USER_NAME = "nipun";
    public static final String PASSWD = "root";
    private static final String TAG = "DatabaseManager";
    private static final DatabaseManager instance = new DatabaseManager();
    public static Connection conn;
    public static Statement stmt;
    private static boolean isConnFailed = false;

    private DatabaseManager() {
    }

    public static DatabaseManager getInstance() {
        return instance;
    }

    public static void showLog(String msg) {
        System.out.println(TAG + " - " + msg);
    }

    /*
        Open Database Connection
     */
    public boolean openConnection() {
        try {
            showLog("Connecting Database...");
            conn = DriverManager.getConnection(DB_URL, USER_NAME, PASSWD);
            showLog("Creating Statement...");
            if (conn != null) {
                stmt = conn.createStatement();
            }
        } catch (SQLException | NullPointerException e1) {
            isConnFailed = true;
            showLog("Please Check The Database Server Connection.");
        }
        return isConnFailed;
    }

    /*
        Close Database Connection
     */
    public void closeConnection() {
        showLog("Closing Connection");
        try {
            if (stmt != null) {
                stmt.close();
                showLog("Closed Statement");
            }
            if (conn != null) {
                conn.close();
                showLog("Closed Connection");
            }
        } catch (SQLException ignored) {
        }
    }

    /*
        Get All Databases
     */
    public List<String> getDatabases() {
        showLog("Getting Databases");
        String sql = "SHOW DATABASES;";
        return executeQuery(sql);
    }

    /*
        Get All Tables in Database
     */
    public List<String> getTables(String db) {
        showLog("Getting Tables");
        String sql = "SHOW TABLES IN " + db + ";";
        return executeQuery(sql);
    }

    /*
        Get All Columns in Table
     */
    public ObservableList<String> getColumns(String db, String tb) {
        ObservableList<String> columns = FXCollections.observableArrayList();
        String sql = "SELECT * FROM " + db + "." + tb + ";";
        try {
            ResultSet rs = stmt.executeQuery(sql);
            ResultSetMetaData rm = rs.getMetaData();
            for (int i = 1; i <= rm.getColumnCount(); i++) {
                columns.add(rm.getColumnName(i));
            }
        } catch (SQLException e) {
            showLog(e.getMessage());
        }
        return columns;
    }

    /*
        Create Database
     */
    public void createDatabase(String db) {
        String sql = "CREATE DATABASE " + db + ";";
        execute(sql);
        showLog("Database Created");
    }

    /*
        Delete Database
     */
    public void deleteDatabase(String db) {
        String sql = "DROP DATABASE " + db + ";";
        execute(sql);
        showLog("Database Deleted");
    }

    /*
        Create Table
     */
    public void createTable(String db, String tb, ObservableList<String> columns) {
        StringBuilder sql = new StringBuilder("CREATE TABLE ");
        sql.append("`").append(db).append("`").append(".").append("`").append(tb).append("`").append("(");
        int count = 0;
        for (String column : columns) {
            sql.append(column);
            count += 1;
            if (count < columns.size()) {
                sql.append(",");
            }
        }
        sql.append(") ENGINE = InnoDB;");
        System.out.println(sql);
        try {
            stmt.executeUpdate(sql.toString());
//            String newSQL = "CREATE TABLE `mydatabase`.`ghjk` ( `yry` INT(6) NOT NULL , `ddf` VARCHAR(10) NOT NULL ) ENGINE = InnoDB;";
        } catch (SQLException e) {
            showLog(e.getMessage());
        }
    }

    /*
        View Full Table Window
     */
    public void viewTable(String db, String tb) {
        String sql = "SELECT * FROM `" + db + "`.`" + tb + "`";
        Table t = new Table(tb, sql);
        t.createTableView();
    }

    public void viewTableSelected(String db, String tb, String columns) {
        String[] columnArray = columns.split(",");
        //List<String> columnArray = new ArrayList<>(Arrays.asList(columns.split(",")));
        StringBuilder col = new StringBuilder();
        int count = 0;
        for (String column : columnArray) {
            col.append("`").append(column).append("`");
            count += 1;
            if (count < columnArray.length) {
                col.append(",");
            }
        }
        StringBuilder sql = new StringBuilder("SELECT ");
        System.out.println(col);
        sql.append(col).append(" FROM ").append(db).append(".").append(tb);
        Table t = new Table(tb, sql.toString());
        System.out.println(sql);
        t.createTableView();
    }

    public void insertData(String db, String tb, String data) {
        StringBuilder sql = new StringBuilder("INSERT INTO ");
        sql.append("`").append(db).append("`.`").append(tb).append("` VALUES(");
    }

    private void execute(String sql) {   // for CREATE / DROP DATABASE,
        try {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private List<String> executeQuery(String sql) {  // for SHOW DATABASES / TABLES,
        List<String> list = new ArrayList<>();
        try {
            ResultSet r1 = stmt.executeQuery(sql);
            while (r1.next()) {
                list.add(r1.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<String> getDataTypes() {
        List<String> dataTypes = new ArrayList<>();
        dataTypes.add("INTEGER");
        dataTypes.add("SMALLINT");
        dataTypes.add("BIGINT");
        dataTypes.add("FLOAT");
        dataTypes.add("DOUBLE");
        dataTypes.add("DECIMAL");
        dataTypes.add("DATE");
        dataTypes.add("TIME");
        dataTypes.add("DATETIME");
        dataTypes.add("YEAR");
        dataTypes.add("CHAR");
        dataTypes.add("VARCHAR");
        dataTypes.add("ENUM");
        return dataTypes;
    }
}
