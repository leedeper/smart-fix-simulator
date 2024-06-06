package smart.fixsimulator;

import io.allune.quickfixj.spring.boot.starter.EnableQuickFixJServer;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import quickfix.Acceptor;

import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import quickfix.Session;
import quickfix.SessionID;
import smart.fixsimulator.dao.MessageLogMapper;
import smart.fixsimulator.fixacceptor.MyJdbcLog;

import java.util.List;
import java.util.Map;

@MapperScan(basePackages = "smart.fixsimulator.dao")
@SpringBootApplication
public class SmartFixSimulatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartFixSimulatorApplication.class, args);
	}

	@Component
	@Slf4j
	public static class TestApplicationListener implements ApplicationListener<ApplicationStartedEvent> {

		@Autowired
		private MessageLogMapper messageLogMapper;

		@Override
		public void onApplicationEvent(ApplicationStartedEvent applicationStartedEvent) {
			log.info("onApplicationEvent, do someting {}",messageLogMapper.toString());
			Class<?> clazz = Session.class;
			try {
				java.lang.reflect.Field ss = clazz.getDeclaredField("sessions");
				ss.setAccessible(true);
				Map map =(Map)ss.get(null);
				map.values().forEach(e->((MyJdbcLog)((Session)e).getLog()).setMessageLogMapper(messageLogMapper));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

	}

}
