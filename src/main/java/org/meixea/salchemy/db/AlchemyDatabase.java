package org.meixea.salchemy.db;

import org.meixea.salchemy.model.AlchemyProperty;
import org.meixea.salchemy.model.MaxPriceSearch;
import org.meixea.salchemy.model.ModelData;
import org.meixea.salchemy.model.Reagent;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface AlchemyDatabase extends Closeable, AutoCloseable {

    String getReagent(int id);
    List<AlchemyProperty> getAllAlchemyProperties() throws SQLException;
    List<Reagent> getAllReagents() throws SQLException;
    ModelData loadModelData() throws SQLException;
    void deleteMaxPriceSearch(MaxPriceSearch search) throws SQLException;
    void saveMaxPriceSearch(MaxPriceSearch search) throws SQLException;
}
