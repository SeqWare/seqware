package net.sourceforge.seqware.pipeline.module;



/*
 * Methods that are not implemented can return ReturnValue.NOTIMPLEMENTED (-1).
 * The default runner will skip over these steps, and only fail on error if a method returns > 0.
 */

import java.util.List;
import net.sourceforge.seqware.common.module.ReturnValue;

/**
 *<p>
 * This interface defines the required behavior of a SeqWare module.
 * Essentially it assumes a life-cycle made up of several phases for a module,
 * specifying a function for each of these phase that an implementation
 * overrides to provide the code executed during that phase. The phases are:
 * </p>
 * <ol>
 * 	<li>{@link ModuleInterface.init() init()}</li>
 * 	<li>{@link ModuleInterface.do_verify_parameters() do_verify_parameters()}</li>
 * 	<li>{@link ModuleInterface.do_verify_input() do_verify_input()}</li>
 * 	<li>{@link ModuleInterface.do_test() do_test()}</li>
 * 	<li>{@link ModuleInterface.do_run() do_run()}</li>
 * 	<li>{@link ModuleInterface.do_verify_output() do_verify_output()}</li>
 * 	<li>{@link ModuleInterface.clean_up() clean_up()}</li>
 * </ol>
 * <p>Information about the the success or
 * failure of each step is packaged into a {@link net.sourceforge.seqware.common.module.ReturnValue} object,
 * including anything sent to STDOUT and STDERR.
 * </p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public interface ModuleInterface {

    /**
     * <p>setParameters.</p>
     *
     * @param params a {@link java.util.List} object.
     */
    public void setParameters(List<String> params);

    /**
     * <p>getAlgorithm.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getAlgorithm();

	/**
	 * Generates a "help" message for the user of the module. This will
	 * be called by [TODO: what?] to provide information about using the
	 * module. It is up to the implementation to defined the format for the
	 * returned message. This is not part of the normal life-cycle of a
	 * module, but represents an "alternate" life, where the module is
	 * called just to provide a self-description.
	 *
	 * @return Description of how to use this module.
	 */
	String get_syntax();


	/*
	 * Init is called before the process is run and marked in the
	 * metadata DB. It is used for pre-processing which is rare.
	 * An example is bfast changes its name to distinguish bfast_match from
	 * bfast_localalign in the MetaDB despite the same module handling both.
	 */

	// [TODO: Bfast example?]
	/**
	 * Module initialization code goes here. This is the first phase of the
	 * module lifecycle run. Might do things here like set up database
	 * access, create temp files, define defaults, etc. Preprocessing can be
	 * done here, although normally processing should be done by/during
	 * do_run().
	 *
	 * @return Success or failure info from this phase, along with any
	 * console output.
	 */
	ReturnValue init();

	/**
	 * Parameter validation code goes here. Use to make sure all required
	 * parameters are available, that parameters are known, that they have
	 * reasonable values, etc. Validation involving multiple options is best
	 * deferred to the next step where the existence and content of input
	 * data files will be checked, {@link ModuleInterface.do_verify_input()
	 * do_verify_input()}.
	 *
	 * @return Success or failure info from this phase, along with any
	 * console output.
	 */
	ReturnValue do_verify_parameters();

	/**
	 * Input data validation code goes here. Use to make sure that input
	 * file exist that needed output directories exist and can be written
	 * to, or can be created, etc. Modules should fail up front when possible
	 * to avoid wasting large amounts of processing time. This is also the
	 * place to validate combinations of parameters (individual parameters and
	 * their values are validated in the previous step,
	 * {@link ModuleInterface.do_verify_parameters() do_verify_parameters()}.
	 *
	 * @return Success or failure info from this phase, along with any
	 * console output.
	 */
	ReturnValue do_verify_input();

	// [TODO: consider using test life-cycle, do_test_verify_input, like Maven.]
	/**
	 * Perform any active system testing here. Use to make sure a DB is
	 * only, [TODO: Huh?], that a command line tools can be run, etc.
	 * You can even write functional tests that run the program you're
	 * wrapping on a small known good and then verify the output.
	 *
	 * @return Success or failure info from this phase, along with any
	 * console output.
	 */
	ReturnValue do_test();

	/**
	 * Performs the main tasks for the module. This is where you actually
	 * execute your task, run the external program, etc.
	 *
	 * @return Success or failure info from this phase, along with any
	 * console output.
	 */
	ReturnValue do_run();

	/**
	 * Perform post-task tests here. Use to check that expected
	 * directories exist, that files were not empty unexpectedly, etc.
	 *
	 * In practice, this check should be light-weight. If the output appears
	 * to be fine, then assume it is. Let the next module's input checking do
	 * any deep verification. Otherwise, everything gets checked twice and
	 * that can waste a lot of time. If it is a quick check, go ahead and do
	 * it. Note: Long/hard to do QC checks might deserve their own module!
	 *
	 * @return Success or failure info from this phase, along with any
	 * console output.
	 */
	ReturnValue do_verify_output();

	/**
	 * Perform post-task clean-up here. Use to remove temporary files and
	 * directories, flush buffers and db connections, etc.
	 *
	 * @return Success or failure info from this phase, along with any
	 * console output.
	 */
	ReturnValue clean_up();
}
