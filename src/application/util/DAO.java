package application.util;

import java.sql.*;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

/**
 * This module interfaces with the database and connects the model to the view_controller
 */
public abstract class DAO extends DBC{
    /*  ======================
        Initialize Data Structure
        ======================*/

     // ObservableList<Record> allRecords = FXCollections.observableArrayList(); // TODO: change to work with DB
        ResultSet rs;


    /* ======================
        EXECUTE QUERY LAMBDA
       ======================*/

    /**
     * goes through a list of objects to use as arguments in a prepared statement
     *
     * @param statement the prepared statement that will be executed
     * @param arguments the arguments to use with the prepared statement
     * @throws SQLException any exception that occurs when setting the arguments
     */
    private void setArguments(PreparedStatement statement, List<Object> arguments) throws SQLException {
        if (arguments != null) {
            for (int i = 0; i < arguments.size(); i++) {
                statement.setObject(i + 1, arguments.get(i));
            }
        }
    }

    protected <T> T executeQuery(String query, BiFunction<SQLException, ResultSet, T> handler) {
        // lambda to consume an exception and result set and allow for DRY resource cleanup
        return executeQuery(query, null, (ex, rs) -> (T) handler.apply(ex, rs));
    }

    protected void executeQuery(String query, BiConsumer<SQLException, ResultSet> handler) {
        // lambda to consume an exception and result set and allow for DRY resource cleanup
        executeQuery(query, null, (ex, rs) -> {
            handler.accept(ex, rs);
            return null;
        });
    }

    protected void executeQuery(String query, List<Object> arguments, BiConsumer<SQLException, ResultSet> handler) {
        // lambda to consume an exception and result set and allow for DRY resource cleanup
        executeQuery(query, arguments, (ex, rs) -> {
            handler.accept(ex, rs);
            return null;
        });
    }

    protected <T> T executeQuery(String query, List<Object> arguments, BiFunction<SQLException, ResultSet, T> handler) {
        try (var stmt = getConnection().prepareStatement(query)) {
            setArguments(stmt, arguments);

            try (var rs = stmt.executeQuery()) {
                return handler.apply(null, rs);
            }
        } catch (SQLException ex) {
            printSQLException(ex);
            return handler.apply(ex, null);
        }
    }

    /* ======================
        ADDERS
       ======================*/

    // add new record
    public void executeInsert(String query, List<Object> arguments, BiConsumer<SQLException, Long> handler) {
        try (
                Connection connection = getConnection();
                PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)
        ) {
            setArguments(stmt, arguments);
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    handler.accept(null, generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
        } catch (SQLException ex) {
            //printSQLException(ex);
            handler.accept(ex, null);
        }
    }


    /* ======================
        UPDATERS
       ======================*/

    protected void executeUpdate(String query, List<Object> arguments, BiConsumer<SQLException, Integer> handler) {
        // lambda to consume an exception and result set and allow for DRY resource cleanup
        executeUpdate(query, arguments, ((BiFunction<SQLException, Integer, Void>) (ex, updates) -> {
            handler.accept(ex, updates);
            return null;
        }));
    }

    protected <T> T executeUpdate(String query, List<Object> arguments, BiFunction<SQLException, Integer, T> handler) {
        try (
                Connection connection = getConnection();
                PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)
        ) {
            setArguments(stmt, arguments);

            int affectedRows = stmt.executeUpdate();
            return handler.apply(null, affectedRows);
        } catch (SQLException ex) {
            printSQLException(ex);
            return handler.apply(ex, null);
        }
    }

    /* ======================
        Exception Handling
        ======================*/

    protected void printSQLException(SQLException ex) {
        System.out.println("SQLException: " + ex.getMessage());
        System.out.println("SQLState: " + ex.getSQLState());
        System.out.println("VendorError: " + ex.getErrorCode());
    }

}
