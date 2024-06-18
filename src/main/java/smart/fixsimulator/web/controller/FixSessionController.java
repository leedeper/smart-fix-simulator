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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import quickfix.Session;
import quickfix.SessionID;
import smart.fixsimulator.common.FixMessageUtil;
import smart.fixsimulator.common.Info;
import smart.fixsimulator.dto.EventLogDTO;
import smart.fixsimulator.dto.SessionDTO;
import smart.fixsimulator.service.EventLogService;
import smart.fixsimulator.web.response.PageResponseResult;
import smart.fixsimulator.web.response.ResponseResult;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * define the fix session controller
 *
 * @author Leedeper
 */
@Slf4j
@RequestMapping("/sfs")
@Controller
public class FixSessionController {

    @RequestMapping({"/","/allsession"})
    public String allEvent(){
        log.debug("access all session page");
        return "sfs/allsession";
    }

    @RequestMapping("/session")
    @ResponseBody
    public ResponseResult<?> getSessions(){
        Map<SessionID, Session> all = FixMessageUtil.getAllSession();
        ArrayList sessionList=new ArrayList();
        all.forEach((k,v)->{
            SessionDTO dto=new SessionDTO();
            dto.setName(k.toString());
            if(v.isLoggedOn()){
                dto.setLogon(true);
                //dto.setLogonTime(new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(v.getStartTime()));
            }else{
                dto.setLogon(false);
            }
            sessionList.add(dto);
        });
        ResponseResult r=new ResponseResult();
        r.setContent(sessionList);
        return r;
    }

    @RequestMapping("/fixlogon")
    @ResponseBody
    public ResponseResult<?> doLogon(String sessionID){
        FixMessageUtil.getAllSession().forEach((k,v)->{
            if(k.toString().equals(sessionID)){
                if(!v.isLoggedOn()){
                    log.info("To logon {}", sessionID);
                    v.logon();
                }
            }
        });
        return Info.SUCC_RESULT;
    }

    @RequestMapping("/fixlogout")
    @ResponseBody
    public ResponseResult<?> doLogout(String sessionID){
        FixMessageUtil.getAllSession().forEach((k,v)->{
            if(k.toString().equals(sessionID)){
                if(v.isLoggedOn()){
                    log.info("To logout {}", sessionID);
                    v.logout();
                }
            }
        });
        return Info.SUCC_RESULT;
    }
}
