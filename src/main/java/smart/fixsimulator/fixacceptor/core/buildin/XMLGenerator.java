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

package smart.fixsimulator.fixacceptor.core.buildin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import quickfix.Message;
import quickfix.SessionID;
import smart.fixsimulator.common.XmlMessage;
import smart.fixsimulator.fixacceptor.core.Generator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import smart.fixsimulator.common.FixMessageUtil;

/**
 * the default generator which load xml template to generate message
 *
 * @author Leedeper
 */
@Slf4j
public class XMLGenerator implements Generator {

    /*
    * it can be optimized.
    * firstly load xml string as message, then copy message every time when request is coming.
    * finally replace the expression.
    * also, it can be Thread-Safe.
    * In the future, it will be managed by GUI.
    */
    private String xmlStr;
    private String templatePath;
    private FileTime lastModifiedTime;

    private long refreshInterval = 5000;

    @Override
    public Message create(Message message, SessionID sessionId) {
        Message replay = FixMessageUtil.parseXML(xmlStr, sessionId, new Analyzer(message,sessionId));
        return replay;
    }

    @Override
    public void init(Properties properties) {
        templatePath = properties.getProperty("templatePath");
        loadXML();

        // don't restart after modify the template
        new Timer(true).schedule(new TimerTask() {
            @Override
            public void run() {
                loadXML();
            }
        }, refreshInterval, refreshInterval);
    }

    private void loadXML(){
        Path path = Paths.get(templatePath);
        try {
            FileTime lastTime = Files.getLastModifiedTime(path);
            if(lastModifiedTime==null){
                lastModifiedTime = lastTime;
            }else{
                if(lastTime.compareTo(lastModifiedTime)==0){
                    return;
                }
                lastModifiedTime = lastTime;
            }
            xmlStr =new String(Files.readAllBytes(path));
            log.info("Load a xml template {}",xmlStr);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void destroy() {
        log.info("destroy xmlGenerator");
    }

    private class Analyzer implements XmlMessage.Analyzer{
        private Message reqMsg;
        private SessionID reqSessionId;
        public Analyzer(Message reqMsg, SessionID reqSessionId){
            this.reqMsg = reqMsg;
            this.reqSessionId = reqSessionId;
        }
        @Override
        public String analyzer(String originalText) {
            try {
                //eg ${sp:#message.getString(11)}
                if (originalText.startsWith("sp:")) {
                    String expressionStr = originalText.substring(originalText.indexOf(":") + 1);
                    return porcessBySpEL(expressionStr);
                } else {
                    // eg ${RandomInt(5,20)}
                    return processByInnerCommand(originalText);
                }
            }catch (Throwable e){
                log.error("Can't analyzer expression {}, so use blank string as it.",originalText,e);
                return "";
            }
        }

        private String porcessBySpEL(String expressionStr){
            EvaluationContext context = new StandardEvaluationContext();
            context.setVariable("message", reqMsg);
            context.setVariable("sessionID", reqSessionId);
            return new SpelExpressionParser().parseExpression(expressionStr).getValue(context, String.class);
        }
        private String processByInnerCommand(String originalText){
            String nameAndParameter[] = originalText.split("\\(");
            if(nameAndParameter.length != 2){
                throw new RuntimeException("Invalid command. no ( or more - "+originalText);
            }
            String name = nameAndParameter[0];
            String theTail = nameAndParameter[1];
            if(theTail.charAt(theTail.length()-1)!=')'){
                throw new RuntimeException("Invalid command. no ) as end - "+originalText);
            }
            theTail = theTail.substring(0,theTail.length()-1);

            String allParameter[] = theTail.split(",");
            String ps[]= new String[allParameter.length];
            for(int i=0;i<allParameter.length;i++){
                ps[i]=allParameter[i].trim();
            }
            return InnerCommand.getValue(name,allParameter);
        }
    }

}
