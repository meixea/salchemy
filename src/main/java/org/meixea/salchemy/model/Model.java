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

    static public void initialize(String databaseFileName){

        try {

            savingThreadPool = Executors.newSingleThreadExecutor();

            applicationPath = getApplicationPath();

            database = new SqliteAlchemyDatabase(applicationPath, databaseFileName, savingThreadPool);

            modelData = database.loadModelData();

        }
        catch (ClassNotFoundException | SQLException | IOException e) {
            System.out.println("Can't initialize database");
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }

    }
    static public AlchemyDatabase getDatabase(){
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

        MaxPriceSearch newSearch = new MaxPriceSearch();

//      Bag adding first, because of database saving dependencies
        modelData.reagentBags.add(newSearch.bagProperty().getValue());

        modelData.maxPriceSearches.add(newSearch);

        return newSearch;
    }
    static public void deleteMaxPriceSearch(MaxPriceSearch search){

//      Search removed first, because of database dependencies
        modelData.maxPriceSearches.remove(search);

        modelData.reagentBags.remove(search.bagProperty().getValue());

    }
    static public ExecutorService getSavingThreadPool(){
        return savingThreadPool;
    }
}
