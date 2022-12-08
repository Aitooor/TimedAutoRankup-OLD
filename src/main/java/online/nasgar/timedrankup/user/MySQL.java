package online.nasgar.timedrankup.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import online.nasgar.timedrankup.TimedRankup;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * This code has been created by
 * gatogamer#6666 A.K.A. gatogamer.
 * If you want to use my code, please
 * don't remove this messages and
 * give me the credits. Arigato! n.n
 */
@Getter
@Setter
@RequiredArgsConstructor
public class MySQL {

    private final TimedRankup timedRankup;

    private String host;
    private String port;
    private String database;
    private String username;
    private String password;
    private Connection connection;

    public void connect () {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Properties properties = new Properties();
            properties.setProperty("user", this.username);
            properties.setProperty("password", this.password);
            properties.setProperty("autoReconnect", "true");
            properties.setProperty("verifyServerCertificate", "false");
            properties.setProperty("useSSL", "false");
            properties.setProperty("requireSSL", "false");
            connection = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database, properties);
            connection.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS timed_auto_rankup (playerIndex INT AUTO_INCREMENT PRIMARY KEY, UUID VARCHAR(100), Rank VARCHAR(100), Time BIGINT)");
            timedRankup.log("Connected to MySQL!");
        } catch (Exception e) {
            e.printStackTrace();
            timedRankup.log("Oh no, I can't connect to MySQL!");
            timedRankup.log("Send this error to gatogamer#6666!");
        }
    }

    public ResultSet query(String query) throws SQLException {
        Statement stmt = connection.createStatement();
        try {
            stmt.executeQuery(query);
            return stmt.getResultSet();
        } catch (Exception e) {
            timedRankup.log("An I/O exception has ocurred, info:");
            timedRankup.log("---------------------------------------------------------------");
            e.printStackTrace();
            timedRankup.log("---------------------------------------------------------------");
            return null;
        }
    }

    public void update(String qry) {
        try {
            connection.createStatement().executeUpdate(qry);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
