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
import quickfix.field.MsgType;
import smart.fixsimulator.common.FixMessageUtil;
import smart.fixsimulator.common.Info;
import smart.fixsimulator.dto.MessageLogDTO;
import smart.fixsimulator.service.MessageLogService;
import smart.fixsimulator.web.response.PageResponseResult;
import smart.fixsimulator.web.response.ResponseResult;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * define the home controller
 *
 * @author Leedeper
 */
@Slf4j
@Controller
public class HomeController {

    @Autowired
    private MessageLogService<List<MessageLogDTO>> messageLogService;

    @RequestMapping({"/home","/"})
    public String home(){
        log.debug("access home page");
        return "home";
    }

    @PostMapping("/home/util/sessions")
    @ResponseBody
    public ResponseResult<?> getAllSession(){
        ArrayList<String> data=new ArrayList();
        FixMessageUtil.getAllSession().keySet().forEach(s->{
            data.add(s.toString());
        });

        ResponseResult<List<String>> r= new ResponseResult<>();
        r.setContent(data);
        return r;
    }

    @PostMapping("/home/util/fixMsgTypeName")
    @ResponseBody
    public ResponseResult<?> getMessageLog(@RequestParam("msgType") String type){
        String displayName = FixMessageUtil.getMsgTypeName(type);
        if(displayName ==null ){
            return new ResponseResult<>(Info.ERROR);
        }
        ResponseResult<String> r= new ResponseResult<>();
        r.setContent(displayName);
        return r;
    }


    @PostMapping("/home/util/version")
    @ResponseBody
    public ResponseResult<?> getAllVersion(){
        Set<SessionID> sId = FixMessageUtil.getAllSession().keySet();
        Map<String,String> all = sId.stream().collect(Collectors.toMap(SessionID::getBeginString,SessionID::getBeginString));
        ResponseResult<Map<String,String>> r= new ResponseResult<>();
        r.setContent(all);
        return r;
    }

    @PostMapping("/home/util/sender")
    @ResponseBody
    public ResponseResult<?> getAllSender(){
        Set<SessionID> sId = FixMessageUtil.getAllSession().keySet();
        Map<String,String> all = sId.stream().collect(Collectors.toMap(SessionID::getSenderCompID, SessionID::getSenderCompID, (o,n)->{return o;}));
        Map<String,String> allTarget = sId.stream().collect(Collectors.toMap(SessionID::getTargetCompID,SessionID::getTargetCompID, (o,n)->{return o;}));
        all.putAll(allTarget);
        ResponseResult<Map<String,String>> r= new ResponseResult<>();
        r.setContent(all);
        return r;
    }

    @PostMapping("/home/util/target")
    @ResponseBody
    public ResponseResult<?> getAllTarget(){
        return getAllSender();
    }

    @PostMapping("/home/util/msgType")
    @ResponseBody
    public ResponseResult<?> getAllMsgType(){
        List<String> list = messageLogService.getAllMsgType();
        Map<String,String> all = list.stream().sorted(Comparator.comparing(e->e))
                .collect(Collectors.toMap(e->{return e+" - "+FixMessageUtil.getMsgTypeName(e);},e->e));

        ResponseResult<Map<String,String>> r= new ResponseResult<>();
        r.setContent(new TreeMap<>(all));
        return r;
    }

}
