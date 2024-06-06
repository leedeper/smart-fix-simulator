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

package smart.fixsimulator.dto;

import lombok.Data;

/**
 * the message for response
 *
 * @author Leedeper
 */
@Data
public class MessageDTO {

    private String beginString;

    private String senderCompID;

    private String senderSubID;

    private String senderLocID;

    private String targetCompID;

    private String targetSubID;

    private String targetLocID;

    private String sessionQualifier;

    private long msgSeqNum;

    private String message;
}
