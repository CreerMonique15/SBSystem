
package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class DBconnector {
    
    //Connection Method to SQLITE
    public static Connection connectDB() {
        Connection con = null;
        try {
            Class.forName("org.sqlite.JDBC"); // Load the SQLite JDBC driver
            con = DriverManager.getConnection("jdbc:sqlite:Salon_BS.db"); // Establish connection

        } catch (Exception e) {
            System.out.println("Connection Failed: " + e);
        }
        return con;
    }
    
    public boolean addRecord(String sql, Object... values) {
        try (Connection conn = DBconnector.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Loop through the values and set them in the prepared statement dynamically
            for (int i = 0; i < values.length; i++) {
                if (values[i] instanceof Integer) {
                    pstmt.setInt(i + 1, (Integer) values[i]);
                } else if (values[i] instanceof Double) {
                    pstmt.setDouble(i + 1, (Double) values[i]);
                } else if (values[i] instanceof Float) {
                    pstmt.setFloat(i + 1, (Float) values[i]);
                } else if (values[i] instanceof Long) {
                    pstmt.setLong(i + 1, (Long) values[i]);
                } else if (values[i] instanceof Boolean) {
                    pstmt.setBoolean(i + 1, (Boolean) values[i]);
                } else if (values[i] instanceof java.util.Date) {
                    pstmt.setDate(i + 1, new java.sql.Date(((java.util.Date) values[i]).getTime()));
                } else if (values[i] instanceof java.sql.Date) {
                    pstmt.setDate(i + 1, (java.sql.Date) values[i]);
                } else if (values[i] instanceof java.sql.Timestamp) {
                    pstmt.setTimestamp(i + 1, (java.sql.Timestamp) values[i]);
                } else {
                    pstmt.setString(i + 1, values[i].toString());
                }
            }

            pstmt.executeUpdate();
            return true; // ✅ Return true if execution was successful

        } catch (SQLException e) {
            System.out.println("Error adding record: " + e.getMessage());
            return false; // ❌ Return false if exception occurred
        }
    }

    
    public void viewRecords(String sqlQuery, String[] columnHeaders, String[] columnNames) {
        if (columnHeaders.length != columnNames.length) {
            System.out.println("Error: Mismatch between column headers and column names.");
            return;
        }

        try (Connection conn = DBconnector.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sqlQuery);
             ResultSet rs = pstmt.executeQuery()) {

            // Step 1: Calculate max width per column
            int[] maxWidths = new int[columnNames.length];

            // Initialize maxWidths based on header length
            for (int i = 0; i < columnHeaders.length; i++) {
                maxWidths[i] = columnHeaders[i].length();
            }

            // Temporarily store all rows
            List<String[]> rows = new ArrayList<>();

            while (rs.next()) {
                String[] row = new String[columnNames.length];
                for (int i = 0; i < columnNames.length; i++) {
                    String value = rs.getString(columnNames[i]);
                    row[i] = value != null ? value : "";
                    if (row[i].length() > maxWidths[i]) {
                        maxWidths[i] = row[i].length();
                    }
                }
                rows.add(row);
            }

            // If no data found
            if (rows.isEmpty()) {
                System.out.println("\n⚠️  No records found.\n");
                return;
            }

            // Step 2: Build horizontal line
            StringBuilder divider = new StringBuilder();
            divider.append("+");
            for (int width : maxWidths) {
                for (int i = 0; i < width + 2; i++) {
                    divider.append("-");
                }
                divider.append("+");
            }

            // Step 3: Print headers
            System.out.println(divider);
            StringBuilder headerLine = new StringBuilder("|");
            for (int i = 0; i < columnHeaders.length; i++) {
                headerLine.append(" ").append(String.format("%-" + maxWidths[i] + "s", columnHeaders[i])).append(" |");
            }
            System.out.println(headerLine);
            System.out.println(divider);

            // Step 4: Print rows
            for (String[] row : rows) {
                StringBuilder rowLine = new StringBuilder("|");
                for (int i = 0; i < row.length; i++) {
                    rowLine.append(" ").append(String.format("%-" + maxWidths[i] + "s", row[i])).append(" |");
                }
                System.out.println(rowLine);
            }

            System.out.println(divider);

        } catch (SQLException e) {
            System.out.println("❌ Error retrieving records: " + e.getMessage());
        }
    }
    
    public void viewRecordsStaff(String sqlQuery, String[] columnHeaders, String[] columnNames, int param) {
        if (columnHeaders.length != columnNames.length) {
            System.out.println("Error: Mismatch between column headers and column names.");
            return;
        }

        try (Connection conn = DBconnector.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sqlQuery)) {

            pstmt.setInt(1, param);
            ResultSet rs = pstmt.executeQuery();

            // Step 1: Calculate max width per column
            int[] maxWidths = new int[columnNames.length];

            for (int i = 0; i < columnHeaders.length; i++) {
                maxWidths[i] = columnHeaders[i].length();
            }

            List<String[]> rows = new ArrayList<>();

            while (rs.next()) {
                String[] row = new String[columnNames.length];
                for (int i = 0; i < columnNames.length; i++) {
                    String value = rs.getString(columnNames[i]);
                    row[i] = value != null ? value : "";
                    if (row[i].length() > maxWidths[i]) {
                        maxWidths[i] = row[i].length();
                    }
                }
                rows.add(row);
            }

            // Show message if no rows found
            if (rows.isEmpty()) {
                System.out.println("\n⚠️  No records found.\n");
                return;
            }

            // Print header divider
            StringBuilder divider = new StringBuilder("+");
            for (int width : maxWidths) {
                for (int i = 0; i < width + 2; i++) {
                    divider.append("-");
                }
                divider.append("+");
            }

            // Print headers
            System.out.println(divider);
            StringBuilder headerLine = new StringBuilder("|");
            for (int i = 0; i < columnHeaders.length; i++) {
                headerLine.append(" ").append(String.format("%-" + maxWidths[i] + "s", columnHeaders[i])).append(" |");
            }
            System.out.println(headerLine);
            System.out.println(divider);

            // Print rows
            for (String[] row : rows) {
                StringBuilder rowLine = new StringBuilder("|");
                for (int i = 0; i < row.length; i++) {
                    rowLine.append(" ").append(String.format("%-" + maxWidths[i] + "s", row[i])).append(" |");
                }
                System.out.println(rowLine);
            }

            System.out.println(divider);

        } catch (SQLException e) {
            System.out.println("❌ Error retrieving records: " + e.getMessage());
        }
    }



    public void updateRecord(String sql, Object... values) {
        try (Connection conn = DBconnector.connectDB(); // Use the connectDB method
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Loop through the values and set them in the prepared statement dynamically
            for (int i = 0; i < values.length; i++) {
                if (values[i] instanceof Integer) {
                    pstmt.setInt(i + 1, (Integer) values[i]); // If the value is Integer
                } else if (values[i] instanceof Double) {
                    pstmt.setDouble(i + 1, (Double) values[i]); // If the value is Double
                } else if (values[i] instanceof Float) {
                    pstmt.setFloat(i + 1, (Float) values[i]); // If the value is Float
                } else if (values[i] instanceof Long) {
                    pstmt.setLong(i + 1, (Long) values[i]); // If the value is Long
                } else if (values[i] instanceof Boolean) {
                    pstmt.setBoolean(i + 1, (Boolean) values[i]); // If the value is Boolean
                } else if (values[i] instanceof java.util.Date) {
                    pstmt.setDate(i + 1, new java.sql.Date(((java.util.Date) values[i]).getTime())); // If the value is Date
                } else if (values[i] instanceof java.sql.Date) {
                    pstmt.setDate(i + 1, (java.sql.Date) values[i]); // If it's already a SQL Date
                } else if (values[i] instanceof java.sql.Timestamp) {
                    pstmt.setTimestamp(i + 1, (java.sql.Timestamp) values[i]); // If the value is Timestamp
                } else {
                    pstmt.setString(i + 1, values[i].toString()); // Default to String for other types
                }
            }

            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error updating record: " + e.getMessage());
        }
    }
    
    public void deleteRecord(String sql, Object... values) {
        try (Connection conn = DBconnector.connectDB();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

        // Loop through the values and set them in the prepared statement dynamically
        for (int i = 0; i < values.length; i++) {
            if (values[i] instanceof Integer) {
                pstmt.setInt(i + 1, (Integer) values[i]); // If the value is Integer
            } else {
                pstmt.setString(i + 1, values[i].toString()); // Default to String for other types
            }
        }
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error deleting record: " + e.getMessage());
        }
    }
    
    // Helper Method for Setting PreparedStatement Values
    private void setPreparedStatementValues(PreparedStatement pstmt, Object... values) throws SQLException {
        for (int i = 0; i < values.length; i++) {
            if (values[i] instanceof Integer) {
                pstmt.setInt(i + 1, (Integer) values[i]);
            } else if (values[i] instanceof Double) {
                pstmt.setDouble(i + 1, (Double) values[i]);
            } else if (values[i] instanceof Float) {
                pstmt.setFloat(i + 1, (Float) values[i]);
            } else if (values[i] instanceof Long) {
                pstmt.setLong(i + 1, (Long) values[i]);
            } else if (values[i] instanceof Boolean) {
                pstmt.setBoolean(i + 1, (Boolean) values[i]);
            } else if (values[i] instanceof java.util.Date) {
                pstmt.setDate(i + 1, new java.sql.Date(((java.util.Date) values[i]).getTime()));
            } else if (values[i] instanceof java.sql.Date) {
                pstmt.setDate(i + 1, (java.sql.Date) values[i]);
            } else if (values[i] instanceof java.sql.Timestamp) {
                pstmt.setTimestamp(i + 1, (java.sql.Timestamp) values[i]);
            } else {
                pstmt.setString(i + 1, values[i].toString());
            }
        }
    }


    // GET SINGLE VALUE METHOD
    public double getSingleValue(String sql, Object... params) {
        double result = 0.0;
        try (Connection conn = connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            setPreparedStatementValues(pstmt, params);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                result = rs.getDouble(1);
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving single value: " + e.getMessage());
        }
        return result;
    }
    
    public int getTripleValue(String sql, int param1, String param2, String param3) {
        int result = 0;
        try (Connection conn = connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, param1);
            pstmt.setString(2, param2);
            pstmt.setString(3, param3);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                result = rs.getInt(1);  // Get first column of the result
            }
        } catch (SQLException e) {
            System.out.println("❌ getTripleValue Error: " + e.getMessage());
        }
        return result;
    }

    
}
