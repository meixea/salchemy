package org.meixea.salchemy.view;

import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.meixea.salchemy.model.AlchemyReagentCategory;
import org.meixea.salchemy.model.Reagent;

import java.util.HashMap;

public class ReagentCategoryCell<T> extends TextCell<T, AlchemyReagentCategory> {

    static private HashMap<AlchemyReagentCategory, Image> images = new HashMap<>();
    static {
        images.put(AlchemyReagentCategory.CULTIVATED, new Image(
                ReagentCategoryCell.class.getResource("icons/reg_cat_common.png").toString()
        ));
        images.put(AlchemyReagentCategory.RARE, new Image(
                ReagentCategoryCell.class.getResource("icons/reg_cat_rare.png").toString()
        ));
        images.put(AlchemyReagentCategory.UNIQUE, new Image(
                ReagentCategoryCell.class.getResource("icons/reg_cat_unique.png").toString()
        ));
    }

    ImageView child;
    public ReagentCategoryCell(SelectionHandler<T> onClickHandler){

        super(Pos.CENTER, onClickHandler);
        child = new ImageView();
        child.setFitHeight(16);
        child.setSmooth(true);
        child.setPreserveRatio(true);
        child.setCache(true);

        setGraphic(child);

        setText("");

    }
    @Override
    public void updateItem(AlchemyReagentCategory item, boolean empty){

        super.updateItem(item, empty);

        child.setImage(images.get(item));
    }
}
