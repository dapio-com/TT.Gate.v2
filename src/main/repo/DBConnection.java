package main.repo;

import java.sql.*;

public class DBConnection {

    //private Logger log = Logger.getLogger(DBConnection.class);

    private Connection connection;
    private Statement connectionStatement;




    public DBConnection(String host, String port, String dbName, String dbUser, String dbPassword) throws SQLException {
        String url = "jdbc:postgresql://" + host + ":" + port + "/" + dbName;
            connection = DriverManager.getConnection(url, dbUser, dbPassword);
            connectionStatement = connection.createStatement();

    }

//    public Connection getConnection() {
//        return connection;
//    }

    public Statement getConnectionStatement() {
        return connectionStatement;
    }

    public void closeConnection() throws Exception{

            if (!connectionStatement.isClosed()){
                connectionStatement.close();
            }
            if (!connection.isClosed()){
                connection.close();
            }
    }
}
