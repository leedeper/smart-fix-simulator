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

package smart.fixsimulator.fixacceptor;

import lombok.extern.slf4j.Slf4j;
import quickfix.*;
import quickfix.field.MsgSeqNum;
import smart.fixsimulator.common.Info;
import smart.fixsimulator.dataobject.MessageLogDO;

import java.sql.Timestamp;

/**
 * my own jdbc log
 *
 * @author Leedeper
 */
@Slf4j
public class MyJdbcMessageLogConvertor {
    protected static MessageLogDO createIncomingMessage(String message, SessionID sessionID){
        MessageLogDO msg = new MessageLogDO();
        commonValue(msg, message, sessionID);
        msg.setSide(Info.Side.In.name());

        // reverse
        msg.setSenderCompID(sessionID.getTargetCompID());
        msg.setSenderSubID(sessionID.getTargetSubID());
        msg.setSenderLocID(sessionID.getTargetLocationID());

        msg.setTargetCompID(sessionID.getSenderCompID());
        msg.setTargetSubID(sessionID.getSenderSubID());
        msg.setTargetLocID(sessionID.getSenderLocationID());

        return msg;
    }

    protected static  MessageLogDO createOutgoingMessage(String message, SessionID sessionID){
        MessageLogDO msg = new MessageLogDO();
        commonValue(msg, message, sessionID);
        msg.setSide(Info.Side.Out.name());

        // same as session
        msg.setSenderCompID(sessionID.getSenderCompID());
        msg.setSenderSubID(sessionID.getSenderSubID());
        msg.setSenderLocID(sessionID.getSenderLocationID());

        msg.setTargetCompID(sessionID.getTargetCompID());
        msg.setTargetSubID(sessionID.getTargetSubID());
        msg.setTargetLocID(sessionID.getTargetLocationID());

        return msg;
    }

    private static void commonValue(MessageLogDO msg, String msgstr, SessionID sessionID){
        msg.setTime(new Timestamp(SystemTime.getUtcCalendar().getTimeInMillis()));
        msg.setText(msgstr);
        msg.setBeginString(sessionID.getBeginString());
        msg.setSessionQualifier(sessionID.getSessionQualifier());

        try {
            msg.setMsgType(MessageUtils.getMessageType(msgstr));
            msg.setMsgSeqNum(Long.valueOf(MessageUtils.getStringField(msgstr, MsgSeqNum.FIELD)));
        } catch (Throwable e) {
            log.warn("Some tag is not exists in msg when getting msgType or msgSeqNum",e);
        }
    }

}
