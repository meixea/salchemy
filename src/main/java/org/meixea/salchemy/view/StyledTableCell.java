package org.meixea.salchemy.view;

import javafx.scene.Node;
import javafx.scene.control.TableCell;
import java.util.List;

public class StyledTableCell<S, T> extends TableCell<S, T> {

    public StyledTableCell(List<String> extraStyles){
        if(extraStyles != null)
            getStyleClass().addAll(extraStyles);
    }
    @Override
    public void updateItem(T item, boolean empty){

        super.updateItem(item, empty);

        if(empty) {
            setText(null);
            setGraphic(null);
        }
        else if(item instanceof Node) {
            setText(null);
            setGraphic((Node) item);
        }
        else
            setText(item.toString());
    }
}
