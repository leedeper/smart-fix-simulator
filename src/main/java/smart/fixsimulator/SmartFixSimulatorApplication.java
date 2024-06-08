package smart.fixsimulator;

import org.mybatis.spring.annotation.MapperScan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.context.support.WebApplicationContextUtils;

@MapperScan(basePackages = "smart.fixsimulator.dao")
@SpringBootApplication
public class SmartFixSimulatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartFixSimulatorApplication.class, args);
	}
/*
	// dirty implement method, I'v no idea for auto config. It'll be deleted
	@org.springframework.stereotype.Component
	@lombok.extern.slf4j.Slf4j
	public static class TestApplicationListener implements
			org.springframework.context.ApplicationListener
					<org.springframework.boot.context.event.ApplicationStartedEvent> {

		@org.springframework.beans.factory.annotation.Autowired
		private smart.fixsimulator.dao.MessageLogMapper messageLogMapper;

		@Override
		public void onApplicationEvent(org.springframework.boot.context.event.ApplicationStartedEvent applicationStartedEvent) {

			Class<?> clazz = quickfix.Session.class;
			try {
				java.lang.reflect.Field ss = clazz.getDeclaredField("sessions");
				ss.setAccessible(true);
				java.util.Map map =(java.util.Map)ss.get(null);
				map.values().forEach(e->((smart.fixsimulator.fixacceptor.MyJdbcLog)((quickfix.Session)e).getLog()).setMessageLogMapper(messageLogMapper));
			} catch (Exception e) {
				log.error("",e);
				throw new RuntimeException(e);
			}
		}

	}*/

}
