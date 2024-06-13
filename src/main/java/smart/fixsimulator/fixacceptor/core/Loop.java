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
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import quickfix.Message;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.SessionNotFound;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.regex.Pattern;

/**
 * Desc:
 *
 * @author Leedeper
 */
@Slf4j
public class Loop {
    private ScheduledThreadPoolExecutor service;
    // they can be cancelled by fix message
    private LinkedHashMap<String, Future> cancellableTask = new LinkedHashMap<>();
    // they can't be cancelled by fix message, but maybe by GUI or other tools
    private LinkedHashMap<Runnable,Future> unnamedTask = new LinkedHashMap<>();
    private ExpressionParser parser = new SpelExpressionParser();

    public Loop(int poolSize){
        service = new ScheduledThreadPoolExecutor(poolSize);
        service.setRemoveOnCancelPolicy(true);
    }

    public void addTask(Message message,SessionID sessionId, Distributer.GeneratorWrapper gw){
        if(gw.isNoIdExpression()){
            SmartTask task = new SmartTask(message, sessionId, gw);
            Future future = summitTask(task,gw);
            unnamedTask.put(task,future);
        }else {
            String refId = createRefId(message, sessionId, gw.getLoopIdExpression());
            SmartTask task = new SmartTask(message, sessionId, refId, gw);
            Future future = summitTask(task,gw);
            cancellableTask.put(refId, future);
        }
    }

    private Future summitTask(Runnable task,Distributer.GeneratorWrapper gw){
        Future future = service.scheduleWithFixedDelay(task, gw.getLoopInitialDelay()
                , gw.getLoopDelay(), gw.getLoopTimeUnit());
        return future;
    }

    public void cancelById(String id){
        Pattern p=Pattern.compile(id);
        Iterator<Map.Entry<String, Future>> it = cancellableTask.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry<String, Future> en = it.next();
            String refId = en.getKey();
            if(p.matcher(refId).matches()){
                log.info("Cancel loop task : {}",refId);
                en.getValue().cancel(true);
                cancellableTask.remove(en.getKey());
            }
        }
    }

    public void cancelUnnamedTask(Runnable task){
        log.info("Cancel one loop task without id ");
        Future f=unnamedTask.remove(task);
        f.cancel(true);

    }

    private String createRefId(Message msg, SessionID sessionId, String expressionStr){
        EvaluationContext context = new StandardEvaluationContext();
        context.setVariable("message", msg);
        context.setVariable("sessionID", sessionId);
        String refId = parser.parseExpression(expressionStr).getValue(context, String.class);
        return refId;
    }

    private  class SmartTask implements Runnable {
        private long count = 0;
        private Distributer.GeneratorWrapper generatorWrapper;
        private String id;
        private Message message;
        private SessionID sessionId;

        private SmartTask(Message message, SessionID sessionId, Distributer.GeneratorWrapper generatorWrapper) {
            this(message,sessionId,null,generatorWrapper);
        }
        private SmartTask(Message message, SessionID sessionId, String id, Distributer.GeneratorWrapper generatorWrapper){
            this.message=message;
            this.sessionId=sessionId;
            this.id=id;
            this.generatorWrapper=generatorWrapper;
        }
        @Override
        public void run() {
            if(!Session.lookupSession(sessionId).isLoggedOn()){
                cancel();
                return;
            }
            Message reply = generatorWrapper.getGenerator().create(message,sessionId);
            try {
                Session.sendToTarget(reply, sessionId);
            } catch (SessionNotFound e) {
                log.error("can't find session - {},", sessionId, e);
                return;
            }
            if(generatorWrapper.getLoopCount()>0) {
                count++;
                if (count == generatorWrapper.getLoopCount()) {
                    cancel();
                }
            }
        }

        private void cancel(){
            if(id!=null){
                cancelById(id);
            }else{
                cancelUnnamedTask(this);
            }
        }

    }
}
