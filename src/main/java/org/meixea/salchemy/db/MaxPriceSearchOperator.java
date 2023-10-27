package org.meixea.salchemy.db;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import org.meixea.salchemy.model.MaxPriceSearch;
import org.meixea.salchemy.model.ModelData;
import org.meixea.salchemy.model.ReagentsBag;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class MaxPriceSearchOperator {

    private Connection connection;
    private ExecutorService pool;

    public MaxPriceSearchOperator(Connection connection, ExecutorService pool) {
        this.connection = connection;
        this.pool = pool;
    }
    public ObservableList<MaxPriceSearch> load(List<ReagentsBag> loadedBags) throws SQLException {

        ObservableList<MaxPriceSearch> result = FXCollections.observableList(loadAllSearches(loadedBags));

        result.addListener(getListListener());

        return result;
    }
    private List<MaxPriceSearch> loadAllSearches(List<ReagentsBag> loadedBags) throws SQLException {

        List<MaxPriceSearch> searches = new ArrayList<>();

        try(Statement st = connection.createStatement()){
            String query;

            query = "SELECT * FROM max_price_searches;";
            st.execute(query);

            try(ResultSet resultSet = st.getResultSet()){
                while(resultSet.next()){

                    int id = resultSet.getInt("id");

                    String name = resultSet.getString("name");

                    int bag_id = resultSet.getInt("bag_id");
                    ReagentsBag bag = loadedBags.stream()
                            .filter( i -> i.getId() == bag_id )
                            .findAny()
                            .orElse(null);

                    searches.add(new MaxPriceSearch(
                            id,
                            new SimpleStringProperty(name),
                            new SimpleObjectProperty(bag)
                    ));
                }
            }
        }

        return searches;

    }
    private ListChangeListener<MaxPriceSearch> getListListener() {
        return changes -> {
            while (changes.next()) {
                if (changes.wasAdded()) {
                    for (MaxPriceSearch search : changes.getAddedSubList())
                        saveMaxPriceSearch(search, pool);
                }
                if (changes.wasRemoved()) {
                    for (MaxPriceSearch search : changes.getRemoved())
                        deleteMaxPriceSearch(search, pool);
                }
            }
        };
    }
    public void deleteMaxPriceSearch(MaxPriceSearch search, ExecutorService pool) {

        Runnable runnable = () -> {
            try (Statement st = connection.createStatement()) {

                String query = String.format("DELETE FROM max_price_searches WHERE id=%d;", search.getId());

                st.execute(query);

            }
            catch(SQLException e){
                System.out.printf("Can't delete MaxPriceSearch, id(%d)\n", search.getId());
                System.out.println(e.getMessage());
            }
        };

        if(pool == null)
            runnable.run();
        else
            pool.execute(runnable);

    }

    public void saveMaxPriceSearch(MaxPriceSearch search, ExecutorService pool) {

        Runnable runnable = () -> {
            try(Statement st = connection.createStatement()) {

                int bag_id = search.bagProperty().getValue().getId();

                String query = String.format(
                        "INSERT INTO max_price_searches " +
                                "(id, bag_id, name) " +
                                "VALUES " +
                                "(%d, %d, '%s');",
                        search.getId(),
                        bag_id,
                        search.nameProperty().getValue());

                st.execute(query);

            }
            catch(SQLException e){
                System.out.printf("Can't save MaxPriceSearch, id(%d)\n", search.getId());
                System.out.println(e.getMessage());
            }
        };

        if(pool == null)
            runnable.run();
        else
            pool.execute(runnable);
    }
}