package org.meixea.salchemy.view;

import javafx.geometry.Pos;
import javafx.scene.control.TableCell;

public class TextCell<S, T> extends TableCell<S, T> {

    public TextCell(Pos alignment, SelectionHandler<S> onClickHandler){
        setAlignment(alignment);

        if(onClickHandler != null)
            setOnMouseClicked( event -> {
                onClickHandler.handle(getTableRow().getItem());
            });
    }
    @Override
    public void updateItem(T item, boolean empty){
        if(empty)
            setText(null);
        else
            setText(item.toString());
    }
}
