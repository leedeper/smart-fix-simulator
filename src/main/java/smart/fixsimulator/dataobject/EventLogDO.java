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

import io.mybatis.provider.Entity.Column;
import io.mybatis.provider.Entity.Table;
import lombok.Data;

import java.sql.Timestamp;

/**
 * Desc:
 *
 * @author Leedeper
 */
@Data
@Table("event_log")
public class EventLogDO {
    @Column(value="id",id=true)
    private Long id;

    @Column("time")
    private Timestamp time;

    @Column("sessionid")
    private String sessionID;

    @Column
    private String type;

    @Column("text")
    private String text;
}
