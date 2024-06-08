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
import quickfix.SessionID;

/**
 * Desc:
 *
 * @author Leedeper
 */
@Slf4j
public class SPELRuler implements Ruler{
    ExpressionParser parser = new SpelExpressionParser();

    @Override
    public boolean match(Message msg, SessionID sessionId, String expressionStr) {
        EvaluationContext context = new StandardEvaluationContext();
        context.setVariable("message", msg);
        context.setVariable("sessionID", sessionId);
        boolean mathed = parser.parseExpression(expressionStr).getValue(context, Boolean.class);
        return mathed;
    }
}