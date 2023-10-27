package org.meixea.salchemy.view;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableCell;

public class ReagentQuantityCell<S> extends TableCell<S, SimpleIntegerProperty> {

    private Spinner<Integer> spinner;

    private SimpleIntegerProperty oldItem = null;

    public ReagentQuantityCell(){

        setFocusTraversable(false);

        spinner = new Spinner<>(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 999, 20, 5));

        spinner.setEditable(true);
        spinner.setFocusTraversable(false);

    }

    @Override
    public void updateItem(SimpleIntegerProperty item, boolean empty){

        if(oldItem != null)
            oldItem.unbind();

        if( empty ) {
            setGraphic(null);
            oldItem = null;
        }
        else {

            setGraphic(spinner);
            spinner.valueFactoryProperty().getValue().setValue(item.getValue());
            item.bind(spinner.valueProperty());
            oldItem = item;

        }
    }
}
