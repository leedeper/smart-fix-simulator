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

package smart.fixsimulator.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import quickfix.SessionID;
import smart.fixsimulator.common.FixMessageUtil;
import smart.fixsimulator.common.Info;
import smart.fixsimulator.dto.MessageLogDTO;
import smart.fixsimulator.service.MessageLogService;
import smart.fixsimulator.web.response.ResponseResult;

import java.util.*;
import java.util.stream.Collectors;

/**
 * define the fix2xml kit controller
 *
 * @author Leedeper
 */
@Slf4j
@Controller
public class Fix2xmlController {

    @RequestMapping("/sfs/tranfix2xml")
    public String tranKit(){
        log.debug("access tran kit page");
        return "sfs/tranfix2xml";
    }

    @PostMapping("/home/util/fix2xml")
    @ResponseBody
    public ResponseResult<?> tranFixToXML(@RequestParam("msg") String msg
            , @RequestParam("session") String sessionStr){
        log.info("parse message to xml {} - {}",sessionStr,msg);
        if(msg.trim().isEmpty()){
            return Info.ERROR_RESULT;
        }
        ResponseResult<String> r= new ResponseResult<>();
        FixMessageUtil.getAllSession().forEach((k,v)->{
            if(k.toString().equals(sessionStr)){
                String xml = FixMessageUtil.toXML(msg, k);
                r.setContent(xml);
                return;
            }
        });
        if(r.getContent()==null){
            return Info.ERROR_RESULT;
        }
        return r;
    }

}
