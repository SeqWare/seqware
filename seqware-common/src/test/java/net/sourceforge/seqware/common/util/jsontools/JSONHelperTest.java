package net.sourceforge.seqware.common.util.jsontools;

import org.testng.Assert;
import org.testng.annotations.Test;
/**
 * Unit tests
 * @author Raunaq Suri
 */
public class JSONHelperTest 
{
    JSONHelper jman = new JSONHelper();
    
    /**
     * Checks to ensure that all required parameters are met
     */
    @Test(testName = "Missing required JSON parameter")
    public void missingRequired(){
        Assert.assertFalse(jman.isJSONValid("schema.json", "missingField.json"), "ERROR: JSON is valid, when it should be invalid");
    }
    
    /**
     * Ensures that no key-value pair is a duplicate
     */
    @Test(testName = "Duplicate JSON fields")
    public void duplicateFields(){
        Assert.assertFalse(jman.isJSONValid("schema.json", "duplicate.json"), "ERROR: JSON is valid when it should be invalid");
    }
    
    /**
     * Checks to make sure that there are configurations for the tests in the JSON
     */
    @Test(testName="No Test Config")
    public void noTestConfig()
    {
        Assert.assertFalse(jman.isJSONValid("schema.json", "noTestConfig.json"), "ERROR: JSON is valid when it should be invalid");
    }
    
    /**
     * Checks to make sure that a null pointer exception is thrown if the files don't exist
     */
    @Test(testName="Bad Resource Names", expectedExceptions = NullPointerException.class)
    public void badResourceNames()
    {
        Assert.assertNull(jman.isJSONValid("Raunaq.json", "data.json"), "ERROR: Schema should be null as the file doesn't exist");
        Assert.assertNull(jman.isJSONValid("schema.json", "Suri.json"), "ERROR: Data should be null as the file doesn't exist");
        
    }
    
    /**
     * Checks to make sure that if the environment data is not written properly, a false is returned
     */
    @Test(testName="Environment Data Malformed")
    public void environmentDataMalformed()
    {
        Assert.assertFalse(jman.isJSONValid("schema.json", "environmentMalformed.json"));
    }
    
    /**
     * The data perfectly matches the schema
     */
    @Test(testName="Everything is Perfect")
    public void everythingIsPerfect()
    {
        Assert.assertTrue(jman.isJSONValid("schema.json", "data.json"), "ERROR: Perfect match gave false instead of true");
    }
}
