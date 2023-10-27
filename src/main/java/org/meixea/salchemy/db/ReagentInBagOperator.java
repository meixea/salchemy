package org.meixea.salchemy.db;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import org.meixea.salchemy.model.Reagent;
import org.meixea.salchemy.model.ReagentInBag;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.StringJoiner;
import java.util.concurrent.ExecutorService;

public class ReagentInBagOperator {

    private Connection connection;
    private ExecutorService pool;
    public ReagentInBagOperator(Connection connection, ExecutorService pool){
        this.connection = connection;
        this.pool = pool;
    }
    public ObservableList<ReagentInBag> loadList(int bag_id, List<Reagent> loadedReagents) throws SQLException {

        ObservableList<ReagentInBag> result = FXCollections.observableArrayList();

        String query = String.format("SELECT * FROM reagent_in_bag WHERE bag_id=%d;", bag_id);

        try(Statement st = connection.createStatement()){
            st.execute(query);
            try(ResultSet ribSet = st.getResultSet()){
                while(ribSet.next()){
                    int reagent_id = ribSet.getInt("reagent_id");
                    Reagent reagent = loadedReagents.stream()
                            .filter( i -> i.idProperty().getValue() == reagent_id)
                            .findAny()
                            .orElse(null);
                    int quantity = ribSet.getInt("quantity_initial");
                    int surplus = ribSet.getInt("quantity_final");

                    result.add(new ReagentInBag(reagent, quantity, surplus));
                }
            }
        }

        setupList(bag_id, result);

        return result;
    }
    public void deleteList(int bag_id) {

        String query = String.format("DELETE FROM reagent_in_bag WHERE bag_id=%d;", bag_id);

        try(Statement st = connection.createStatement()){
            st.execute(query);
        }
        catch( SQLException e ){
            System.out.printf("Can't delete Reagents In Bag, id(%d)\n", bag_id);
            System.out.println(e.getMessage());
        }
    }
    public void setupList(final int bag_id, ObservableList<ReagentInBag> ribs){

//      Add ChangeListeners for existing reagents (on loading from database)
        for(ReagentInBag rib : ribs) {
            rib.quantityProperty().addListener( getQuantityListener(bag_id, rib));
            rib.surplusProperty().addListener(getSurplusListener(bag_id, rib));
        }
//      Add ChangeListeners when adding or removing reagents at runtime

        ribs.addListener((ListChangeListener<ReagentInBag>) changes -> {
            while(changes.next()){
                if(changes.wasAdded()){

                    pool.execute(getRibAddingRunnable(bag_id, (List<ReagentInBag>) changes.getAddedSubList()));

                    for(ReagentInBag rib : changes.getAddedSubList()){
                        rib.quantityProperty().addListener(getQuantityListener(bag_id, rib));
                        rib.surplusProperty().addListener(getSurplusListener(bag_id, rib));
                    }
                }
                if(changes.wasRemoved()){

                    pool.execute(getRibDeletingRunnable(bag_id, (List<ReagentInBag>) changes.getRemoved()));

                }
            }
        });
    }
    private Runnable getRibAddingRunnable(int bag_id, List<ReagentInBag> addedList){
        return () -> {
            StringJoiner values = new StringJoiner(", ");
            for(ReagentInBag rib : addedList)
                values.add(String.format("(%d, %d, %d, %d)",
                        bag_id,
                        rib.getReagent().idProperty().getValue(),
                        rib.quantityProperty().getValue(),
                        rib.surplusProperty().getValue()
                ));

            String query =
                    "INSERT INTO reagent_in_bag " +
                            "(bag_id, reagent_id, quantity_initial, quantity_final) " +
                            "VALUES " +
                            values + ";";

            try(Statement st = connection.createStatement()){
                st.execute(query);
            }
            catch( SQLException e ){
                System.out.printf("Can't insert Reagents In Bag, id(%d)\n", bag_id);
                System.out.println(e.getMessage());
            }
        };
    }
    private Runnable getRibDeletingRunnable(int bag_id, List<ReagentInBag> deletedList){
        return () -> {
            StringJoiner values = new StringJoiner(", ");
            for(ReagentInBag rib : deletedList)
                values.add(rib.getReagent().idProperty().getValue().toString());

            String query = String.format(
                    "DELETE FROM reagent_in_bag " +
                            "WHERE " +
                            "bag_id=%d " +
                            "AND " +
                            "reagent_id IN (%s);",
                    bag_id, values);

            try(Statement st = connection.createStatement()){
                st.execute(query);
            }
            catch( SQLException e ){
                System.out.printf("Can't delete Reagents From Bag, id(%d)\n", bag_id);
                System.out.println(e.getMessage());
            }
        };
    }
    private ChangeListener<Number> getQuantityListener(int bag_id, ReagentInBag item){

        return (observable, oldValue, newValue) ->{

            Runnable runnable = () -> {

                String query = String.format(
                        "UPDATE reagent_in_bag " +
                        "SET quantity_initial=%d " +
                        "WHERE bag_id=%d AND reagent_id=%d;",
                        newValue, bag_id, item.getReagent().idProperty().getValue()
                );

                try(Statement st = connection.createStatement()){
                    st.execute(query);
                }
                catch(SQLException e){
                    System.out.printf("Can't save quantity change for bag(%d), reagent(%d)\n",
                            bag_id, item.getReagent().idProperty().getValue());
                    System.out.println(e.getMessage());
                }

            };

            pool.execute(runnable);
        };
    }
    private ChangeListener<Number> getSurplusListener(int bag_id, ReagentInBag item){

        return (observable, oldValue, newValue) ->{

            Runnable runnable = () -> {

                String query = String.format(
                        "UPDATE reagent_in_bag " +
                        "SET quantity_final=%d " +
                        "WHERE bag_id=%d AND reagent_id=%d;",
                        newValue, bag_id, item.getReagent().idProperty().getValue()
                );

                try(Statement st = connection.createStatement()){
                    st.execute(query);
                }
                catch(SQLException e){
                    System.out.printf("Can't save surplus change for bag(%d), reagent(%d)\n",
                            bag_id, item.getReagent().idProperty().getValue());
                    System.out.println(e.getMessage());
                }

            };

            pool.execute(runnable);
        };
    }
}
