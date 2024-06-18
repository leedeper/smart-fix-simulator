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
import quickfix.*;
import smart.fixsimulator.common.FixMessageUtil;
import smart.fixsimulator.fixacceptor.core.Generator;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.util.Properties;

/**
 * Desc:generator message from xml which is translated by xslt
 *
 * @author Leedeper
 */
@Slf4j
public class XSLTGenerator implements Generator {
    private Transformer transformer;
    @Override
    public Message create(Message message, SessionID sessionId) {
            return createMessage(message, sessionId);
    }

    private Message createMessage(Message message, SessionID sessionId) {
        if(transformer==null){
            throw new RuntimeException("No xslt transformer init, do nothing.");
        }
        Source xml = new StreamSource(new StringReader(message.toXML()));
        ByteArrayOutputStream os=new ByteArrayOutputStream();
        Result result = new StreamResult(os);
        try {
            transformer.transform(xml, result);
        } catch (TransformerException e) {
            log.error("Can't transform xml by xslt ");
            throw new RuntimeException(e);
        }
        String xmlStr = os.toString();
        return FixMessageUtil.parseXML(xmlStr, sessionId);
    }
    @Override
    public void init(Properties properties) {
        log.info("xsltgen init : {}", properties);
        // maybe auto reload when modified in the future.
        try {
            String xsltPath = properties.getProperty("xsltPath");
            TransformerFactory factory = TransformerFactory.newInstance();
            Source xslt = new StreamSource(xsltPath);
            transformer = factory.newTransformer(xslt);
        }catch (Throwable e){
            log.error("Load xslt error ",e);
        }
    }

    @Override
    public void destroy() {
        log.info("xsltgen destroy ");
    }

}
