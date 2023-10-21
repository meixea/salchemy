package org.meixea.salchemy.view;

import org.meixea.salchemy.controller.ApplicationMode;
import org.meixea.salchemy.model.Model;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.Properties;

public class AppProperties extends Properties {
    static private final String PROPERTIES_FILENAME = "alchemy.properties";

    private boolean isRestored = false;

    public boolean restore() {

        if( isRestored )
            return true;

        Path propertiesPath = Path.of(Model.applicationPath, PROPERTIES_FILENAME);

        try(FileReader reader = new FileReader(propertiesPath.toString())) {

            load(reader);

        }
        catch( IOException e ){
            return false;
        }

        isRestored = true;
        return true;
    }
    public void save() {

        Model.getSavingThreadPool().execute( () -> {

            Path propertiesPath = Path.of(Model.applicationPath, PROPERTIES_FILENAME);

            try (PrintStream output = new PrintStream(new FileOutputStream(propertiesPath.toString()))) {

                list(output);

            } catch (IOException e) {
                System.out.println("Can't save properties");
            }

        });

    }
}
