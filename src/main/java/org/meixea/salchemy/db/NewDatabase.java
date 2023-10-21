package org.meixea.salchemy.db;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;

public class NewDatabase {

    private static Connection connection;
    private static Statement statement;

    public static void createDataBase(String fileName) throws SQLException {

        System.out.println("Create base");

        connection = DriverManager.getConnection("jdbc:sqlite:" + fileName);

        statement = connection.createStatement();

        statement.execute(
                "CREATE TABLE reagents (" +
                        "id         INTEGER PRIMARY KEY, " +
                        "name       TEXT NOT NULL, " +
                        "category   INTEGER" +
                ");");

        statement.execute(
                "CREATE TABLE properties (" +
                        "id         INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "name       TEXT NOT NULL, " +
                        "type       INTEGER NOT NULL, " +
                        "price      INTEGER NOT NULL, " +
                        "category   INTEGER" +
                ");");

        statement.execute(
                "CREATE TABLE reg_prop (" +
                        "property   REFERENCES properties(id), " +
                        "reagent    REFERENCES reagents(id), " +
                        "CONSTRAINT id PRIMARY KEY (property, reagent) " +
                ");");

        statement.execute(
                "CREATE TABLE max_price_searches (" +
                        "id         INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "name       TEXT" +
                ");");

        fillPropTable();
        fillReagentsTable();
        fillMaxPriceSearchesTable();

        statement.close();
        connection.close();
    }
    private static void fillPropTable() throws SQLException {

        try(BufferedReader reader = new BufferedReader(new FileReader("properties.txt"))){

            while(reader.ready()){

                String line = reader.readLine();

                if( !line.equals("") )
                    parsePropertyString(line);

            }

        }
        catch(IOException e){
            e.printStackTrace();
        }

    }
    private static void parsePropertyString(String line) throws SQLException {

        String[] parts = line.split("#");

        int type;
        if(parts[0].equals("POSITIVE"))
            type = 1;
        else
            type = 0;

        String name = parts[1].strip();

        int price = Integer.valueOf(parts[2].strip());

        int category = Integer.valueOf(parts[3].strip());

        String query = String.format(
                "INSERT INTO properties (name, type, price, category) VALUES ('%s', %d, %d, %d);",
                name,
                type,
                price,
                category
        );

        statement.execute(query);
    }
    private static void fillReagentsTable() throws SQLException {

        try(BufferedReader reader = new BufferedReader(new FileReader("reagents.txt"))){

            int id = 1;
            while(reader.ready()){

                String line = reader.readLine();

                if( !line.equals("") )
                    parseReagentString(id++, line);

            }

        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
    private static void parseReagentString(int id, String line) throws SQLException {

        String[] parts = line.split("#");

        String name = parts[0];

        int category = Integer.valueOf(parts[5].strip());

        String query = String.format(
                "INSERT INTO reagents (id, name, category) VALUES (%d, '%s', %d);",
                id,
                name,
                category
        );

        statement.execute(query);

        for(int i = 1; i < 5; i++){
            query = String.format(
                    "SELECT id FROM properties WHERE (name LIKE '%s');",
                    parts[i]
            );

            statement.execute(query);

            ResultSet result = statement.getResultSet();
            result.next();

            int propId = result.getInt("id");

            result.close();

            query = String.format(
                    "INSERT INTO reg_prop (property, reagent) VALUES (%d, %d);",
                    propId,
                    id
            );

            statement.execute(query);

        }

    }
    static private void fillMaxPriceSearchesTable() throws SQLException {
        statement.execute(
                "INSERT INTO max_price_searches (id, name) VALUES (1, NULL);"
        );
    }

}
