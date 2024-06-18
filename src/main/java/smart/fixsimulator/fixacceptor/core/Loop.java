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
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import quickfix.Message;
import quickfix.Session;
import quickfix.SessionID;

import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;


/**
 * Desc:
 *
 * @author Leedeper
 */
@Slf4j
public class Loop {
    private ScheduledThreadPoolExecutor service;
    // they can be cancelled by fix message
    private LinkedHashMap<String, SmartTask> allTask = new LinkedHashMap<>();
    private ExpressionParser parser = new SpelExpressionParser();

    // default 1,but can be modified
    private static Loop instance=new Loop(1);
    public static Loop get(){
        return instance;
    }
    public static void setPoolSize(int poolSize){
        instance.service.setCorePoolSize(poolSize);
    }

    private Loop(int poolSize){
        service = new ScheduledThreadPoolExecutor(poolSize);
        service.setRemoveOnCancelPolicy(true);
    }

    public void addTask(Message message,SessionID sessionId, Distributer.GeneratorWrapper gw){
        if(gw.isNoIdExpression()){
            SmartTask task = new SmartTask(message, sessionId, gw);
            SmartTask tsk = summitTask(task,gw);
            allTask.put(task.toString() , tsk);
        }else {
            String refId = createRefId(message, sessionId, gw.getLoopIdExpression());
            if(allTask.containsKey(refId)){
                throw new RuntimeException("id existed in loop task - "+refId);
            }
            SmartTask task = new SmartTask(message, sessionId, refId, gw);
            SmartTask tsk = summitTask(task,gw);
            allTask.put(refId, tsk);
        }
    }

    private SmartTask summitTask(SmartTask task,Distributer.GeneratorWrapper gw){
        Future future = service.scheduleWithFixedDelay(task, gw.getLoopInitialDelay()
                , gw.getLoopDelay(), gw.getLoopTimeUnit());
        task.future = future;
        return task;
    }

    public void cancelById(String id){
        SmartTask task = allTask.get(id);
        if(task!=null){
            cancel(id, task);
        }
    }

    public void cancelUnnamedTask(String beanStr){
        log.info("Cancel one loop task without id ");
        SmartTask task = allTask.get(beanStr);
        if(task!=null){
            cancel(beanStr, task);
        }
    }
    private void cancel(String id, SmartTask task){
        log.info("Cancel loop task : {}",id);
        task.future.cancel(true);
        allTask.remove(id);
    }

    public List<TaskInfo> getTaskInfo(){
        ArrayList<TaskInfo> list =new ArrayList(allTask.size());
        allTask.forEach((k,v)->{
            TaskInfo info=new TaskInfo();
            info.total = v.generatorWrapper.getLoopCount();
            info.count = v.count;
            info.createTime = v.createTime;
            info.delay = v.generatorWrapper.getLoopDelay();
            info.hasRefId = v.id!=null;
            info.id=k;
            info.name = v.generatorWrapper.getName();
            info.timeUnit = v.generatorWrapper.getLoopTimeUnit().name();
            info.type = v.generatorWrapper.getType();
            list.add(info);
        });
        return list;
    }

    private String createRefId(Message msg, SessionID sessionId, String expressionStr){
        EvaluationContext context = new StandardEvaluationContext();
        context.setVariable("message", msg);
        context.setVariable("sessionID", sessionId);
        String refId = parser.parseExpression(expressionStr).getValue(context, String.class);
        return refId;
    }

    @Data
    public static class TaskInfo{
        private String id;
        private Date createTime;
        private String name;
        private String type;
        private long delay;
        private long total;
        private long count;
        private String timeUnit;
        private boolean hasRefId;
    }

    private  class SmartTask implements Runnable {
        private long count = 0;
        private Distributer.GeneratorWrapper generatorWrapper;
        private String id;
        private Message message;
        private SessionID sessionId;
        private Future future;
        private Date createTime;

        private SmartTask(Message message, SessionID sessionId, Distributer.GeneratorWrapper generatorWrapper) {
            this(message,sessionId,null,generatorWrapper);
        }
        private SmartTask(Message message, SessionID sessionId, String id, Distributer.GeneratorWrapper generatorWrapper){
            this.message=message;
            this.sessionId=sessionId;
            this.id=id;
            this.generatorWrapper=generatorWrapper;
            this.createTime = new Date();
        }

        @Override
        public void run() {
            if(!Session.lookupSession(sessionId).isLoggedOn()){
                cancel();
                return;
            }
            try {
                Message reply = generatorWrapper.getGenerator().create(message,sessionId);
                if(reply!=null) {
                    log.info("Generator '{}' create a reply in task '{}' - {}"
                            , generatorWrapper.getName(), (id == null ? this.toString() : id), reply);
                    Session.sendToTarget(reply, sessionId);
                }
            } catch (Throwable e) {
                log.error("create and send message error in task - generator={} ", generatorWrapper.getName(), e);
                return;
            }
            count++;
            if(generatorWrapper.getLoopCount()>0) {
                if (count == generatorWrapper.getLoopCount()) {
                    cancel();
                }
            }

        }

        private void cancel(){
            if(id!=null){
                cancelById(id);
            }else{
                cancelUnnamedTask(this.toString());
            }
        }

    }
}
