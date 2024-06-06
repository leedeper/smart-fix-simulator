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

import quickfix.Log;
import quickfix.LogFactory;
import quickfix.SessionID;
import quickfix.SessionSettings;

/**
 * The quickfixJ JdbcLog save sender and target from session,
 * so it's not easy to find outgoing or incoming message.
 * also  the msg type is not saved to db.
 *
 * @author Leedeper
 */
public class MyJdbcLogFactory implements LogFactory {
    private SessionSettings serverSessionSettings;
    MyJdbcLogFactory(SessionSettings serverSessionSettings){
        this.serverSessionSettings = serverSessionSettings;
    }
    @Override
    public Log create(SessionID sessionID) {
        return new MyJdbcLog(serverSessionSettings, sessionID);
    }
}
