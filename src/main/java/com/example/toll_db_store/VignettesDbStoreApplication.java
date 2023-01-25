package com.example.toll_db_store;


import com.example.toll_core.listvignettaforvehicle.storage.VignettesStorage;
import com.example.toll_core.listvignettaforvehicle.storage.VignetteStorageDAO;
import com.example.toll_core.listvignettaforvehicle.entity.MotorwayVignetteEntity;
import com.example.toll_core.listvignettaforvehicle.enums.MotorWayVignetteType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class VignettesDbStoreApplication implements VignettesStorage {

    @Override
    public List<VignetteStorageDAO> findVignettesByRegistrationNumber(String registrationNumber) {
        List<VignetteStorageDAO> result;
        //result = selectItem(registrationNumber);
        result = selectItemMock(registrationNumber);
        return result;
    }

    Connection conn;
    private String SQL_TABLE = "vignette";
    private String KEY_NAME = "name";
    private String KEY_QUANTITY = "quantity";
    private Map<String,String> KeyTypePairs = Map.of(
                "id","INTEGER AUTOINCREMENT",
            "validFrom", "Date",
            "validTo", "Date",
            "valid", "Boolean",
            "dateOfPurchase", "Date",
            "price", "Float",
            "motorWayVignetteType", "varchar(255)",
            "vehicleCategory", "varchar(255)");
    private String dbURL = "jdbc:sqlite::memory:";

    public VignettesDbStoreApplication() {
        connect();
        createTable();
    }

    private void createTable() {
        executeUpdate("CREATE TABLE IF NOT EXISTS "+SQL_TABLE+"(" + KEY_NAME +" varchar(30)  PRIMARY KEY," + KEY_QUANTITY + " INT);");
    }
    private void dropTable() {
        executeUpdate("drop table if exists "+SQL_TABLE);
    }

    private void executeUpdate(String command) {
        if (conn != null) {
            Statement stmt = null;
            try {
                stmt = conn.createStatement();
                stmt.executeUpdate(command);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public List<VignetteStorageDAO> selectItemMock(String registrationNumber) {
        List<VignetteStorageDAO> result = new ArrayList<>();
        VignetteStorageDAO vignettesStorageDAO= new VignetteStorageDAO();
        MotorwayVignetteEntity vignette= new MotorwayVignetteEntity();

        vignette.setMotorWayVignetteType(MotorWayVignetteType.TenDays);
        vignette.setPrice(1534.15F);
        vignette.setDateOfPurchase(new Date(System.currentTimeMillis()));
        vignette.setValidFrom(new Date(System.currentTimeMillis()));
        vignette.setValidTo(new Date(System.currentTimeMillis()));
        vignette.setVehicleCategory("D1");

        vignettesStorageDAO.setVignette(vignette);
        vignettesStorageDAO.setLastSyncDateTime(new Date(System.currentTimeMillis()));
        result.add(vignettesStorageDAO);
        return result;
    }


    public List<VignetteStorageDAO> selectItem(String registrationNumber) {
        List<VignetteStorageDAO> result = null;
        String actName;
        int actQuantity;
        if (conn != null) {
            try {
                Statement Stmt = conn.createStatement();
                ResultSet rs = Stmt.executeQuery(
                        "select * from "+SQL_TABLE+" where "+KEY_NAME + "='"+registrationNumber+"'");
                if (rs.next()) {
                    actName = rs.getString(KEY_NAME);
                    actQuantity = rs.getInt(KEY_QUANTITY);
                    result.add(new VignetteStorageDAO());
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }




    private void printDatabaseMetaData() throws SQLException {
        DatabaseMetaData dm = (DatabaseMetaData) conn.getMetaData();
        System.out.println("Driver name: " + dm.getDriverName());
        System.out.println("Driver version: " + dm.getDriverVersion());
        System.out.println("Product name: " + dm.getDatabaseProductName());
        System.out.println("Product version: " + dm.getDatabaseProductVersion());
    }

    private void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection(dbURL);
			printDatabaseMetaData();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

// only persist database
	private void close()
	{
		try {
			conn.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}


}
