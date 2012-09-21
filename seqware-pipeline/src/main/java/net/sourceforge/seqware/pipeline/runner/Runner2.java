package net.sourceforge.seqware.pipeline.runner;

import net.sourceforge.seqware.common.err.OldSeqwareException;
import net.sourceforge.seqware.common.err.SeqwareException;
import net.sourceforge.seqware.common.err.SwRunnerParameterException;
import net.sourceforge.seqware.pipeline.module.ModuleInterface;
import net.sourceforge.seqware.pipeline.module.ann.NewSeqwareStyle;
import net.sourceforge.seqware.common.util.configtools.OptionParsing;
import net.sourceforge.seqware.common.util.exceptiontools.ExceptionTools;
import org.kohsuke.args4j.CmdLineException;
import org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.sourceforge.seqware.common.module.ReturnValue;

/**
 * This is a new Runner class that is designed (hopefuly) to replace the current Runner class.
 *
 * This Runner class is designed to be a Spring Container, which will instantiate and run the requested module.
 * In addition, it can also instantiate any other user desired object that will cross-cut with the module. Two typical
 * kinds of such objects are:
 *
 * (a) An object that will record the module's running status to some backend, e.g., the Seqware_meta_db
 * (b) An object that will redirect the stdout/stderr to a file
 *
 * Both of these objects shall be instantiated via a Spring Configuration object. And they interact with a module via
 * spring's Aspect-Oriented-Programming (AOP) capability.
 *
 * There is a default configuration class, currently "net.sourceforge.seqware.pipeline.runner.DefaultConfig", to simplify
 * writing the runner parameter. However, a user can use its own Configuration class to instantiate different AOP object
 * suitable for their need.
 *
 * Compared to the old Runner, there are some changes.
 *
 * (1) The Runner syntax will change. The basic syntax to pass parameters to the Runner is
 *
 * --namespace:option value
 *
 * The name of each bean (Spring's term for managed object) is the namespace. Each bean's name is defined in the
 * Configuration class. There are two reserved namespace. 'run' and 'module', the former is reserved for the Runner to
 * use and the latter is used to setup Module parameter. If the namespace is absent, it is default to 'module'.
 *
 * A basic Runner parameter will be
 *
 * --run:config net.sourceforge.seqware.pipeline.runner.DefaultConfig \
 * --run:module org.example.HelloWorld \
 * hello world parameters
 *
 * If user needs to specify some property on an instantiated AOP bean, it may look like
 *
 * --run:config net.sourceforge.seqware.pipeline.runner.DefaultConfig \
 * --run:module org.example.HelloWorld \
 * --redirect:stdout foo.bar.txt --redirect:stderr foo.bar.err \
 * hello world parameters
 *
 * The above parameter will be set on the redirect bean, instantiated by DefaultConfig. And by extension, the syntax
 * can be used to set properties on any object. The only dependency is the Runner2 uses args4j library (it sets a property
 * via annotation, which I like). If we don't want this library dependency, we will need to devise some interface to do so.
 *
 * (2) The Runner2 will work with an modified version of ModuleInterface. The old runner works with the concrete class
 * Module, as opposed to ModuleInterface, mainly -- I guess -- for the ability to set parameters on ModuleInterface as
 * well as get the name of the algorithm. Hence, I have added ModuleInterface with such two methods.
 *   - setParameter(List<String>)
 *   - getAlgorithm()
 * This allows me to code cleanly against an interface, yet without breaking backward compatibility.
 *
 * (3) The third change is to abandon the ReturnValue. I think the ReturnValue is designed probably for the MetaDB class.
 * It is very cubersome to code. In Runner2, we will use Exception to communicate its success or failure. If a method runs
 * successefully, nothing happens. If something wrong, throw a (subclass of) SeqwareException. The SeqwareException is
 * unchecked for making the implementation of ModuleInterface easier and cleaner. The Runner will catch this.
 *
 * (4) The forth change is to reverse the order of init() and do_verify_parameters(). The old runner do init() before
 * verify its parameters, so I think it is backward because without verifying parameters, how can a module init() (correctly?)
 *
 * But to make user adopt the above two changes without breaking backward compatibility, Module's implemented against
 * this new style must annotate its class with @NewSeqwareStyle. The Runner2 will check this annotation. If absent, it
 * will run the Module in the old way, i.e., checking ReturnValue and init() first and verify_parameter later.
 *
 *
 * User: Xiaoshu Wang (xiao@renci.org)
 * Date: 9/8/11
 * Time: 8:54 AM
 */
public class Runner2 {

    private final static String defaultConfigClz = "net.sourceforge.seqware.pipeline.runner.DefaultConfig";

    private List<String> moduleParameter;
    private ApplicationContext appContext;

    public void setModuleParameter(List<String> moduleParameter) {
        this.moduleParameter = moduleParameter;
    }

    public void run() throws CmdLineException {
//        ModuleInterface module = appContext.getBean(ModuleInterface.class);
//        module.setParameters(moduleParameter);
//
//        if (appContext.findAnnotationOnBean(RunnerParams.Bean.MODULE, NewSeqwareStyle.class)!= null) {
//            //New style, reverse the order of do_verify_parameters/init
//            module.do_verify_parameters();
//            module.init();
//            //the subsequent order is the same but without checking return value
//            module.do_verify_input();
//            module.do_run();
//            module.do_verify_output();
//            module.clean_up();
//        } else {
//            checkReturn(module.init());
//            checkReturn(module.do_verify_parameters());
//
//            checkReturn(module.do_verify_input());
//            checkReturn(module.do_run());
//            checkReturn(module.do_verify_output());
//            checkReturn(module.clean_up());
//        }
    }

    /**
     * This is to check the return value for running it in the old way
     * @param ret
     */
    private void checkReturn(ReturnValue ret) {
        if (ret.getExitStatus() > 0) {
            OldSeqwareException e = new OldSeqwareException(ret.getDescription());
            e.setExitCode(ret.getExitStatus());
            e.setReturnValue(ret);
            throw e;
        }
    }

    public static void main(String[] args) {
        RunnerParams runnerParams = null;
        int exitCode = 0;
        try {
            runnerParams = parseArgs(args);
            String configClz = runnerParams.get(RunnerParams.Bean.APP, "--config", true);
            if (configClz == null)  configClz = defaultConfigClz;

            String moduleName = runnerParams.get(RunnerParams.Bean.APP, "--module", true);

            AnnotationConfigApplicationContext context =
                    new AnnotationConfigApplicationContext(ModuleConfig.class, Class.forName(configClz));

            BeanDefinition modDef = new GenericBeanDefinition();
            modDef.setBeanClassName(moduleName);
            context.registerBeanDefinition(RunnerParams.Bean.MODULE, modDef);

            BeanDefinition appDef = new GenericBeanDefinition();
            appDef.setBeanClassName(Runner2.class.getName());
            context.registerBeanDefinition(RunnerParams.Bean.APP, appDef);

            //setting up beans
            for (String beanId : runnerParams.keys()) {
                if (!context.containsBean(beanId)) {
                    throw new SwRunnerParameterException("Unrecognizable namespace '" + beanId + "'.");
                }
                Object bean = context.getBean(beanId);

                if (!beanId.equals(RunnerParams.Bean.MODULE)) {
                    OptionParsing.parseOption(bean, runnerParams.getArgs(beanId));
                }
            }

//            Runner2 runner = context.getBean(Runner2.class);
//            runner.setApplicationContext(context);
//            runner.setModuleParameter(runnerParams.getArgs(RunnerParams.Bean.MODULE));
//            runner.run();

        } catch (Throwable t) {
            t.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            t.printStackTrace();
            //Organize error by checking if the cause is from any of SeqwareExceptions
            Throwable err = ExceptionTools.causedBy(t, SeqwareException.class);
            if (err != null) {
                exitCode = ((SeqwareException) err).getExitCode();
            } else {
                exitCode = -1;
            }
        }

        System.exit(exitCode);

    }

    private static RunnerParams parseArgs(String[] args) throws CmdLineException {
        RunnerParams runnerParams = new RunnerParams();
        Pattern paramPattern = Pattern.compile("--(\\w*):(.*)");

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.equals("--")) {//skip "--" because args4j cannot handle it
                continue;
            }
            Matcher matcher = paramPattern.matcher(arg);
            String groupKey = RunnerParams.Bean.MODULE;
            if (matcher.find()) {
                groupKey = matcher.group(1);
                arg = normalizeOptions(matcher.group(2));
            }
            runnerParams.add(groupKey, arg);
            if (i < args.length - 1) {
                String nextArg = args[i + 1];
                if (!nextArg.startsWith("-")) {
                    runnerParams.add(groupKey, nextArg);
                    i++;
                }
            }
        }

        return runnerParams;
    }


    public static String normalizeOptions(String origOpt) {
        String normOpt = origOpt;
        Pattern optPattern = Pattern.compile("-*(.*)");
        Matcher matcher = optPattern.matcher(origOpt);
        if (matcher.find()) {
            String opt = matcher.group(1).trim();
            if (opt.length() == 1) {
                normOpt = "-" + opt;
            } else {
                normOpt = "--" + opt;
            }
        } else {
            normOpt = "--" + origOpt;
        }
        return normOpt;
    }

//    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.appContext = applicationContext;
    }


    /**
     * This class is used to configure the instantiating of a module
     */
    @Configuration
    public static class ModuleConfig {
        @Bean
        public AnnotationAwareAspectJAutoProxyCreator annotationAwareAspectJAutoProxyCreator() {
            AnnotationAwareAspectJAutoProxyCreator aopConfig = new AnnotationAwareAspectJAutoProxyCreator();
            return aopConfig;
        }
    }
}
