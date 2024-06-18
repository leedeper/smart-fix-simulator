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
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import smart.fixsimulator.common.Info;
import smart.fixsimulator.dataobject.EventLogDO;
import smart.fixsimulator.dataobject.MessageLogDO;
import smart.fixsimulator.dto.EventLogDTO;
import smart.fixsimulator.dto.MessageLogDTO;
import smart.fixsimulator.service.EventLogService;
import smart.fixsimulator.service.MessageLogService;
import smart.fixsimulator.web.request.SearchRequest;
import smart.fixsimulator.web.response.PageResponseResult;
import smart.fixsimulator.web.response.ResponseResult;

import java.util.List;

/**
 * define the event log controller
 *
 * @author Leedeper
 */
@Slf4j
@RequestMapping("/sfs")
@Controller
public class EventLogController {

    @Autowired
    private EventLogService<List<EventLogDTO>> eventLogService;

    @RequestMapping({"/","/alleventlog"})
    public String allEvent(){
        log.debug("access all message_log page");
        return "sfs/alleventlog";
    }

    @RequestMapping("/event")
    @ResponseBody
    public PageResponseResult<?> getEventLog(@RequestParam("pageNum") Integer pageNum
            ,@RequestParam("pageSize") Integer pageSize){
        return eventLogService.getMessageLog(pageNum, pageSize);
    }

    @RequestMapping("/delallevent")
    @ResponseBody
    public ResponseResult<?> delAllEvent(){
        boolean succ=eventLogService.deleteAll();
        if(!succ){
            return Info.ERROR_RESULT;
        }
        return Info.SUCC_RESULT;
    }
}
