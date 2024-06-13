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

package smart.fixsimulator.common;

import lombok.Getter;
import smart.fixsimulator.web.response.ResponseResult;

/**
 * some info for common used
 *
 * @author Leedeper
 */
public final class Info {

    public static final char SPLIT_CHAT='\001';

   public enum Side {
       In,Out;
    }

    /**
     * if success
     */
    public static final ResponseStatus SUCCESS=new ResponseStatus("1","success");

    /**
     * If unknown
     */
    public static final ResponseStatus UNKNOWN=new ResponseStatus("0","unknown");

    /**
     * If there is no need to clearly define the error code, use -1.
     * If detailed definition is required, use other numbers
     */
    public static final ResponseStatus ERROR=new ResponseStatus("-1","common error");

    public static final ResponseResult ERROR_RESULT=new ResponseResult<>(ERROR);
    public static final ResponseResult SUCC_RESULT=new ResponseResult<>();


    public static class ResponseStatus {
        @Getter
        private final String code;

        @Getter
        private final String message;
        public ResponseStatus(String code, String message){
            this.code=code;
            this.message=message;
        }
    }
}
