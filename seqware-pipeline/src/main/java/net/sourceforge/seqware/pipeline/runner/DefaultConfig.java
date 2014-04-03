package net.sourceforge.seqware.pipeline.runner;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import javax.activation.DataSource;

/**
 * Default Configuation class. This class is used to hookup things other than the module
 *
 * User: Xiaoshu Wang (xiao@renci.org)
 * Date: 8/10/11
 * Time: 9:01 PM
 *
 * @author boconnor
 * @version $Id: $Id
 */
@Configuration
public class DefaultConfig {

    /**
     * <p>logging.</p>
     *
     * @return a {@link net.sourceforge.seqware.pipeline.runner.LogAspect} object.
     * @throws java.lang.Throwable if any.
     */
    @Bean(name = RunnerParams.Bean.LOG)
    public LogAspect logging() throws Throwable {
        LogAspect log = new LogAspect();
        log.setJdbcTemplate(jdbcTemplate());
        return log;
    }

    /**
     * <p>redirect.</p>
     *
     * @return a {@link net.sourceforge.seqware.pipeline.runner.RedirectAspect} object.
     * @throws java.lang.Throwable if any.
     */
    @Bean(name = RunnerParams.Bean.REDIRECT)
    public RedirectAspect redirect() throws Throwable {
        return new RedirectAspect();
    }

    /**
     * <p>dataSource.</p>
     *
     * @return a {@link org.springframework.jdbc.datasource.SingleConnectionDataSource} object.
     */
    @Bean
    public SingleConnectionDataSource dataSource() {
        SingleConnectionDataSource dataSource = new SingleConnectionDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl("jdbc:postgresql://server:port/database");
        dataSource.setUsername("username");
        dataSource.setPassword("password");
        return dataSource;
    }

    /**
     * <p>jdbcTemplate.</p>
     *
     * @return a {@link org.springframework.jdbc.core.simple.SimpleJdbcTemplate} object.
     */
    @Bean
    public SimpleJdbcTemplate jdbcTemplate(){
        return new SimpleJdbcTemplate(dataSource());
    }
}
