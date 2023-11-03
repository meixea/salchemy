package org.meixea.salchemy.view;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.meixea.salchemy.model.AlchemyProperty;
import org.meixea.salchemy.model.MaxPriceSearch;
import org.meixea.salchemy.model.Potion;
import org.meixea.salchemy.model.Reagent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MaxPriceResultTableView extends TableView<Potion> {

    TableColumn<Potion, Number> priceColumn;
    TableColumn<Potion, Number> quantityColumn;
    TableColumn<Potion, List<AlchemyProperty>> propertiesColumn;
    TableColumn<Potion, List<Reagent>> reagentsColumn;
    public MaxPriceResultTableView(MaxPriceSearch search){
        super(search.getCalculationResult());

        priceColumn = new TableColumn<>();
        priceColumn.getStyleClass().add("price-result-column");
        priceColumn.setResizable(false);
        priceColumn.setCellValueFactory( potion ->
                new SimpleIntegerProperty(potion.getValue().getPrice())
        );

        propertiesColumn = new TableColumn<>();
        propertiesColumn.getStyleClass().add("properties-result-column");
        propertiesColumn.setCellValueFactory( potion -> {
            List<AlchemyProperty> value = new ArrayList<>(potion.getValue().getCommonProperties());
            value.sort( (p1, p2) -> p2.getPrice() - p1.getPrice() );
            return new SimpleObjectProperty<>(value);
        });
        propertiesColumn.setCellFactory( column -> new PotionPropertiesCell() );

        reagentsColumn = new TableColumn<>();
        reagentsColumn.getStyleClass().add("reagents-result-column");
        reagentsColumn.setCellValueFactory( potion -> {
            List<Reagent> value = new ArrayList<>(potion.getValue().getFormula());
            value.sort(Comparator.comparingInt(p -> p.categoryProperty().getValue().ordinal()));
            return new SimpleObjectProperty<>(value);
        });
        reagentsColumn.setCellFactory( column -> new PotionReagentsCell() );
        reagentsColumn.setResizable(false);

        quantityColumn = new TableColumn<>();
        quantityColumn.getStyleClass().add("price-result-column");
        quantityColumn.setResizable(false);
        quantityColumn.setCellValueFactory( potion ->
                new SimpleIntegerProperty(potion.getValue().getQuantity())
        );
        quantityColumn.setCellFactory( column -> new PotionQuantityCell() );

        getColumns().addAll(priceColumn, propertiesColumn, reagentsColumn, quantityColumn);

        propertiesColumn.prefWidthProperty().bind(
                widthProperty().add(38)
                        .subtract(priceColumn.widthProperty())
                        .subtract(quantityColumn.widthProperty())
                        .divide(2)
        );

        reagentsColumn.prefWidthProperty().bind(
                widthProperty().add(38)
                        .subtract(priceColumn.widthProperty())
                        .subtract(quantityColumn.widthProperty())
                        .subtract(propertiesColumn.widthProperty())
        );

        setFocusTraversable(false);

        getStyleClass().add("calc-result-table");

    }
}
