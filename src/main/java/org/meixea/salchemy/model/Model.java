package org.meixea.salchemy.model;

import javafx.collections.FXCollections;
import org.meixea.salchemy.db.AlchemyDatabase;
import org.meixea.salchemy.db.SqliteAlchemyDatabase;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Model {

    static private AlchemyDatabase database;

    static private ExecutorService savingThreadPool;

    static public String applicationPath;

    static public ModelData modelData;

    static public void initialize(){

        try {

            applicationPath = getApplicationPath();

            database = new SqliteAlchemyDatabase(applicationPath);

            AlchemyProperty.allProperties = database.getAllAlchemyProperties();
            Reagent.allReagents = FXCollections.observableList(database.getAllReagents());

            modelData = database.loadModelData();

            savingThreadPool = Executors.newSingleThreadExecutor();

        }
        catch (ClassNotFoundException | SQLException | IOException e) {
            System.out.println("Can't initialize database");
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }

    }
    static AlchemyDatabase getDatabase(){
        return database;
    }
    static public void shutdown(){

        savingThreadPool.shutdown();

        try {
            database.close();
        } catch (IOException e) {
            System.out.println("Error closing Model");
            System.out.println(e.getMessage());
        }

    }
    static private String getApplicationPath(){

        try {

            Path path = Path.of(Class.forName("org.meixea.salchemy.model.Main")
                    .getProtectionDomain().getCodeSource().getLocation().toURI());

            if(Files.isRegularFile(path))
                path = path.getParent();


            return path.toString();

        } catch (ClassNotFoundException | URISyntaxException e) {
            throw new RuntimeException(e);
        }

    }
    static public ModelData getModelData(){
        return modelData;
    }
    static public MaxPriceSearch createMaxPriceSearch(){

        MaxPriceSearch newSearch = getModelData().createMaxPriceSearch();
        savingThreadPool.execute( () -> {
            try {
                getDatabase().saveMaxPriceSearch(newSearch);
            } catch (SQLException e) {
                System.out.printf("Can't save \"%s\" to database\n", newSearch.nameProperty().getValue());
                System.out.println(e.getMessage());
            }
        } );

        return newSearch;
    }
    static public void deleteMaxPriceSearch(int id){

        MaxPriceSearch search = getModelData().getMaxPriceSearch(id);

        savingThreadPool.execute( () -> {
            try {
                getDatabase().deleteMaxPriceSearch(search);
            } catch (SQLException e) {
                System.out.printf("Can't delete \"%s\" from database\n", search.nameProperty().getValue());
                System.out.println(e.getMessage());
            }
        } );
    }
    static public ExecutorService getSavingThreadPool(){
        return savingThreadPool;
    }
}
