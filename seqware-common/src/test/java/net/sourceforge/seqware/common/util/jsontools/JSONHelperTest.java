package net.sourceforge.seqware.common.util.jsontools;

import java.io.InputStream;
import org.junit.Assert;
import org.junit.Test;

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
    @Test
    public void missingRequired() {
        InputStream schema = this.getClass().getClassLoader().getResourceAsStream("schema.json");
        InputStream testData = this.getClass().getClassLoader().getResourceAsStream("missingField.json");
        Assert.assertNotNull(testData);
        Assert.assertFalse("ERROR: JSON is valid, when it should be invalid", jman.isJSONValid(schema, testData));
    }

    /**
     * Checks to make sure that there are configurations for the tests in the JSON
     */
    @Test
    public void noTestConfig() {
        InputStream schema = this.getClass().getClassLoader().getResourceAsStream("schema.json");
        InputStream testData = this.getClass().getClassLoader().getResourceAsStream("noTestConfig.json");
        Assert.assertNotNull(testData);
        Assert.assertFalse("ERROR: JSON is valid when it should be invalid", jman.isJSONValid(schema, testData));
    }

    /**
     * Checks to make sure that a null pointer exception is thrown if the files don't exist
     */
    @Test(expected = NullPointerException.class)
    public void badResourceNames() {
        InputStream schema = this.getClass().getClassLoader().getResourceAsStream("schema.json");
        InputStream data = this.getClass().getClassLoader().getResourceAsStream("data.json");
        Assert.assertNull("ERROR: Schema should be null as the file doesn't exist",
                jman.isJSONValid(this.getClass().getClassLoader().getResourceAsStream("Raunaq.json"), data));
        Assert.assertNull("ERROR: Data should be null as the file doesn't exist",
                jman.isJSONValid(schema, this.getClass().getClassLoader().getResourceAsStream("Suri.json")));

    }

    /**
     * Checks to make sure that if the environment data is not written properly, a false is returned
     */
    @Test
    public void environmentDataMalformed() {
        InputStream schema = this.getClass().getClassLoader().getResourceAsStream("schema.json");
        InputStream testData = this.getClass().getClassLoader().getResourceAsStream("environmentMalformed.json");
        Assert.assertNotNull(testData);
        Assert.assertFalse(jman.isJSONValid(schema, testData));
    }

    /**
     * The data perfectly matches the schema
     */
    @Test
    public void everythingIsPerfect() {
        InputStream schema = this.getClass().getClassLoader().getResourceAsStream("schema.json");
        InputStream data = this.getClass().getClassLoader().getResourceAsStream("data.json");
        Assert.assertTrue("ERROR: Perfect match gave false instead of true", jman.isJSONValid(schema, data));
    }
}
