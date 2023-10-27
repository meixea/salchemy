package org.meixea.salchemy.model;

import org.meixea.salchemy.db.AlchemyDatabase;
import org.meixea.salchemy.db.SqliteAlchemyDatabase;
import org.meixea.salchemy.view.AlchemyApplication;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

public class Main {

    static private String MAIN_DATABASE_FILENAME = "alchemy.s3db";
    public static void main(String[] args) {

        Model.initialize(MAIN_DATABASE_FILENAME);

        AlchemyApplication.main(args);

        Model.shutdown();

    }
}
