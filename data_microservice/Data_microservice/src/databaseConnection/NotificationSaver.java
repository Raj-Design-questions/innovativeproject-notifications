package databaseConnection;

import java.sql.*;

import com.sun.istack.internal.NotNull;
import services.Notification;


public class NotificationSaver implements INotificationSaver {
    private final String values = " VALUES ( ?, ?, ?, ?, ?, ? );";
    private DBConnection connection;

    public NotificationSaver(DBConnection connection) {
        this.connection = connection;
    }

    @Override
    public void saveToDatabase(Notification notification, @NotNull String tableName) {
        final String insertNotification = "INSERT INTO " + tableName +
            " ( timestamp, source_user_id, target_user_id," +
            " target_group_id, priority, value) " + values;
        try {
            Connection connection = this.connection.getConnection();
            PreparedStatement insert = connection.prepareStatement(insertNotification);
            setNotificationOnStatement(notification, insert);
            insert.execute();
            
        } catch (Exception e) {
           e.printStackTrace();
        }
    }

    @Override
    public boolean isAnyNotificationFromProducerInDataBase(Notification notification, @NotNull String tableName) {
        //TODO na podstawie jakich pol znawac ze msg juz jest?
        final String select = "SELECT * FROM " + tableName + " WHERE " +
                "source_user_id=" + notification.getSenderId() +
                " AND target_user_id=" + notification.getReceivers();
        try {
            Statement statement = connection.getConnection().createStatement();
            ResultSet result = statement.executeQuery(select);
            return result.next();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void update(Notification notification, @NotNull String tableName) {
        //TODO jakie pola updatowac?
        final String update = "UPDATE " + tableName +
                " SET timestamp=\'" + notification.getTime().toString() + "\', " +
                " value=\'" + notification.getMessage() + "\' " +
                " WHERE source_user_id=" + notification.getSenderId() +
                " AND target_user_id=" + notification.getReceivers();
        try {
            Statement statement = connection.getConnection().createStatement();
            statement.executeUpdate(update);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setNotificationOnStatement(Notification notification, PreparedStatement statement) throws SQLException {
        statement.setTimestamp(1, notification.getTime());
        statement.setInt(2, notification.getSenderId());
        statement.setInt(3, notification.getReceivers());
        statement.setInt(4, notification.getReceivers());
        statement.setInt(5, notification.getPriority());
        statement.setString(6, notification.getMessage());
    }
}