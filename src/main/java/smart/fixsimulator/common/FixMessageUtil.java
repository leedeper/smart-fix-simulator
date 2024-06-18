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

package smart.fixsimulator.common;

import lombok.extern.slf4j.Slf4j;
import quickfix.*;
import quickfix.field.MsgType;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Desc:
 *
 * @author Leedeper
 */
@Slf4j
public class FixMessageUtil {
    private static Map<String,String> msgValueName = new HashMap<>();
    static{
        List<Field> allFields = Arrays.stream(MsgType.class.getDeclaredFields())
                .filter(e-> Modifier.isStatic(e.getModifiers())).collect(Collectors.toList());

        allFields.forEach(e->{
            try {
                e.setAccessible(true);
                Object value = e.get(null);
                if(value instanceof String){
                    msgValueName.put((String)e.get(null),e.getName());
                }
            } catch (Throwable ex) {
                throw new RuntimeException("can't get the value for static field = "+e.getName(),ex);
            }
        });
    }
    public static String  getMsgTypeName(String value) {
        return msgValueName.get(value);
    }


    public static java.util.Map<SessionID, Session> getAllSession(){
        Class<?> clazz = quickfix.Session.class;
        try {
            java.lang.reflect.Field ss = clazz.getDeclaredField("sessions");
            ss.setAccessible(true);
            java.util.Map<SessionID, Session> map =(java.util.Map)ss.get(null);
            return map;
        } catch (Exception e) {
            log.error("Hack to get fix session error",e);
            throw new RuntimeException(e);
        }
    }

    public static Message parseXML(String xmlStr, SessionID sessionId, XmlMessage.Analyzer analyzer){
        try {
            String rawMsg = new XmlMessage(xmlStr, String.valueOf(Info.SPLIT_CHAT), analyzer).toFixMessage();
            rawMsg = changeSum(rawMsg);
            return toMessage(rawMsg, sessionId);
        }catch (Throwable e){
            throw  new RuntimeException(e);
        }
    }

    private static String changeSum(String rawMsg){
        int sum = MessageUtils.checksum(rawMsg);
        String splitedMsg[]=rawMsg.split("\u000110=");
        int inx = splitedMsg[1].indexOf('\u0001');
        String tail = splitedMsg[1].substring(inx);
        String newMsg = splitedMsg[0]+"\u000110="+sum+tail;
        return newMsg;
    }

    public static Message parseXML(String xmlStr,SessionID sessionId){
        return parseXML(xmlStr, sessionId, null);
    }

    public static String toXML(String beginString, String senderCompID
            , String targetCompID, String qualifier, String rawMsg) {
        SessionID sId=new SessionID(beginString, senderCompID, targetCompID, qualifier);
        return toXML(rawMsg,sId);
    }
    public static String toXML( String rawMsg,SessionID sId) {
        Message msg = toMessage(rawMsg,sId);
        return msg.toXML();
    }

    public static Message toMessage(String rawMsg, SessionID sessionID){
        Session session = Session.lookupSession(sessionID);
        try {
            return MessageUtils.parse(session, rawMsg);
        } catch (InvalidMessage e) {
            throw new RuntimeException(e);
        }
    }
}
