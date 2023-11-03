package org.meixea.salchemy.view;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import org.meixea.salchemy.model.AlchemyProperty;
import org.meixea.salchemy.model.AlchemyType;
import org.meixea.salchemy.model.Potion;

import java.util.Arrays;
import java.util.List;

public class PotionPropertiesCell extends TableCell<Potion, List<AlchemyProperty>> {

    static private final int MIN_ITEM_CELL_HEIGHT = 26;

    private PotionPropertiesTableView itemsView;

    public PotionPropertiesCell(){

        itemsView = new PotionPropertiesTableView();

        itemsView.prefWidthProperty().bind(widthProperty());

    }
    @Override
    public void updateItem(List<AlchemyProperty> properties, boolean empty){

        super.updateItem(properties, empty);

        setText(null);

        if(empty)
            setGraphic(null);
        else {
            itemsView.setItems(FXCollections.observableList(properties));
//            itemsView.prefHeightProperty().bind(heightProperty());
            setGraphic(itemsView);
        }
    }

    static public class PotionPropertiesTableView extends TableView<AlchemyProperty> {

        private TableColumn<AlchemyProperty, Number> priceColumn;
        private TableColumn<AlchemyProperty, String> nameColumn;

        private SimpleIntegerProperty rowsNumber = new SimpleIntegerProperty(1);

        public PotionPropertiesTableView(){

            getStyleClass().add("potion-properties-tableview");

            priceColumn = new TableColumn<>();
            priceColumn.setCellValueFactory( property -> new SimpleIntegerProperty(property.getValue().getPrice()) );
            priceColumn.setCellFactory( column -> new PotionPropertiesTableCell<>(List.of("price")) );

            nameColumn = new TableColumn<>();
            nameColumn.setCellValueFactory( property -> new SimpleStringProperty(property.getValue().getName()) );
            nameColumn.setCellFactory( column -> {
                PotionPropertiesTableCell cell = new PotionPropertiesTableCell<>(List.of("name"));
                cell.prefHeightProperty().bind(heightProperty().divide(rowsNumber));
                return cell;
            });
            nameColumn.prefWidthProperty().bind(widthProperty());

            getColumns().addAll(priceColumn, nameColumn);

            itemsProperty().addListener( (observable, oldList, newList) -> {
                rowsNumber.set(newList.size());
                setPrefHeight((MIN_ITEM_CELL_HEIGHT+0) * newList.size());

            });

        }
    }

    static public class PotionPropertiesTableCell<T> extends StyledTableCell<AlchemyProperty, T> {

        public PotionPropertiesTableCell(List<String> extraStyles){

            super(extraStyles);

            setMinHeight(MIN_ITEM_CELL_HEIGHT);

        }
        @Override
        public void updateItem(T item, boolean empty){

            super.updateItem(item, empty);

            List<String> styles = getStyleClass();

            styles.remove("potion");
            styles.remove("poison");

            AlchemyProperty property = getTableRow().getItem();

            if(property != null){
                if (property.getType() == AlchemyType.NEGATIVE)
                    styles.add("poison");
                else
                    styles.add("potion");
            }
        }
    }
}
