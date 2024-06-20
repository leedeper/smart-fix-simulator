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

import java.util.Date;

/**
 * Desc:
 *
 * @author Leedeper
 */
@Data
public class LoopTaskDTO {
    private String id;
    private String time;
    private String name;
    private String type;
    private long delay;
    private long count;
    private long total;
    private String timeUnit;
    private boolean hasRefId;
}