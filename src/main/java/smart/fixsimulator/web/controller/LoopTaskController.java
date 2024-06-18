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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import quickfix.Session;
import quickfix.SessionID;
import smart.fixsimulator.common.FixMessageUtil;
import smart.fixsimulator.common.Info;
import smart.fixsimulator.dto.EventLogDTO;
import smart.fixsimulator.dto.LoopTaskDTO;
import smart.fixsimulator.dto.SessionDTO;
import smart.fixsimulator.fixacceptor.core.Loop;
import smart.fixsimulator.service.EventLogService;
import smart.fixsimulator.web.response.ResponseResult;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * define the loop task controller
 *
 * @author Leedeper
 */
@Slf4j
@RequestMapping("/sfs")
@Controller
public class LoopTaskController {

    @RequestMapping({"/","/looptask"})
    public String allTask(){
        log.debug("access all session page");
        return "sfs/looptask";
    }

    @RequestMapping("/task")
    @ResponseBody
    public ResponseResult<?> getTasks(){
        List<Loop.TaskInfo> list = Loop.get().getTaskInfo();
        List <LoopTaskDTO> rList =new ArrayList<>(list.size());
        list.forEach(info ->{
            LoopTaskDTO dto =new LoopTaskDTO();
            BeanUtils.copyProperties(info, dto);
            dto.setTime(new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(info.getCreateTime()));
            rList.add(dto);
        });
        ResponseResult r=new ResponseResult();
        r.setContent(rList);
        return r;
    }

    @RequestMapping("/cancel")
    @ResponseBody
    public ResponseResult<?> cancel(String id, boolean hasRefId){
        if(hasRefId){
            Loop.get().cancelById(id);
        }else{
            Loop.get().cancelUnnamedTask(id);
        }
        return Info.SUCC_RESULT;
    }
}
