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
import quickfix.field.*;

/**
 * handle the biz message
 *
 * @author Leedeper
 */
@Slf4j
public class FixApplicationAdapter implements Application {

	private final DefaultMessageFactory messageFactory = new DefaultMessageFactory();

	@Override
	public void fromAdmin(Message message, SessionID sessionId) {
		log.info("fromAdmin: Message={}, SessionId={}", message, sessionId);
	}

	@Override
	public void fromApp(Message message, SessionID sessionId) {
		log.info("fromApp: Message={}, SessionId={}", message, sessionId);

        try {
			if(message.getHeader().getField(new MsgType()).valueEquals(MsgType.ORDER_SINGLE)){
				sendExecutionReport(message);
			}/*else{
				sendBusinessReject(message, BusinessRejectReason.APPLICATION_NOT_AVAILABLE,
						"Application not available");
			}*/

        } catch (FieldNotFound e) {
			log.error("",e);
            throw new RuntimeException(e);
        } catch (SessionNotFound e) {
			log.error("",e);
            throw new RuntimeException(e);
        }
    }

	@Override
	public void onCreate(SessionID sessionId) {
		log.info("onCreate: SessionId={}", sessionId);
	}

	@Override
	public void onLogon(SessionID sessionId) {
		log.info("onLogon: SessionId={}", sessionId);
	}

	@Override
	public void onLogout(SessionID sessionId) {
		log.info("onLogout: SessionId={}", sessionId);
	}

	@Override
	public void toAdmin(Message message, SessionID sessionId) {
		log.info("toAdmin: Message={}, SessionId={}", message, sessionId);
	}

	@Override
	public void toApp(Message message, SessionID sessionId) {
		log.info("toApp: Message={}, SessionId={}", message, sessionId);
	}


	private Message createMessage(Message message, String msgType) throws FieldNotFound {
		Message msg= messageFactory.create(message.getHeader().getString(BeginString.FIELD), msgType);
		msg.reverseRoute(message.getHeader());
		return msg;
	}


	private void sendSessionReject(Message message, int rejectReason) throws FieldNotFound,
			SessionNotFound {
		Message reply = createMessage(message, MsgType.REJECT);
		String refSeqNum = message.getHeader().getString(MsgSeqNum.FIELD);
		reply.setString(RefSeqNum.FIELD, refSeqNum);
		reply.setString(RefMsgType.FIELD, message.getHeader().getString(MsgType.FIELD));
		reply.setInt(SessionRejectReason.FIELD, rejectReason);
		Session.sendToTarget(reply);
	}

	private void sendBusinessReject(Message message, int rejectReason, String rejectText)
			throws FieldNotFound, SessionNotFound {
		Message reply = createMessage(message, MsgType.BUSINESS_MESSAGE_REJECT);
		String refSeqNum = message.getHeader().getString(MsgSeqNum.FIELD);
		reply.setString(RefSeqNum.FIELD, refSeqNum);
		reply.setString(RefMsgType.FIELD, message.getHeader().getString(MsgType.FIELD));
		reply.setInt(BusinessRejectReason.FIELD, rejectReason);
		reply.setString(Text.FIELD, rejectText);
		Session.sendToTarget(reply);
		log.info("svr send BusinessReject msg : Message={}", reply);
	}

	private void sendExecutionReport(Message message)
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

		Session.sendToTarget(reply);
		log.info("svr send ExecutionReport msg : Message={}", reply);
	}
}
