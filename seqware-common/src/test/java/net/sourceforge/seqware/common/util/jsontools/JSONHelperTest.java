package net.sourceforge.seqware.common.util.jsontools;

import java.io.InputStream;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Unit tests
 *
 * @author Raunaq Suri
 */
public class JSONHelperTest {

    JSONHelper jman = new JSONHelper();

    /**
     * Checks to ensure that all required parameters are met
     */
    @Test(testName = "Missing required JSON parameter")
    public void missingRequired() {
        InputStream schema = this.getClass().getClassLoader().getResourceAsStream("schema.json");
        InputStream testData = this.getClass().getClassLoader().getResourceAsStream("missingField.json");
        Assert.assertNotNull(testData);
        Assert.assertFalse(jman.isJSONValid(schema, testData), "ERROR: JSON is valid, when it should be invalid");
    }

    /**
     * Checks to make sure that there are configurations for the tests in the
     * JSON
     */
    @Test(testName = "No Test Config")
    public void noTestConfig() {
        InputStream schema = this.getClass().getClassLoader().getResourceAsStream("schema.json");
        InputStream testData = this.getClass().getClassLoader().getResourceAsStream("noTestConfig.json");
        Assert.assertNotNull(testData);
        Assert.assertFalse(jman.isJSONValid(schema, testData), "ERROR: JSON is valid when it should be invalid");
    }

    /**
     * Checks to make sure that a null pointer exception is thrown if the files
     * don't exist
     */
    @Test(testName = "Bad Resource Names", expectedExceptions = NullPointerException.class)
    public void badResourceNames() {
        InputStream schema = this.getClass().getClassLoader().getResourceAsStream("schema.json");
        InputStream data = this.getClass().getClassLoader().getResourceAsStream("data.json");
        Assert.assertNull(jman.isJSONValid(this.getClass().getClassLoader().getResourceAsStream("Raunaq.json"), data), "ERROR: Schema should be null as the file doesn't exist");
        Assert.assertNull(jman.isJSONValid(schema, this.getClass().getClassLoader().getResourceAsStream("Suri.json")), "ERROR: Data should be null as the file doesn't exist");

    }

    /**
     * Checks to make sure that if the environment data is not written properly,
     * a false is returned
     */
    @Test(testName = "Environment Data Malformed")
    public void environmentDataMalformed() {
        InputStream schema = this.getClass().getClassLoader().getResourceAsStream("schema.json");
        InputStream testData = this.getClass().getClassLoader().getResourceAsStream("environmentMalformed.json");
        Assert.assertNotNull(testData);
        Assert.assertFalse(jman.isJSONValid(schema, testData));
    }

    /**
     * The data perfectly matches the schema
     */
    @Test(testName = "Everything is Perfect")
    public void everythingIsPerfect() {
        InputStream schema = this.getClass().getClassLoader().getResourceAsStream("schema.json");
        InputStream data = this.getClass().getClassLoader().getResourceAsStream("data.json");
        Assert.assertTrue(jman.isJSONValid(schema, data), "ERROR: Perfect match gave false instead of true");
    }
}
