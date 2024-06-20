/*
 * Copyright 2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Send email to lyziuu@gmail.com for any question.
 *
 */

package smart.fixsimulator.fixacceptor;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import quickfix.*;

import javax.sql.DataSource;


/**
 * fix acceptor init
 *
 * @author Leedeper
 */
@Configuration
public class FixAcceptorConfiguration {

    @Value("classpath:h2db/scripts.sql")
    private Resource dataScript;

    @Value("${simulator.cfg.path:./simulator.cfg}")
    private String simulatorCfgPath;

    @Value("${quickfix.messageStoreFactory.type:file}")
    private String messageStoreFactoryType;

    @Bean
    public Application serverApplication() {
        return new FixApplicationAdapter(simulatorCfgPath);
    }

    @Bean
    public Acceptor serverAcceptor(quickfix.Application serverApplication, MessageStoreFactory serverMessageStoreFactory,
                                   SessionSettings serverSessionSettings, LogFactory serverLogFactory,
                                   MessageFactory serverMessageFactory) throws ConfigError {

        return new ThreadedSocketAcceptor(serverApplication, serverMessageStoreFactory, serverSessionSettings,
                serverLogFactory, serverMessageFactory);
    }

    @Bean
    public MessageStoreFactory serverMessageStoreFactory(SessionSettings serverSessionSettings, DataSource dataSource) {
        MessageStoreFactory messageStoreFactory;
        if(messageStoreFactoryType.equals("db")){
            JdbcStoreFactory jdbcStoreFactory = new JdbcStoreFactory(serverSessionSettings);
            jdbcStoreFactory.setDataSource(dataSource);
            return jdbcStoreFactory;
        }else{
           return new FileStoreFactory(serverSessionSettings);
        }
    }

    @Bean
    public LogFactory serverLogFactory(SessionSettings serverSessionSettings) {
        return new MyJdbcLogFactory(serverSessionSettings);
    }

    @ConfigurationProperties(prefix = "spring.datasource")
    @Bean
    @Primary
    public DataSource dataSource() {
        return new DruidDataSource();
    }


    @Bean
    public DataSourceInitializer dataSourceInitializer(DataSource dataSource) {
        DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(dataSource);
        initializer.setDatabasePopulator(databasePopulator());
        return initializer;
    }

    private DatabasePopulator databasePopulator() {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(dataScript);
        return populator;
    }

}
