package com.licenta.andrisan.easychoice.services;

import com.licenta.andrisan.easychoice.database.DatabaseConnection;
import oracle.jdbc.OracleTypes;
import org.springframework.stereotype.Service;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

@Service
public class FacilityService {

    public LinkedList<String> getAllFacilities() throws SQLException, ClassNotFoundException {
        LinkedList<String> facilities = new LinkedList<>();

        String runOracleFunction = "{ ? = call get_all_facilities }";
        DatabaseConnection conn = new DatabaseConnection();

        try {
            CallableStatement callableStatement = conn.getOracleConnection().prepareCall(runOracleFunction);
            callableStatement.registerOutParameter(1, OracleTypes.CURSOR);
            callableStatement.execute();

            ResultSet resultSet = (ResultSet) callableStatement.getObject(1);
            while (resultSet.next()) {
                facilities.add(resultSet.getString("facility_name"));
            }

            resultSet.close();
            callableStatement.close();
        }catch (SQLException e) {
            e.printStackTrace();
        }
        conn.DisconnectFromDatabase();
        return facilities;
    }

    public LinkedList<String> getHouseFacilities(int houseID) throws SQLException, ClassNotFoundException {
        LinkedList<String> facilities = new LinkedList<>();
        String runOracleFunction = "{ ? = call get_house_facilities(?) }";
        DatabaseConnection conn = new DatabaseConnection();

        try {
            CallableStatement callableStatement = conn.getOracleConnection().prepareCall(runOracleFunction);
            callableStatement.registerOutParameter(1, OracleTypes.CURSOR);
            callableStatement.setInt(2, houseID);
            callableStatement.execute();

            ResultSet resultSet = (ResultSet) callableStatement.getObject(1);
            while (resultSet.next()) {
                facilities.add(resultSet.getString("facility_name"));
            }
            resultSet.close();
            callableStatement.close();
        }catch (SQLException e) {
            e.printStackTrace();
        }

        conn.DisconnectFromDatabase();
        return facilities;
    }

    public void addHouseFacilities(int houseID, String[] facilities) throws SQLException, ClassNotFoundException {
        String runStoredProcedure = "{ call licenta_house_pkg.insert_house_facilities(?, ?) }";
        DatabaseConnection conn = new DatabaseConnection();
        try {
            for(String facility : facilities) {
                CallableStatement callableStatement = conn.getOracleConnection().prepareCall(runStoredProcedure);
                callableStatement.setInt(1, houseID);
                callableStatement.setString(2, facility);
                callableStatement.execute();
                callableStatement.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        conn.DisconnectFromDatabase();
    }

}
