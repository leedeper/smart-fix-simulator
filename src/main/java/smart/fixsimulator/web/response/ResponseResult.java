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

package smart.fixsimulator.web.response;

import lombok.Data;
import smart.fixsimulator.common.Info;

import java.io.Serializable;

/**
 * web response
 *
 * @author Leedeper
 */
@Data
public class ResponseResult<T> implements Serializable {
    private String code;
    private String message;
    private T content;

    public ResponseResult() {
        this(Info.SUCCESS);
    }

    public ResponseResult(Info.ResponseStatus statusMessage){
        this.code = statusMessage.getCode();
        this.message = statusMessage.getMessage();
    }
}
