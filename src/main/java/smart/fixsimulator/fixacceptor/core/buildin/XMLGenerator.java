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
import quickfix.FileUtil;
import quickfix.Message;
import quickfix.SessionID;
import smart.fixsimulator.common.XmlMessage;
import smart.fixsimulator.fixacceptor.core.Generator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    * firstly load xml string as message, then copy message every time when request is coming.
    * finally replace the expression.
    */
    private String xmlStr;
    @Override
    public Message create(Message message, SessionID sessionId) {
        Message replay = FixMessageUtil.parseXML(xmlStr, sessionId, new SPELAnalyzer(message,sessionId));
        return replay;
    }

    @Override
    public void init(Properties properties) {
        Path path = Paths.get(properties.getProperty("templatePath"));
        try {
            xmlStr = Files.readString(path);
            log.info("Load a xml template {}",xmlStr);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void destroy() {

    }

    private class SPELAnalyzer implements XmlMessage.Analyzer{
        private Message reqMsg;
        private SessionID reqSessionId;
        public SPELAnalyzer(Message reqMsg, SessionID reqSessionId){
            this.reqMsg = reqMsg;
            this.reqSessionId = reqSessionId;
        }
        @Override
        public String analyzer(String originalText) {
            //TODO spring el
            return "HHH";
        }
    }


}
