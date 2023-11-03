package org.meixea.salchemy.view;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.meixea.salchemy.model.*;

import java.util.List;

public class PotionReagentsCell extends TableCell<Potion, List<Reagent>> {

    static private final int MIN_ITEM_CELL_HEIGHT = 26;

    private PotionReagentsTableView itemsView;

    public PotionReagentsCell(){

        itemsView = new PotionReagentsTableView();

        itemsView.prefWidthProperty().bind(widthProperty());

    }
    @Override
    public void updateItem(List<Reagent> reagents, boolean empty){

        super.updateItem(reagents, empty);

        setText(null);

        if(empty)
            setGraphic(null);
        else {
            itemsView.setItems(FXCollections.observableList(reagents));
            setGraphic(itemsView);
        }
    }

    static public class PotionReagentsTableView extends TableView<Reagent> {

        private TableColumn<Reagent, AlchemyReagentCategory> categoryColumn;
        private TableColumn<Reagent, String> nameColumn;

        private SimpleIntegerProperty rowsNumber = new SimpleIntegerProperty(1);

        public PotionReagentsTableView(){

            getStyleClass().add("potion-reagents-tableview");

            categoryColumn = new TableColumn<>();
            categoryColumn.setCellValueFactory( reagent -> reagent.getValue().categoryProperty() );
            categoryColumn.setCellFactory( column -> new ReagentCategoryCell(null));

            nameColumn = new TableColumn<>();
            nameColumn.setCellValueFactory( reagent -> reagent.getValue().nameProperty() );
            nameColumn.setCellFactory( column -> {
                PotionReagentsTableCell cell = new PotionReagentsTableCell(null);
                cell.prefHeightProperty().bind(heightProperty().divide(rowsNumber));
                return cell;
            });
            nameColumn.prefWidthProperty().bind(widthProperty());

            getColumns().addAll(categoryColumn, nameColumn);

            prefHeightProperty().bind(rowsNumber.multiply(MIN_ITEM_CELL_HEIGHT));

            itemsProperty().addListener( (observable, oldList, newList) -> {
                rowsNumber.set(newList.size());
            });

        }
    }

    static public class PotionReagentsTableCell extends StyledTableCell<Reagent, String> {

        public PotionReagentsTableCell(List<String> extraStyles){

            super(extraStyles);

            setMinHeight(MIN_ITEM_CELL_HEIGHT);

        }
        @Override
        public void updateItem(String item, boolean empty){

            super.updateItem(item, empty);

        }
    }
}
