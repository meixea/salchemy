package org.meixea.salchemy.db;

import org.meixea.salchemy.model.*;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutorService;

public interface AlchemyDatabase extends Closeable, AutoCloseable {

    ModelData loadModelData() throws SQLException;
}
