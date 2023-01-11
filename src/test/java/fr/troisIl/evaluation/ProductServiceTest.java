package fr.troisIl.evaluation;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import static org.junit.Assert.*;

public class ProductServiceTest {

    private Database db = null;
    private ProductService productService;

    private int countBefore = 0;

    @Before
    public void setUp() throws SQLException {
        String testDatabaseFileName = "product.db";

        // reset la BDD avant le test
        File file = new File(testDatabaseFileName);
        file.delete();

        db = new Database(testDatabaseFileName);
        db.createBasicSqlTable();

        productService = new ProductService(db);

        countBefore = count();
    }

    /**
     * Compte les produits en BDD
     *
     * @return le nombre de produit en BDD
     */
    private int count() throws SQLException {
        ResultSet resultSet = db.executeSelect("Select count(*) from Product");
        assertNotNull(resultSet);
        return resultSet.getInt(1);
    }

    @Test
    public void testInsert() throws SQLException {
        ResultSet totalProductBeforeInsert = db.executeSelect("Select count(*) from Product");

        Product product = new Product(null, "Perceuse",2);

        Product dataProduct = productService.insert(product);
        Product dataInsertedProduct = productService.findById(product.getId());

        Assert.assertEquals(dataProduct.getId(), dataInsertedProduct.getId());
        Assert.assertEquals(dataProduct.getLabel(), dataInsertedProduct.getLabel());
        Assert.assertEquals(dataProduct.getQuantity(), dataInsertedProduct.getQuantity());

        ResultSet totalProductAfterInsert = db.executeSelect("Select count(*) from Product");

        Assert.assertNotSame(totalProductBeforeInsert, totalProductAfterInsert);

    }

    @Test
    public void testInsertProductNull() {
        try {
            productService.insert(null);
            Assert.fail("Le test aurait du crasher");
        } catch (RuntimeException e) {
            Assert.assertEquals("Le produit ne peut pas être null", e.getMessage());
        }
    }

    @Test
    public void testInsertLabelNull() {
        Product product = new Product(null, "null", 2);
        try {
            productService.insert(product);
            Assert.fail("Le test aurait du crasher");
        } catch (RuntimeException e) {
            Assert.assertEquals("Le libellé du produit est requis", e.getMessage());
        }
    }

    @Test
    public void testUpdate() throws SQLException {
        Product product = new Product(null, "Perceuse",2);
        productService.insert(product);

        ResultSet productInserted = db.executeSelect("Select * from Product");

        product.setLabel("Marteau");
        product.setQuantity(3);

        productService.update(product);

        ResultSet productUpdated = db.executeSelect("Select * from Product");

        assertEquals(productInserted.getInt("id"), productUpdated.getInt("id"));
        assertNotSame(productInserted.getString("label"), productUpdated.getString("label"));
        assertNotSame(productInserted.getInt("quantity"), productUpdated.getInt("quantity"));

    }

    @Test
    public void testUpdateProductNull() {
        try {
            productService.update(null);
            Assert.fail("Le test aurait du crasher");
        } catch (RuntimeException e) {
            Assert.assertEquals("Le produit ne peut pas être null", e.getMessage());
        }
    }

    @Test
    public void testUpdateLabelNull() {
        Product product = new Product(1, null, 2);
        try {
            productService.update(product);
            Assert.fail("Le test aurait du crasher");
        } catch (RuntimeException e) {
            Assert.assertEquals("Le libellé du produit est requis", e.getMessage());
        }
    }

    @Test
    public void testUpdateIdNull() {
        Product product = new Product(null, "Perceuse", 2);
        try {
            productService.update(product);
            Assert.fail("Le test aurait du crasher");
        } catch (RuntimeException e) {
            Assert.assertEquals("L'identifiant du produit est requis pour une modification", e.getMessage());
        }
    }

    @Test
    public void testUpdateQuantityNull() {
        Product product = new Product(1, "Perceuse", null);
        try {
            productService.update(product);
            Assert.fail("Le test aurait du crasher");
        } catch (RuntimeException e) {
            Assert.assertEquals("La quantité ne peut être nulle", e.getMessage());
        }
    }

    @Test
    public void testFindById() throws SQLException {
        Product product = new Product(null, "Perceuse",2);

        productService.insert(product);

        Product productFound = productService.findById(product.getId());

        ResultSet RequestProductFound = db.executeSelect("Select COUNT(*) from Product WHERE id = " + productFound.getId());

        assertNotEquals(0, RequestProductFound.getInt(1));
    }

    @Test
    public void testFindByIdIdNull() {
        try {
            productService.findById(null);
            Assert.fail("Le test aurait du crasher");
        } catch (RuntimeException e) {
            Assert.assertEquals("L'id du produit recherché est requis", e.getMessage());
        }
    }

    @Test
    public void testDelete() throws SQLException {
        Product product = new Product(null, "Perceuse",2);
        productService.insert(product);

        ResultSet productInserted = db.executeSelect("Select count(*) from Product");

        productService.delete(product.getId());

        ResultSet totalProducts = db.executeSelect("Select COUNT(*) from Product WHERE id = " + product.getId());

        assertNotSame(productInserted.getInt(1), totalProducts.getInt(1));

    }

    @Test
    public void testDeleteIdNull() {
        try {
            productService.delete(null);
            Assert.fail("Le test aurait du crasher");
        } catch (RuntimeException e) {
            Assert.assertEquals("L'id du produit à supprimer est requis", e.getMessage());
        }
    }

}
