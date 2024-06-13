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

package smart.fixsimulator.fixacceptor.core;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import quickfix.*;
import smart.fixsimulator.common.ApplicationContextUtils;
import smart.fixsimulator.fixacceptor.core.buildin.BuiltinGeneratorMapping;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * distribute the fix app message to simulator
 *
 * @author Leedeper
 */
@Slf4j
public class Distributer {
    private List<Generator> generator;
    private static String RULER_SPEL = "spEL";
    private static String RULER_MSGTYPE="msgType";
    private static String GEN_FLAG = "generator.";
    private LinkedHashMap<String, GeneratorWrapper> allGen = new LinkedHashMap<>();
    private Ruler ruler;
    private Loop loop;
    private enum DistributerParam{
        Rule("rule"),
        Type("type"),
        LoopInitialDelay("loop.initialDelay"),
        LoopDelay("loop.delay"),
        LoopCount("loop.count"),
        LoopTimeUnit("loop.timeUnit"),
        LoopIdExpression("loop.idExpression");

        private String keyName;
        DistributerParam(String keyName) {
            this.keyName=keyName;
        }
        public String getKeyName(){
            return keyName;
        }
        public boolean contain(String name){
            DistributerParam dp = Arrays.stream(DistributerParam.values())
                    .filter(e->e.getKeyName().equals(name)).findFirst().orElse(null);
            return dp != null;
        }
    }

    public Distributer(String simulatorCfgPath){
        init(simulatorCfgPath);
    }


    public void process(Message message, SessionID sessionId){
        final List<String> matchedGenerator = new ArrayList<>();
        allGen.values().forEach(e->{
            log.debug("process by generator {}",e.getName());
            try{
                boolean matched = processMsg(message,sessionId,e);
                if(matched){
                    matchedGenerator.add(e.getName());
                }
            }catch (Throwable ex){
                log.error("Exception when process message in session {}",sessionId, ex);
            }
        });
        if(matchedGenerator.isEmpty()){
            log.warn("No generator matched for this message, maybe the config is wrong.");
        }else{
            log.info("This message is processed by {}", matchedGenerator);
        }
    }
    private boolean processMsg(Message message, SessionID sessionId, GeneratorWrapper gw){
        boolean matched = ruler.match(message, sessionId, gw.rule);
        log.debug("generator {} is matched ? {}",gw.getName(), matched);
        if(matched){
            if(gw.isLoop()){
                log.debug("it's a loop generator, add message to scheduler queue");
                loop.addTask(message,sessionId,gw);
            }else{
                createAndSendMessage(message, sessionId, gw);
            }
        }
        return matched;
    }
    private void createAndSendMessage(Message message, SessionID sessionId, GeneratorWrapper gw){
        Message reply;
        try {
            reply = gw.generator.create(message, sessionId);
            Session.sendToTarget(reply, sessionId);
        } catch (quickfix.SessionNotFound ex) {
            log.error("Handler error, can't  find quickfix.SessionNotFound, generator = {}, sessionId = {}"
                    , gw.name, sessionId);
            throw new RuntimeException(ex);
        }
    }

/*    private void sendMsg(SessionID sessionId, Message replay) throws FieldNotFound, SessionNotFound {
        //replay.reverseRoute(msg.getHeader());
        Session.sendToTarget(replay, sessionId);
    }*/

    public void destory(){
        allGen.values().forEach(e->{e.generator.destroy();});
    }
    private void init(String simulatorCfgPath){
        Properties properties = loadProperties(simulatorCfgPath);

        if(properties==null){
            return;
        }

        initRuler(properties);
        initGenerator(properties);
        initLoop(properties);

    }

    private void initLoop(Properties properties){
        int poolSize = Integer.valueOf(properties.getProperty("loop.poolSize","5"));
        loop = new Loop(poolSize);
    }

    private void initRuler(Properties properties){
        String rType = properties.getProperty("rule.type",RULER_MSGTYPE);
        // support more in the future
        if(rType.equals(RULER_MSGTYPE)){
            ruler =new MsgTypeRuler();
        }else if(rType.equals(RULER_SPEL)){
            ruler =new SPELRuler();
        }else{
            log.warn("unsupported ruler type , use default {}", RULER_MSGTYPE);
            ruler =new MsgTypeRuler();
        }

    }

    private void initGenerator(Properties properties){
        Map<String, Properties> gens = assemble(properties);
        createAllGenerator(gens);
    }

    private void createAllGenerator(Map<String, Properties> gens){
        gens.entrySet().forEach(e->{
            String name = e.getKey();
            Properties properties = e.getValue();
            String type = properties.getProperty(DistributerParam.Type.getKeyName());

            Class clazz = BuiltinGeneratorMapping.mapping.get(type);
            if(clazz == null) {
                // It should be a class which implements Generator
                try {
                    clazz = Class.forName(type);
                } catch (ClassNotFoundException ex) {
                    log.error("The rule type is not build-in, so pls define a full path class as it", ex);
                    throw new RuntimeException(ex);
                }
            }

            Generator gen = (Generator)ApplicationContextUtils.newAutoWiredInstance(clazz);
            GeneratorWrapper wrapper = new GeneratorWrapper();
            wrapper.generator = gen;
            wrapper.type = type;
            wrapper.name = name;
            wrapper.rule=properties.getProperty(DistributerParam.Rule.getKeyName());

            // if <=0, then don't loop
            wrapper.loopDelay = getLongFromPropertiesDefaultZero(DistributerParam.LoopDelay,properties);
            wrapper.loopCount = getLongFromPropertiesDefaultZero(DistributerParam.LoopCount,properties);
            wrapper.loopInitialDelay = getLongFromPropertiesDefaultZero(DistributerParam.LoopInitialDelay,properties);
            if(wrapper.loopInitialDelay<=0){
                // maybe loopDelay<=0, then don't loop, so it's safe
                wrapper.loopInitialDelay = wrapper.loopDelay;
            }
            wrapper.loopIdExpression = properties.getProperty(DistributerParam.LoopIdExpression.getKeyName());
            if(wrapper.isLoop() && wrapper.loopCount <= 0 && wrapper.isNoIdExpression()){
                log.warn("{} is infinite loop, but no loopIdExpression, it will not be stopped", wrapper.name);
            }
            String unitType= properties.getProperty(DistributerParam.LoopTimeUnit.getKeyName());
            wrapper.loopTimeUnit = getTimeUnit(unitType);


            Arrays.stream(DistributerParam.values()).forEach(v->properties.remove(v.getKeyName()));
            log.info("init a generator {} with parameter {}", wrapper.name,properties);
            gen.init(properties);

            allGen.put(wrapper.name,wrapper);
            log.info("Success to create a generator {}",wrapper.name);

        });
    }

    private TimeUnit getTimeUnit(String unitType){
        TimeUnit unit = TimeUnit.MILLISECONDS;
        if(ChronoUnit.NANOS.toString().equals(unitType)){
            return TimeUnit.NANOSECONDS;
        } else if(ChronoUnit.MICROS.toString().equals(unitType)){
            return TimeUnit.MICROSECONDS;
        }else if(ChronoUnit.MILLIS.toString().equals(unitType)){
            return TimeUnit.MILLISECONDS;
        }else if(ChronoUnit.SECONDS.toString().equals(unitType)){
            return TimeUnit.SECONDS;
        }else if(ChronoUnit.MINUTES.toString().equals(unitType)){
            return TimeUnit.MINUTES;
        }else if(ChronoUnit.HOURS.toString().equals(unitType)){
            return TimeUnit.HOURS;
        }else{
            log.warn("Unsupported Time Unit for {}, regard as MILLISECONDS",unitType);
            return TimeUnit.MILLISECONDS;
        }
    }

    private Long getLongFromPropertiesDefaultZero(DistributerParam key, Properties properties) {
        return getLongFromProperties(key,0L,properties);
    }
    private Long getLongFromProperties(DistributerParam key, Long defaultValue, Properties properties){
        return Long.parseLong(properties.getProperty(key.getKeyName(), String.valueOf(defaultValue)));
    }

    private Map<String, Properties> assemble(Properties properties){
        Map<String, Properties> gens = new HashMap<>();
        properties.entrySet().forEach(e->{
            String k = (String) e.getKey();
            String v = (String) e.getValue();
            if(k.startsWith(GEN_FLAG)){
                String sub = k.substring(GEN_FLAG.length());
                int inx = sub.indexOf('.');
                if(inx==-1){
                    log.warn("Invalid generator parameter - {}",k);
                    return;
                }
                String name=sub.substring(0,inx);
                String key = sub.substring(inx+1);

                Properties p = gens.get(name);
                if(p==null){
                    p = new Properties();
                    gens.put(name,p);
                }
                p.setProperty(key,v);
            }
        });
        log.debug("all parameter in distributer wrapper {}",gens);
        return gens;

    }

    private Properties loadProperties(String simulatorCfgPath){
        Properties properties = new Properties();
        try {
            FileInputStream inputStream  = new FileInputStream(simulatorCfgPath);
            properties.load(inputStream);
        } catch (IOException e) {
            log.warn("Load confg {} error, don't start simulator to work.",simulatorCfgPath,e);
            return null;
        }
        return properties;
    }


    @Data
    protected static class GeneratorWrapper{
        private String rule;
        private String name;
        private String type;
        private long loopInitialDelay;
        private long loopDelay;
        private long loopCount;
        private TimeUnit loopTimeUnit;
        private String loopIdExpression;
        private Generator generator;

        public boolean isLoop(){
            return loopDelay > 0;
        }

        public boolean isNoIdExpression(){
            return StringUtils.isEmpty(loopIdExpression);
        }

    }

}
