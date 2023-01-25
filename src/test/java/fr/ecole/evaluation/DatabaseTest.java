package fr.ecole.evaluation;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;

public class DatabaseTest {

    private Database db = null;

    @Before
    public void setUp() {
        String testDatabaseFileName = "product.db";

        // reset la BDD avant le test
        File file = new File(testDatabaseFileName);
        file.delete();

        db = new Database(testDatabaseFileName);

    }

    @Test
    public void testDatabaseTable() throws SQLException {
        setUp();

        db.createBasicSqlTable();
        assertEquals("Product", db.showTable());
    }
}
