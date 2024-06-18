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
import quickfix.SessionID;
import quickfix.SystemTime;
import smart.fixsimulator.common.Info;
import smart.fixsimulator.dataobject.EventLogDO;

import java.sql.Timestamp;

/**
 * my own jdbc event log
 *
 * @author Leedeper
 */
@Slf4j
public class MyJdbcEventLogConvertor {
    protected static EventLogDO createEvent(String txt, SessionID sessionID, Info.EventLogType type){
        EventLogDO logDo = new EventLogDO();
        logDo.setTime(new Timestamp(SystemTime.getUtcCalendar().getTimeInMillis()));
        logDo.setSessionID(sessionID.toString());
        logDo.setType(type.name());
        if(txt.length()>2048){
            log.info("It's too long, substring 0-2048");
            txt = txt.substring(0,2048);
        }
        logDo.setText(txt);
        return logDo;
    }

}
