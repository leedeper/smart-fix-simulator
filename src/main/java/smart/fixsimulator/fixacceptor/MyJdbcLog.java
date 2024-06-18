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
import org.springframework.beans.factory.annotation.Autowired;
import quickfix.*;

import smart.fixsimulator.common.Info;
import smart.fixsimulator.dao.EventLogMapper;
import smart.fixsimulator.dao.MessageLogMapper;
import smart.fixsimulator.dataobject.EventLogDO;
import smart.fixsimulator.dataobject.MessageLogDO;

import static quickfix.JdbcSetting.SETTING_JDBC_LOG_HEARTBEATS;

/**
 * my own jdbc log
 *
 * @author Leedeper
 */
@Slf4j
public class MyJdbcLog implements Log {
    private SessionID sessionID;
    private boolean logHeartbeats;

    private MessageLogMapper messageLogMapper;
    private EventLogMapper eventLogMapper;

    @Autowired
    public void setMessageLogMapper(MessageLogMapper messageLogMapper){
        this.messageLogMapper = messageLogMapper;
    }
    @Autowired
    public void setEventLogMapper(EventLogMapper eventLogMapper){
        this.eventLogMapper = eventLogMapper;
    }
    public MyJdbcLog(SessionSettings serverSessionSettings, SessionID sessionID){
        this.sessionID = sessionID;
        setLogHeartbeats(serverSessionSettings);
    }

    private void setLogHeartbeats(SessionSettings serverSessionSettings){
        try {
            logHeartbeats = !serverSessionSettings.isSetting(sessionID, SETTING_JDBC_LOG_HEARTBEATS)
                    || serverSessionSettings.getBool(sessionID, SETTING_JDBC_LOG_HEARTBEATS);
        } catch (Exception e) {
            log.warn("set the default value 'N' to logHeartbeats",e);
            logHeartbeats = false;
        }
    }


    @Override
    public void onIncoming(String message) {
        if (!logHeartbeats && MessageUtils.isHeartbeat(message)) {
            return;
        }

        MessageLogDO msgDO = MyJdbcMessageLogConvertor.createIncomingMessage(message, sessionID);
        messageLogMapper.insert(msgDO);
    }

    @Override
    public void onOutgoing(String message) {
        if (!logHeartbeats && MessageUtils.isHeartbeat(message)) {
            return;
        }
        MessageLogDO msgDO = MyJdbcMessageLogConvertor.createOutgoingMessage(message, sessionID);
        messageLogMapper.insert(msgDO);
    }

    @Override
    public void onEvent(String txt) {
        insertEventLog(txt, Info.EventLogType.Info);
    }

    @Override
    public void onErrorEvent(String txt) {
        insertEventLog(txt, Info.EventLogType.Error);
    }

    private void insertEventLog(String txt, Info.EventLogType type){
        EventLogDO eventDo = MyJdbcEventLogConvertor.createEvent(txt,sessionID, type);
        eventLogMapper.insert(eventDo);
    }

    @Override
    public void clear() {

    }



}
