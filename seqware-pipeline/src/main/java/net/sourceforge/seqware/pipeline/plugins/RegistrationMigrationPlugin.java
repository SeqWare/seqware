package net.sourceforge.seqware.pipeline.plugins;

import io.seqware.util.PasswordStorage;
import joptsimple.OptionException;
import net.sourceforge.seqware.common.factory.DBAccess;
import net.sourceforge.seqware.common.metadata.Metadata;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.pipeline.plugin.Plugin;
import net.sourceforge.seqware.pipeline.plugin.PluginInterface;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.openide.util.lookup.ServiceProvider;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This plugin migrates passwords from pre-1.2.x and generates password hashes for existing passwords while encrypting them.
 * 
 *
 * @author dyuen ProviderFor(PluginInterface.class)
 * @version $Id: $Id
 */
@ServiceProvider(service = PluginInterface.class)
public class RegistrationMigrationPlugin extends Plugin {

    public static final int HASH_BYTE_SIZE = 18;
    public static final int PBKDF2_ITERATIONS = 64000;
    public static final String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA1";


    /**
     * <p>
     * Constructor for HelloWorld.
     * </p>
     */
    public RegistrationMigrationPlugin() {
        super();
        parser.acceptsAll(Arrays.asList("help", "h", "?"), "Provides this help message.");
    }

    /**
     * {@inheritDoc}
     *
     * @param config
     */
    @Override
    public void setConfig(Map<String, String> config) {
        /**
         * explicitly no nothing
         */
    }

    /**
     * {@inheritDoc}
     *
     * @param params
     */
    @Override
    public void setParams(List<String> params) {
        this.params = params.toArray(new String[params.size()]);
    }

    /**
     * {@inheritDoc}
     *
     * @param metadata
     */
    @Override
    public void setMetadata(Metadata metadata) {
        // println("Setting Metadata: " + metadata);
        this.metadata = metadata;
    }

    /**
     * {@inheritDoc}
     *
     * @return
     */
    @Override
    public String get_syntax() {

        try {
            parser.printHelpOn(System.err);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Log.fatal(e);
        }
        return ("");
    }

    /**
     * {@inheritDoc}
     *
     * @return
     */
    @Override
    public ReturnValue parse_parameters() {
        ReturnValue ret = new ReturnValue();
        try {
            options = parser.parse(params);
        } catch (OptionException e) {
            get_syntax();
            ret.setExitStatus(ReturnValue.INVALIDARGUMENT);
        }
        return ret;
    }

    /**
     * {@inheritDoc}
     *
     * @return
     */
    @Override
    public ReturnValue init() {
        return new ReturnValue();
    }

    /**
     * {@inheritDoc}
     *
     * @return
     */
    @Override
    public ReturnValue do_test() {
        return new ReturnValue();
    }

    /**
     * {@inheritDoc}
     *
     * @return
     */
    @Override
    public ReturnValue do_run() {
        QueryRunner runner = new QueryRunner();
        try(Connection db = DBAccess.get().getDb()) {
            // create password salts where not present, then hash the password
            final List<Map<String, Object>> mapList = runner.query(db, "select * from registration", new MapListHandler());
            for(Map<String, Object> row : mapList){
                final String password = (String) row.get("password");
                if (!password.startsWith("sha1:")){
                    // generate a salt and save it
                    final int registrationId = (int) row.get("registration_id");
                    // Hash the password
                    final String hash = PasswordStorage.createHash(password);
                    runner.update(db, "update registration set password = '" + hash + "' where registration_id = " + registrationId);
                }
            }
        } catch (SQLException e) {
            Log.fatal("Could not close DB connection");
            Logger.getLogger(RegistrationMigrationPlugin.class.getName()).log(Level.SEVERE, null, e);
            return new ReturnValue(ReturnValue.FAILURE);
        } catch (PasswordStorage.CannotPerformOperationException e) {
            Log.fatal("Could not hash passwords");
            Logger.getLogger(RegistrationMigrationPlugin.class.getName()).log(Level.SEVERE, null, e);
            return new ReturnValue(ReturnValue.FAILURE);
        }

        return new ReturnValue();
    }

    /**
     * {@inheritDoc}
     *
     * @return
     */
    @Override
    public ReturnValue clean_up() {
        return new ReturnValue();
    }

    /**
     *
     * @return
     */
    @Override
    public String get_description() {
        return ("This plugin migrates passwords from pre-1.2.x and generates password hashes for existing passwords while encrypting them.");
    }

    public static void main(String[] args) {
        RegistrationMigrationPlugin mp = new RegistrationMigrationPlugin();
        mp.init();
        List<String> arr = new ArrayList<>();
        mp.setParams(arr);
        mp.parse_parameters();
        mp.do_run();
    }
}
