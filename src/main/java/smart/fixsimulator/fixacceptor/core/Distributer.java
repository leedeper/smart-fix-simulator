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

import lombok.extern.slf4j.Slf4j;
import quickfix.*;
import smart.fixsimulator.common.ApplicationContextUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

/**
 * distribute the fix app message to simulator
 *
 * @author Leedeper
 */
@Slf4j
public class Distributer {
    private List<Generator> generator;
    private static String RULER_SPEL = "spEL";
    private static String GEN_FLAG = "generator.";
    private LinkedHashMap<String, GeneratorWrapper> allGen = new LinkedHashMap<>();
    private Ruler ruler;
    private enum DistributerParam{
        Rule("rule"),Type("type"),Wait("wait");
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
        List<Message> preMsg=new ArrayList<>();
        allGen.values().forEach(e->{
            if(ruler.match(message, sessionId, e.rule)){
                Message reply;
                try {
                    reply = e.generator.create(message, sessionId, preMsg);
                    Session.sendToTarget(reply, sessionId);
                } catch (Throwable ex) {
                    log.error("Handler error, generator = {}, msg = {}", e.name, message);
                    log.debug("original exception ",ex);
                    return;
                }
                preMsg.add(reply);
            }
        });
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

        setRuleType(properties);
        setGenerator(properties);
    }

    private void setRuleType(Properties properties){
        String rType = properties.getProperty("rule.type",RULER_SPEL);
        // support more in the future
        if(rType.equals(RULER_SPEL)){
            ruler =new SPELRuler();
        }else{
            log.warn("unsupported ruler type , use default {}", RULER_SPEL);
            ruler =new SPELRuler();
        }

    }

    private void setGenerator(Properties properties){
        Map<String, Properties> gens = assemble(properties);
        createAllGenerator(gens);
    }

    private void createAllGenerator(Map<String, Properties> gens){
        gens.entrySet().forEach(e->{
            String name = e.getKey();
            Properties properties = e.getValue();
            String type = properties.getProperty(DistributerParam.Type.getKeyName());

            Class clazz = BuiltinGeneratorMapping.mapping.get(type);
            if(clazz != null){
                Generator gen = (Generator)ApplicationContextUtils.newAutoWiredInstance(clazz);

                GeneratorWrapper wrapper = new GeneratorWrapper();
                wrapper.generator = gen;
                wrapper.type = type;
                wrapper.name = name;
                wrapper.wait =Long.parseLong(properties.getProperty(DistributerParam.Wait.getKeyName(),"0"));
                wrapper.rule=properties.getProperty(DistributerParam.Rule.getKeyName());

                Arrays.stream(DistributerParam.values()).forEach(v->properties.remove(v.getKeyName()));
                log.info("init a generator {} with parameter {}", wrapper.name,properties);
                gen.init(properties);

                allGen.put(wrapper.name,wrapper);
                log.info("Success to create a generator {}",wrapper.name);
            }else{
                log.warn("to wait {}",type);
            }
        });
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

    private static class GeneratorWrapper{
        private String rule;
        private String name;
        private String type;
        private long wait;
        private Generator generator;

    }
}