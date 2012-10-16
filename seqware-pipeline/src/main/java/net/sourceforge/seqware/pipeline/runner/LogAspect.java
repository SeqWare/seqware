package net.sourceforge.seqware.pipeline.runner;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import java.util.List;
import net.sourceforge.seqware.common.util.Log;

/**
 * This is a test class
 *
 * Created by IntelliJ IDEA.
 * User: Xiaoshu Wang (xiao@renci.org)
 * Date: 8/10/11
 * Time: 10:02 PM
 *
 * @author boconnor
 * @version $Id: $Id
 */
@Aspect
public class LogAspect {

    private SimpleJdbcTemplate jdbcTemplate;
    private final static String processing_sequence = "'processing_processing_id_seq'";

    /**
     * <p>Setter for the field <code>jdbcTemplate</code>.</p>
     *
     * @param jdbcTemplate a {@link org.springframework.jdbc.core.simple.SimpleJdbcTemplate} object.
     */
    public void setJdbcTemplate(SimpleJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * <p>watchRunner.</p>
     *
     * @param joinPoint a {@link org.aspectj.lang.ProceedingJoinPoint} object.
     * @throws java.lang.Throwable if any.
     */
    @Around("bean(run) && execution(* net.sourceforge.seqware.pipeline.runner.Runner2.run(..))")
    public void watchRunner(ProceedingJoinPoint joinPoint) throws Throwable {
        Log.info("LogAspect.watchRunner");
        try {
            joinPoint.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            Log.error("Errors thrown");
            throw throwable;
        }
    }

    /**
     * <p>doRun.</p>
     */
    @Pointcut("bean(module) && execution(* net.sourceforge.seqware.pipeline.module.ModuleInterface.do_run(..))")
    public void doRun() {}

    /**
     * <p>watchModuleRun.</p>
     *
     * @param joinPoint a {@link org.aspectj.lang.ProceedingJoinPoint} object.
     * @throws java.lang.Throwable if any.
     */
    @Around("doRun()")
    public void watchModuleRun(ProceedingJoinPoint joinPoint) throws Throwable{
        Log.info("proId2accId(10) = " + proId2accId(10));
        try {
            joinPoint.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            Log.info("Module encounter error during run");
        }
    }

    private List logPrior2Run(){
        String sql = "select processing_id, description from processing";
        return jdbcTemplate.queryForList(sql);
    }

    /**
     * This method will add a row to processing table. I believe this is the first step that should be run
     * upon a new module running
     */
    private void add_empty_processing_event() {
        //We should call this method first
        String sql = "INSERT INTO processing(status, create_tstmp) VALUES('pending', now())";
        //then getting the current value of sequence 'processing_processing_id_seq' with the following sql
        String getProcessingId = "SELECT currval(" + processing_sequence + ")";
        //TODO: link the processing with any possible parent accessid
        //The above step is by searching tables - ius, lane, sequencer_run, processing - for matching accession #
        //If found, then insert a row into ius_processing, lane_processing, sequencer_run_processing, processing_relations

    }

    /**
     * This method find the matching sw_accession number from processing_id
     * @param proId
     * @return
     */
    private int proId2accId(int proId){
        String sql = "SELECT sw_accession from processing where processing_id = ?";
        return jdbcTemplate.queryForInt(sql, proId);
    }

}
