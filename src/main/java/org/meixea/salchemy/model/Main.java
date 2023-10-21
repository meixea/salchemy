package org.meixea.salchemy.model;

import org.meixea.salchemy.db.AlchemyDatabase;
import org.meixea.salchemy.db.SqliteAlchemyDatabase;
import org.meixea.salchemy.view.AlchemyApplication;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

public class Main {
    public static void main(String[] args) {

        Model.initialize();

        AlchemyApplication.main(args);

        Model.shutdown();

    }
}
