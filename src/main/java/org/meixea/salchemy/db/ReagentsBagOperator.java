package org.meixea.salchemy.db;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import org.meixea.salchemy.model.Reagent;
import org.meixea.salchemy.model.ReagentInBag;
import org.meixea.salchemy.model.ReagentsBag;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class ReagentsBagOperator {

    private Connection connection;
    private ExecutorService pool;
    private ReagentInBagOperator ribOperator;
    public ReagentsBagOperator(Connection connection, ExecutorService pool){
        this.connection = connection;
        this.pool = pool;

        ribOperator = new ReagentInBagOperator(connection, pool);
    }
    public ObservableList<ReagentsBag> load(List<Reagent> loadedReagents) throws SQLException {

        ObservableList<ReagentsBag> result = FXCollections.observableList(loadAllBags(loadedReagents));

        result.addListener(getListListener());

        return result;
    }
    private List<ReagentsBag> loadAllBags(List<Reagent> loadedReagents) throws SQLException {

        List<ReagentsBag> bags = new ArrayList<>();

        try(Statement st = connection.createStatement()){
            String query;

            query = "SELECT * FROM reagents_bag;";
            st.execute(query);

            try(ResultSet resultSet = st.getResultSet()){
                while(resultSet.next()){

                    int id = resultSet.getInt("bag_id");

                    ObservableList<ReagentInBag> reagents = ribOperator.loadList(id, loadedReagents);

                    bags.add(new ReagentsBag(id, reagents));
                }
            }
        }

        return bags;
    }
    private ListChangeListener<ReagentsBag> getListListener(){
        return changes -> {
            while(changes.next()) {
                if(changes.wasAdded()){
                    for( ReagentsBag bag : changes.getAddedSubList() )
                        saveReagentsBag(bag, pool);
                }
                if(changes.wasRemoved()){
                    for( ReagentsBag bag : changes.getRemoved() )
                        deleteReagentsBag(bag, pool);
                }
            }
        };
    }
    public void deleteReagentsBag(ReagentsBag bag, ExecutorService pool){
        Runnable runnable = () -> {

            ribOperator.deleteList(bag.getId());

            try (Statement st = connection.createStatement()) {

                String query = String.format("DELETE FROM reagents_bag WHERE bag_id=%d;", bag.getId());

                st.execute(query);

            }
            catch(SQLException e){
                System.out.printf("Can't delete ReagentsBag id(%d)\n", bag.getId());
                System.out.println(e.getMessage());
            }
        };

        if(pool == null)
            runnable.run();
        else
            pool.execute(runnable);

    }
    public void saveReagentsBag(ReagentsBag bag, ExecutorService pool) {

        ribOperator.setupList(bag.getId(), bag.getReagents());

        Runnable task = () -> {
            try(Statement st = connection.createStatement()){
                String query = String.format(
                        "INSERT INTO reagents_bag (bag_id) VALUES (%d);",
                        bag.getId()
                );
                st.execute(query);
            }
            catch(SQLException e){
                System.out.printf("Can't save ReagentsBag id(%d)\n", bag.getId());
                System.out.println(e.getMessage());
            }
        };

        if( pool == null )
            task.run();
        else
            pool.execute(task);
    }
}
