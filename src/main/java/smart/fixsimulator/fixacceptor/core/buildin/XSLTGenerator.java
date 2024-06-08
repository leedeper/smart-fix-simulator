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
import quickfix.field.*;
import smart.fixsimulator.fixacceptor.core.Generator;

import java.util.List;
import java.util.Properties;

/**
 * Desc:
 *
 * @author Leedeper
 */
@Slf4j
public class XSLTGenerator implements Generator {
    @Override
    public Message create(Message message, SessionID sessionId, List<Message> previousMessages) {
        try {
            return mockExecutionReport(message);
        } catch (FieldNotFound e) {
            throw new RuntimeException(e);
        } catch (SessionNotFound e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void init(Properties properties) {
        log.info("xsltgen init : {}", properties);
    }

    @Override
    public void destroy() {

    }

    private Message createMessage(Message message, String msgType) throws FieldNotFound {
        Message msg= new DefaultMessageFactory().create(message.getHeader().getString(BeginString.FIELD), msgType);
        msg.reverseRoute(message.getHeader());
        return msg;
    }
    private Message mockExecutionReport(Message message)
            throws FieldNotFound, SessionNotFound {
        Message reply = createMessage(message, MsgType.EXECUTION_REPORT);
        String refSeqNum = message.getHeader().getString(MsgSeqNum.FIELD);
        reply.setInt(AvgPx.FIELD, 22);
        reply.setString(ClOrdID.FIELD, "id1111352157882577");
        reply.setDouble(LastPx.FIELD,0D);
        reply.setDouble(LastQty.FIELD,0D);
        reply.setDouble(CumQty.FIELD, 2233);
        reply.setString(ExecID.FIELD, "948485id");
        reply.setChar(ExecTransType.FIELD, '0');
        reply.setString(OrderID.FIELD, "orderid0001");
        reply.setDouble(OrderQty.FIELD, message.getDouble(OrderQty.FIELD));
        reply.setChar(OrdStatus.FIELD, '0');
        reply.setChar(Side.FIELD, message.getChar(Side.FIELD));
        reply.setString(Symbol.FIELD, message.getString(Symbol.FIELD));
        reply.setChar(ExecType.FIELD,'2');
        reply.setDouble(LeavesQty.FIELD, 0d);
        log.info("mock ExecutionReport msg : Message={}", reply);
        return reply;
    }
}
