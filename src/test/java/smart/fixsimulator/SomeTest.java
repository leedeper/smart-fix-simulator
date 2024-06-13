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

package smart.fixsimulator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import quickfix.Message;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Desc:
 *
 * @author Leedeper
 */
public class SomeTest {
    @Test
    void testSPEL(){
        ExpressionParser parser = new SpelExpressionParser();
        String expressionStr="#message.getHeader().getString(35).equals(\"D\") && #message.getString(20).equals(\"0\")";
        Message msg = new Message();
        msg.getHeader().setString(35,"D");
        msg.setString(20,"0");


        EvaluationContext context = new StandardEvaluationContext();
        context.setVariable("message", msg);

        boolean matched = parser.parseExpression(expressionStr).getValue(context, Boolean.class);
        assertEquals(true, matched,"spel error");
    }
}
