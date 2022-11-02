/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package connection;

import java.sql.*;
import enums.ConnectionStatus;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ondra
 */
public class DatabaseConnection extends Thread {
    private String user;
    private String password;
    private Connection connection;
    private ConnectionStatus connectionStatus = ConnectionStatus.UNKNOWN;
    
    boolean waitingForBlockedStatement = false;

    Thread sleeper = null;
    Thread checker = null;
    
    public DatabaseConnection(String user, String password) throws ClassNotFoundException {
        this.user = user;
        this.password = password;

        checker = new Thread(this);
        checker.setDaemon(true);
        checker.start();
        initConnection();
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }
    

    private void initConnection() throws ClassNotFoundException {
        while (!connectionStatus.getConnectionStatusBoolean()) {
            try {
                //Class.forName("oracle.jdbc.driver.OracleDriver");  
                this.setConnection(DriverManager.getConnection("jdbc:oracle:thin:@fei-sql1.upceucebny.cz:1521:IDAS", this.getUser(), this.getPassword()));
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
                throw new DatabaseException("");
            }
            this.connectionStatus = ConnectionStatus.CONNECTED;
            if (sleeper != null) {
                sleeper.interrupt();
            }
        }
    }
    
    public void close() {
        try {
            if (this.connectionStatus == ConnectionStatus.CONNECTED
                    || this.connectionStatus == ConnectionStatus.DISCONNECTED) {
                this.connection.close();
                this.connectionStatus = ConnectionStatus.CLOSED;
                this.checker.interrupt();
                if (this.sleeper != null) {
                    this.sleeper.interrupt();
                }
            }
        } catch (SQLException e) {
        }
    }

    public void checkConnection() throws ClassNotFoundException {
        while (!Thread.interrupted()) {
            isConnected();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    public boolean isConnected() throws ClassNotFoundException {
        if (connectionStatus != ConnectionStatus.CLOSED) {
            try {
                if (connectionStatus == ConnectionStatus.UNKNOWN || connectionStatus == ConnectionStatus.DISCONNECTED) {
                    initConnection();
                }
                boolean connected = getConnection().isValid(100) && !connection.isClosed();
                if (connected != connectionStatus.getConnectionStatusBoolean()) {
                    if (!connected) {
                        connectionStatus = ConnectionStatus.DISCONNECTED;
                    } else {
                        connectionStatus = ConnectionStatus.CONNECTED;
                    }

                }
                if (waitingForBlockedStatement) {
                    if (connected) {
                        waitingForBlockedStatement = false;
                    }
                }
                return connected;

            } catch (SQLException e) {
                return false;
            }

        }
        return false;
    }

    public Statement createUnBlockedStatement() {
        if (connectionStatus == ConnectionStatus.CONNECTED) {
            Statement statement = null;
            try {
                statement = getConnection().createStatement();

            } catch (SQLException e) {
            }
            return statement;
        } else {
            return null;
        }
    }

    public synchronized Statement createBlockedStatement() {
        if (connectionStatus != ConnectionStatus.CLOSED) {
            waitingForBlockedStatement = true;

            if (connectionStatus == ConnectionStatus.DISCONNECTED || connectionStatus == ConnectionStatus.UNKNOWN) {
                sleeper = new Thread();
                while (waitingForBlockedStatement) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                    }
                }
            }

            Statement statement = null;
            try {
                statement = getConnection().createStatement();
            } catch (SQLException e) {
                return null;
            }

            return statement;
        }
        return null;
    }

    public ResultSet executeQuery(Statement statement, String sql) {
        if (connectionStatus == ConnectionStatus.CONNECTED) {
            ResultSet resultSet = null;
            try {
                resultSet = statement.executeQuery(sql);
                return resultSet;
            } catch (SQLException e) {
                return null;
            }

        } else {
            return null;
        }

    }

    public void executeUpdate(Statement statement, String sql) throws ClassNotFoundException {
        if (isConnected()) {
            try {
                statement.executeUpdate(sql);
            } catch (Exception e) {
            } finally {
                try {
                    statement.close();
                } catch (SQLException e) {
                }
            }
        } else {
        }

    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                this.checkConnection();
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(DatabaseConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
