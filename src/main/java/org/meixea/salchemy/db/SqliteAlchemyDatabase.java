package org.meixea.salchemy.db;

import org.meixea.salchemy.model.*;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SqliteAlchemyDatabase implements AlchemyDatabase {

    private String DATABASE_FILENAME = "alchemy.s3db";

    protected Connection connection = null;
    protected Statement statement = null;

    public SqliteAlchemyDatabase(String appPath) throws ClassNotFoundException, SQLException, IOException {
        initDatabase(appPath);
    }

    @Override
    public void close(){
        try {

            if(statement != null)
                statement.close();

            if( connection != null )
                connection.close();

        } catch (SQLException e) {
            System.out.println("Error closing database!");
        }
    }
    protected String getDatabasePath(String appPath){

            return Path.of(appPath, DATABASE_FILENAME).toString();

    }
    @Override
    public List<AlchemyProperty> getAllAlchemyProperties() throws SQLException {

        List<AlchemyProperty> result = new ArrayList();

        statement.execute("SELECT * FROM properties;");

        try(ResultSet data = statement.getResultSet()) {

            while (data.next()) {

                int id = data.getInt("id");
                String name = data.getString("name");
                AlchemyType type = AlchemyType.values()[data.getInt("type")];
                int price = data.getInt("price");
                AlchemyPropertyCategory category = AlchemyPropertyCategory.values()[data.getInt("category")];

                result.add(new AlchemyProperty(id, name, type, price, category));

            }

        }

        return result;
    }
    @Override
    public List<Reagent> getAllReagents() throws SQLException {

        AlchemyProperty.getAllProperties();
        List<Reagent> result = new ArrayList<>();

        statement.execute(
                "SELECT " +
                        "reagents.id AS reg_id, " +
                        "reagents.name AS reg_name, " +
                        "reagents.category AS reg_category, " +
                        "properties.id AS prop_id " +
                "FROM " +
                        "reagents " +
                "JOIN " +
                        "reg_prop " +
                    "ON " +
                        "reagents.id=reg_prop.reagent " +
                "JOIN " +
                        "properties " +
                    "ON " +
                        "reg_prop.property=properties.id;"
        );

        try(ResultSet data = statement.getResultSet()) {

            Reagent prevReagent = null;

            while( data.next() ){

                int id = data.getInt("reg_id");

                if(prevReagent == null || prevReagent.idProperty().getValue() != id) {
                    String name = data.getString("reg_name");
                    AlchemyReagentCategory category = AlchemyReagentCategory.values()[data.getInt("reg_category")];
                    prevReagent = new Reagent(id, name, category, new ArrayList<>());
                    result.add(prevReagent);
                }

                AlchemyProperty property = AlchemyProperty.getProperty(data.getInt("prop_id"));
                prevReagent.addProperty(property);
            }
        }

        return result;
    }

    @Override
    public String getReagent(int id){

        return null;

    }
    @Override
    public ModelData loadModelData() throws SQLException {
        ModelData result = new ModelData();

        try(Statement st = connection.createStatement()){
            String query;

            query = "SELECT * FROM max_price_searches;";
            st.execute(query);

            try(ResultSet resultSet = st.getResultSet()){
                while(resultSet.next()){

                    int id = resultSet.getInt("id");
                    String name = resultSet.getString("name");

                    result.maxPriceSearches.add(new MaxPriceSearch(id, name));
                }
            }
        }

        return result;
    }

    private void initDatabase(String appPath) throws ClassNotFoundException, SQLException, IOException {

        Class.forName("org.sqlite.JDBC");

        String dataPath = getDatabasePath(appPath);

        if(Files.exists(Path.of(dataPath)))
            openDataBase(dataPath);

        else {

            URL patternBase = this.getClass().getResource(DATABASE_FILENAME);
            if(patternBase == null) {
                NewDatabase.createDataBase(dataPath);
                openDataBase(dataPath);
            }
            else
                unpackDataBase(dataPath, patternBase);

        }

//        printDataBase();

    }
    @Override
    public void deleteMaxPriceSearch(MaxPriceSearch search) throws SQLException {

        try(Statement st = connection.createStatement()) {

            String query = String.format("DELETE FROM max_price_searches WHERE id=%d;",
                    search.idProperty().getValue());

            st.execute(query);

        }

    }
    @Override
    public void saveMaxPriceSearch(MaxPriceSearch search) throws SQLException {

        try(Statement st = connection.createStatement()) {

            String query = String.format("INSERT INTO max_price_searches (id, name) VALUES (%d, '%s');",
                    search.idProperty().getValue(), search.nameProperty().getValue());

            st.execute(query);
        }
    }

    private void openDataBase(String fileName) throws SQLException {

        connection = DriverManager.getConnection("jdbc:sqlite:" + fileName);
        statement = connection.createStatement();

    }
    private void unpackDataBase(String dataPath, URL patternBase) throws IOException, SQLException {

        try(
                InputStream inputStream = patternBase.openStream();
                FileOutputStream outputStream = new FileOutputStream(dataPath)
        ){

            byte[] buffer = new byte[100000];
            while(inputStream.available() > 0){
                int n = inputStream.read(buffer);
                outputStream.write(buffer, 0, n);
            }

        }

        openDataBase(dataPath);
    }

    public void printDataBase() throws SQLException {

        statement.execute(
                "SELECT " +
                        "reagents.id AS reg_id, reagents.name AS reg_name, properties.name AS prop " +
                    "FROM " +
                        "reagents " +
                    "JOIN " +
                        "reg_prop " +
                    "ON " +
                        "reagents.id=reg_prop.reagent " +
                    "JOIN " +
                        "properties " +
                    "ON " +
                        "reg_prop.property=properties.id;");

        ResultSet result = statement.getResultSet();

        while(result.next()){
            System.out.printf(
                    "%-3d  %-30s| %s\n",
                    result.getInt("reg_id"),
                    result.getString("reg_name"),
                    result.getString("prop")
            );
        }

        result.close();
    }
}
