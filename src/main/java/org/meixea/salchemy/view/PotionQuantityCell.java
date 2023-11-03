package org.meixea.salchemy.view;

import javafx.scene.control.TableCell;
import org.meixea.salchemy.model.Potion;

public class PotionQuantityCell extends TableCell<Potion, Number> {
    @Override
    public void updateItem(Number item, boolean empty){

        super.updateItem(item, empty);

        if(empty)
            setText(null);
        else
            setText("x" + item.toString());
    }
}
