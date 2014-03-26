package net.sourceforge.seqware.common.util.jsontools;

import antlr.RecognitionException;
import antlr.TokenStreamException;
import com.sdicons.json.model.JSONObject;
import com.sdicons.json.model.JSONValue;
import com.sdicons.json.parser.JSONParser;
import com.sdicons.json.validator.JSONValidator;
import com.sdicons.json.validator.ValidationException;
import com.sdicons.json.validator.Validator;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;

/**
 * JSON Helper Class
 * Contains methods related to parsing/validating of JSON which one may find useful
 * @author Raunaq Suri
 *
 */
public class JSONHelper {
    
    /**
     * Validates a json file against a schema to see if the file is valid
     * @param schemaJSON the json file which contains the schema
     * @param dataJSON  the json file which  you wish to validate
     * @return Whether or not the JSON is valid or not
     */
    public boolean isJSONValid(InputStream schemaJSON, InputStream dataJSON) {
        
        //Loads the schema and the data
        JSONParser schemaParser = new JSONParser(schemaJSON);
        JSONParser dataParser = new JSONParser(dataJSON);
        try {
            
            //Setup
            JSONObject validatorObj = (JSONObject) schemaParser.nextValue();

            Validator validator = new JSONValidator(validatorObj);

            JSONValue data = dataParser.nextValue();
            
            //Validates the data
            validator.validate(data);
            
        } catch (TokenStreamException ex) {
            Logger.getLogger(JSONHelper.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Token stream exception");
            System.err.println(ex.getMessage());
            return false;
            
        } catch (RecognitionException ex) {
            Logger.getLogger(JSONHelper.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Not recognized");
            System.err.println(ex.getMessage());
            return false;
        } catch (ValidationException ex) {
            Logger.getLogger(JSONHelper.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Not properly validated");
            System.err.println(ex.getMessage());
            return false;
        }
        
        
        //Returns true if there are no errors
        return true;
    }
}
