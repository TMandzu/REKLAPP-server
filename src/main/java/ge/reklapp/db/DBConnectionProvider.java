package ge.reklapp.db;

/**
 * Created by Tornike on 09.07.2016.
 */

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnectionProvider {
    public static Connection getConnection() throws URISyntaxException, SQLException, ClassNotFoundException {
        URI dbUri = new URI("postgres://oecfciomjamslc:Hsz2GjWV0dY7wa0qW-DslfmZ2z@ec2-54-247-185-241.eu-west-1.compute.amazonaws.com:5432/dbdo3nrjf7vjk9");

        String username = dbUri.getUserInfo().split(":")[0];
        String password = dbUri.getUserInfo().split(":")[1];
        String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath()+"?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";

        Class.forName("org.postgresql.Driver");
        return DriverManager.getConnection(dbUrl, username, password);
    }
}
