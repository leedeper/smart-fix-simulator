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
import smart.fixsimulator.common.ScannedFileLoader;
import smart.fixsimulator.common.XmlMessage;
import smart.fixsimulator.fixacceptor.core.Generator;

import java.util.Properties;


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
    * 1. load xml string as message, then copy message every time when request is coming.
    * 2. replace the expression.
    * 3. it could be Thread-Safe.
    * 4. it will be managed by GUI.
    */
    private ScannedFileLoader scannedFileLoader;

    @Override
    public Message create(Message message, SessionID sessionId) {
        if(scannedFileLoader==null || scannedFileLoader.get()==null){
            throw new RuntimeException("Pls call after init, or some error has occured");
        }
        return FixMessageUtil.parseXML(scannedFileLoader.get(), sessionId, new Analyzer(message,sessionId));
    }

    @Override
    public void init(Properties properties) {
        String templatePath = properties.getProperty("templatePath");
        scannedFileLoader = new ScannedFileLoader(templatePath);
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
                    return InnerCommand.getValue(originalText);
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

    }

}
