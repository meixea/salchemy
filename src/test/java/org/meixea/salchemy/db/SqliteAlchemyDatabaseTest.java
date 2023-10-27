package org.meixea.salchemy.db;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.meixea.salchemy.model.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SqliteAlchemyDatabaseTest {
    static private String TEST_DATABASE_FILENAME = "test.s3db";
    @BeforeAll
    static void setup() {
        Model.initialize(TEST_DATABASE_FILENAME);
    }
    @Test
    void loadAllAlchemyProperties() {

        List<AlchemyProperty> properties = Model.modelData.alchemyProperties;

        assertEquals(55, properties.size());
        AlchemyProperty last = properties.get(properties.size() - 1);
        assertEquals(AlchemyType.NEGATIVE, last.getType());
        assertEquals("Уязвимость к яду", last.getName());
        assertEquals(330, last.getPrice());
        assertEquals(AlchemyPropertyCategory.POISON, last.getCategory());

    }
    @Test
    void loadAllReagents() {

        List<Reagent> reagents = Model.modelData.alchemyReagents;

        assertEquals(111, reagents.size());

        Reagent last = reagents.get(reagents.size() - 1);

        assertEquals("Яйцо ястреба", last.nameProperty().getValue());
        assertEquals(AlchemyReagentCategory.RARE, last.categoryProperty().getValue());

        assertTrue(last.hasProperty(Model.modelData.getProperty("Сопротивление магии")));
        assertTrue(last.hasProperty(Model.modelData.getProperty("Повреждение регенерации магии")));
        assertTrue(last.hasProperty(Model.modelData.getProperty("Водное дыхание")));
        assertTrue(last.hasProperty(Model.modelData.getProperty("Затяжной урон запасу сил")));
    }
    @Test
    void initialDataLoading(){
        assertEquals(1, Model.modelData.maxPriceSearches.get(0).getId());
        assertEquals("Поиск 1", Model.modelData.maxPriceSearches.get(0).nameProperty().getValue());
    }
    @Test
    void savingMaxPriceChanges() throws SQLException, InterruptedException {

        int oldBagsSize = Model.modelData.reagentBags.size();
        int oldPricesSize = Model.modelData.maxPriceSearches.size();

        MaxPriceSearch search = Model.createMaxPriceSearch();
        search.bagProperty().getValue().add(Model.modelData.getReagent("Двемерское масло"), 30);
        search.bagProperty().getValue().add(Model.modelData.getReagent("Кабаний клык"), 40);
        search.bagProperty().getValue().add(Model.modelData.getReagent("Желе нетча"), 50);

        assertEquals(oldBagsSize + 1, Model.modelData.reagentBags.size());
        assertEquals(oldPricesSize + 1, Model.modelData.maxPriceSearches.size());
        assertEquals(3, search.bagProperty().getValue().getReagents().size());

        Thread.sleep(1000);
        Model.modelData = Model.getDatabase().loadModelData();

        assertEquals(oldBagsSize + 1, Model.modelData.reagentBags.size());
        assertEquals(oldPricesSize + 1, Model.modelData.maxPriceSearches.size());

        MaxPriceSearch reloaded = Model.modelData.getMaxPriceSearch(search.getId());

        assertEquals(search.nameProperty().getValue(), reloaded.nameProperty().getValue());
        assertEquals(3, reloaded.bagProperty().getValue().getReagents().size());
        int count1 = 0;
        int count2 = 0;
        int count3 = 0;
        for(ReagentInBag rib : reloaded.bagProperty().getValue().getReagents()) {
            switch (rib.getReagent().nameProperty().getValue()){
                case "Двемерское масло":
                    count1++;
                    assertEquals(30, rib.quantityProperty().getValue());
                    assertEquals(30, rib.surplusProperty().getValue());
                    break;
                case "Кабаний клык":
                    count2++;
                    assertEquals(40, rib.quantityProperty().getValue());
                    assertEquals(40, rib.surplusProperty().getValue());
                    break;
                case "Желе нетча":
                    count3++;
                    assertEquals(50, rib.quantityProperty().getValue());
                    assertEquals(50, rib.surplusProperty().getValue());
                    break;
            }
        }
        assertEquals(1, count1);
        assertEquals(1, count2);
        assertEquals(1, count3);

        Model.deleteMaxPriceSearch(reloaded);

        assertEquals(oldBagsSize, Model.modelData.reagentBags.size());
        assertEquals(oldPricesSize, Model.modelData.maxPriceSearches.size());

        Thread.sleep(1000);

        Model.modelData = Model.getDatabase().loadModelData();

        assertEquals(oldBagsSize, Model.modelData.reagentBags.size());
        assertEquals(oldPricesSize, Model.modelData.maxPriceSearches.size());

    }
    @Test
    void reagentInBagOperatorTest(){

    }
}
