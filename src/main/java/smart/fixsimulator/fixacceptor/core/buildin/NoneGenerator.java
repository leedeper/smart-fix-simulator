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
import smart.fixsimulator.common.FixMessageUtil;
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

/**
 * do nothing,just log it
 *
 * @author Leedeper
 */
@Slf4j
public class NoneGenerator implements Generator {

    @Override
    public Message create(Message message, SessionID sessionId) {
        log.info("Don't create messge ,just log it. session {},message {}", sessionId,message);
        return null;
    }

    @Override
    public void init(Properties properties) {
        log.info("Init a none-gen");
    }


    @Override
    public void destroy() {
        log.info("destroy none-gen");
    }

}
