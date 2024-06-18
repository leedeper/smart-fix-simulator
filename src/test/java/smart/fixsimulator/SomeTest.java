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
import org.springframework.util.Assert;
import quickfix.Message;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Desc:
 *
 * @author Leedeper
 */
public class SomeTest {

    @Test
    void testSum(){
        String rawMsg = "abc\u000110=88\u0001edf";

        String splitedMsg[]=rawMsg.split("\u000110=");
        System.out.println(splitedMsg[0]+"  "+splitedMsg[1]);
        int inx = splitedMsg[1].indexOf('\u0001');
        String tail = splitedMsg[1].substring(inx);

        String newMsg = splitedMsg[0]+"\u000110="+"99"+tail;

        assertEquals("abc\u000110=99\u0001edf",newMsg);

        // int end = rawMsg.lastIndexOf("\u000110=") ;
       // String pre=
    }
    @Test
    void testSplit(){
        String originalText="RandomInt(10000,50000)";
        String nameAndParameter[] = originalText.split("\\(");
        System.out.println(nameAndParameter.length);
        assertEquals(2,nameAndParameter.length);
    }
    @Test
    void testPath(){
        String f="/Users/luck/githubproj/smart-fix-simulator/pom.xml";
        Path p = Paths.get(f);
        System.out.println(p.getFileName());
    }
    @Test
    void testDouble(){
        Double d=ThreadLocalRandom.current().nextDouble(200000000,400000000);
        DecimalFormat df = new DecimalFormat("#.00");
        String str = df.format(d);
        System.out.println(str);
        assertEquals(2,str.substring(str.indexOf('.')).length()-1);
    }
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
