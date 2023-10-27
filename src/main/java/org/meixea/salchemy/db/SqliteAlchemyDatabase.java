package org.meixea.salchemy.db;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import org.meixea.salchemy.model.*;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class SqliteAlchemyDatabase implements AlchemyDatabase {

    private final String DATABASE_FILENAME;
    private ExecutorService savingPool;

    protected Connection connection = null;
    private ReagentsBagOperator reagentsBagOperator;
    private MaxPriceSearchOperator maxPriceSearchOperator;

    public SqliteAlchemyDatabase(String appPath, String baseName, ExecutorService savingPool)
            throws ClassNotFoundException, SQLException, IOException
    {

        DATABASE_FILENAME = baseName;
        this.savingPool = savingPool;

        initDatabase(appPath);
    }

    @Override
    public void close(){
        try {

            if( connection != null )
                connection.close();

        } catch (SQLException e) {
            System.out.println("Error closing database!");
        }
    }
    protected String getDatabasePath(String appPath){

            return Path.of(appPath, DATABASE_FILENAME).toString();

    }
    private List<AlchemyProperty> loadAllAlchemyProperties() throws SQLException {

        List<AlchemyProperty> result = new ArrayList();

        try(Statement statement = connection.createStatement() ) {

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
        }

        return result;
    }
    private List<Reagent> loadAllReagents(ModelData modelData) throws SQLException {

        List<Reagent> result = new ArrayList<>();

        try(Statement statement = connection.createStatement() ) {
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

                    AlchemyProperty property = modelData.getProperty(data.getInt("prop_id"));
                    prevReagent.addProperty(property);
                }
            }
        }

        return result;
    }

    private void initDatabase(String appPath) throws ClassNotFoundException, SQLException, IOException {

        Class.forName("org.sqlite.JDBC");

        String dataPath = getDatabasePath(appPath);

        boolean isCreating = false;

        if(Files.exists(Path.of(dataPath)))
            connection = DriverManager.getConnection("jdbc:sqlite:" + dataPath);
        else {

            URL patternBase = this.getClass().getResource(DATABASE_FILENAME);

            if(patternBase == null) {
                connection = DriverManager.getConnection("jdbc:sqlite:" + dataPath);
                isCreating = true;
            }
            else {
                unpackDataBase(dataPath, patternBase);
                connection = DriverManager.getConnection("jdbc:sqlite:" + dataPath);
            }

        }

        reagentsBagOperator = new ReagentsBagOperator(connection, savingPool);
        maxPriceSearchOperator = new MaxPriceSearchOperator(connection, savingPool);

        if(isCreating){
            System.out.println("Create base: " + dataPath);
            createDataBase();
        }

        openDataBase();

    }
    private void createDataBase() throws SQLException {

        NewDatabase.createDataBase(connection);

        ReagentsBag bag = new ReagentsBag(1, FXCollections.observableArrayList());

        reagentsBagOperator.saveReagentsBag(bag, null);

        maxPriceSearchOperator.saveMaxPriceSearch(new MaxPriceSearch(
                1,
                new SimpleStringProperty(MaxPriceSearch.getDefaultSearchName(1)),
                new SimpleObjectProperty(bag)
        ), null);

    }
    @Override
    public ModelData loadModelData() throws SQLException {

        ModelData result = new ModelData();

        result.alchemyProperties = loadAllAlchemyProperties();

        result.alchemyReagents = FXCollections.observableList(loadAllReagents(result));

//      Must be loaded first, because of dependencies
        result.reagentBags = reagentsBagOperator.load(result.alchemyReagents);

        result.maxPriceSearches = maxPriceSearchOperator.load(result.reagentBags);

        return result;
    }

    private void openDataBase() throws SQLException {

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

    }
}
