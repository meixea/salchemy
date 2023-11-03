package org.meixea.salchemy.db;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;

public class NewDatabase {

    public static void createDataBase(Connection connection) throws SQLException {

        try(Statement statement = connection.createStatement()) {
            statement.execute(
                    "CREATE TABLE reagents (" +
                            "id         INTEGER PRIMARY KEY, " +
                            "name       TEXT NOT NULL, " +
                            "category   INTEGER" +
                    ");"
            );

            statement.execute(
                    "CREATE TABLE properties (" +
                            "id         INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "name       TEXT NOT NULL, " +
                            "type       INTEGER NOT NULL, " +
                            "price      INTEGER NOT NULL, " +
                            "category   INTEGER" +
                    ");"
            );

            statement.execute(
                    "CREATE TABLE reg_prop (" +
                            "property   REFERENCES properties(id), " +
                            "reagent    REFERENCES reagents(id), " +
                            "CONSTRAINT id PRIMARY KEY (property, reagent) " +
                    ");"
            );

            statement.execute(
                    "CREATE TABLE reagents_bag (" +
                            "bag_id     INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "reserved   TEXT" +
                    ");"
            );

            statement.execute(
                    "CREATE TABLE reagent_in_bag (" +
                            "bag_id             REFERENCES reagents_bag(id), " +
                            "reagent_id         REFERENCES reagents(id), " +
                            "quantity_initial   INTEGER, " +
                            "quantity_final   INTEGER, " +
                            "CONSTRAINT id PRIMARY KEY (bag_id, reagent_id) " +
                    ");"
            );

            statement.execute(
                    "CREATE TABLE max_price_searches (" +
                            "id         INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "bag_id     REFERENCES reagents_bag(id), " +
                            "name       TEXT" +
                    ");"
            );

            statement.execute(
                    "CREATE TABLE potions (" +
                            "potion_id  INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "price      INTEGER NOT NULL, " +
                            "quantity   INTEGER NOT NULL" +
                    ");"
            );

            statement.execute(
                    "CREATE TABLE potion_reagents (" +
                            "potion_id          REFERENCES potions(potion_id), " +
                            "reagent_id         REFERENCES reagents(id), " +
                            "CONSTRAINT id PRIMARY KEY (potion_id, reagent_id) " +
                    ");"
            );

            statement.execute(
                    "CREATE TABLE maxpricesearch_potions (" +
                            "potion_id          REFERENCES potions(potion_id), " +
                            "search_id          REFERENCES max_price_searches(id), " +
                            "CONSTRAINT id PRIMARY KEY (potion_id, search_id) " +
                    ");"
            );

            fillPropTable(statement);
            fillReagentsTable(statement);

        }
    }
    private static void fillPropTable(Statement statement) throws SQLException {

        try(BufferedReader reader = new BufferedReader(new FileReader("properties.txt"))){

            while(reader.ready()){

                String line = reader.readLine();

                if( !line.equals("") )
                    parsePropertyString(line, statement);

            }

        }
        catch(IOException e){
            e.printStackTrace();
        }

    }
    private static void parsePropertyString(String line, Statement statement) throws SQLException {

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
    private static void fillReagentsTable(Statement statement) throws SQLException {

        try(BufferedReader reader = new BufferedReader(new FileReader("reagents.txt"))){

            int id = 1;
            while(reader.ready()){

                String line = reader.readLine();

                if( !line.equals("") )
                    parseReagentString(id++, line, statement);

            }

        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
    private static void parseReagentString(int id, String line, Statement statement) throws SQLException {

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

            int propId;
            try(ResultSet result = statement.getResultSet()) {
                result.next();

                propId = result.getInt("id");
            }

            query = String.format(
                    "INSERT INTO reg_prop (property, reagent) VALUES (%d, %d);",
                    propId,
                    id
            );

            statement.execute(query);
        }

    }
}
