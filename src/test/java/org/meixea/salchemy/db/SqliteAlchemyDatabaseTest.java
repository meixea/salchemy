package org.meixea.salchemy.db;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.meixea.salchemy.model.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SqliteAlchemyDatabaseTest {
    @BeforeAll
    static void setup() {
        Model.initialize();
    }
    @Test
    void getAllAlchemyProperties() {

        List<AlchemyProperty> properties = AlchemyProperty.getAllProperties();

        assertEquals(55, properties.size());
        AlchemyProperty last = properties.get(properties.size() - 1);
        assertEquals(AlchemyType.NEGATIVE, last.getType());
        assertEquals("Уязвимость к яду", last.getName());
        assertEquals(330, last.getPrice());
        assertEquals(AlchemyPropertyCategory.POISON, last.getCategory());

    }
    @Test
    void getAllReagents() {

        List<Reagent> reagents = Reagent.getAllReagents();

        assertEquals(111, reagents.size());

        Reagent last = reagents.get(reagents.size() - 1);

        assertEquals("Яйцо ястреба", last.nameProperty().getValue());
        assertEquals(AlchemyReagentCategory.RARE, last.categoryProperty().getValue());

        assertTrue(last.hasProperty(AlchemyProperty.getProperty("Сопротивление магии")));
        assertTrue(last.hasProperty("Повреждение регенерации магии"));
        assertTrue(last.hasProperty("Водное дыхание"));
        assertTrue(last.hasProperty("Затяжной урон запасу сил"));
    }
}
