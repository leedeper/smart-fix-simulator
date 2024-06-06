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

package smart.fixsimulator.dataobject;

import lombok.Data;

import io.mybatis.provider.Entity.*;

/**
 * Desc:
 *
 * @author Leedeper
 */
@Data
@Table("messages")
public class MessageDO {

    @Column("beginstring")
    private String beginString;
    @Column("sendercompid")
    private String senderCompID;

    @Column("sendersubid")
    private String senderSubID;

    @Column("senderlocid")
    private String senderLocID;

    @Column("targetcompid")
    private String targetCompID;

    @Column("targetsubid")
    private String targetSubID;

    @Column("targetlocid")
    private String targetLocID;

    @Column("session_qualifier")
    private String sessionQualifier;

    @Column("msgseqnum")
    private Long msgSeqNum;

    @Column("message")
    private String message;
}
