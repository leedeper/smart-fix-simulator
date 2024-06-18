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
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import smart.fixsimulator.common.Info;
import smart.fixsimulator.dataobject.MessageLogDO;
import smart.fixsimulator.dto.MessageLogDTO;
import smart.fixsimulator.service.MessageLogService;
import smart.fixsimulator.web.request.SearchRequest;
import smart.fixsimulator.web.response.PageResponseResult;
import smart.fixsimulator.web.response.ResponseResult;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * define the message log controller
 *
 * @author Leedeper
 */
@Slf4j
@RequestMapping("/sfs")
@Controller
public class MessageLogController {

    @Autowired
    private MessageLogService<List<MessageLogDTO>> messageLogService;

    @RequestMapping({"/","/allmsglog"})
    public String allMessage(){
        log.debug("access all message_log page");
        return "sfs/allmsglog";
    }


    @RequestMapping("/msg")
    @ResponseBody
    public PageResponseResult<?> getMessageLog(@RequestParam("pageNum") Integer pageNum
                                                ,@RequestParam("pageSize") Integer pageSize
                                                ,SearchRequest searchRequest){
        log.debug("receive search request : pageNum ={},pageSize={}, parameter = {}",pageNum, pageSize, searchRequest);

        MessageLogDO condition = new MessageLogDO();
        condition.setSide(searchRequest.getSide());
        condition.setMsgType(searchRequest.getMsgType());
        condition.setSenderCompID(searchRequest.getSender());
        condition.setTargetCompID(searchRequest.getTarget());
        condition.setBeginString(searchRequest.getVersion());
        Date startDate = null, endDate=null;
        if(!StringUtils.isEmpty(searchRequest.getStartDate())){
            startDate = toDate(searchRequest.getStartDate());
        }
        if(!StringUtils.isEmpty(searchRequest.getEndDate())){
            endDate = toDate(searchRequest.getEndDate());
        }
        return messageLogService.getMessageLog(pageNum, pageSize, condition, startDate, endDate);
    }
    private Date toDate(String date){
        try {
            return new SimpleDateFormat("yyyyMMdd HH:mm:ss").parse(date + " 00:00:00");
        } catch (ParseException e) {
            log.error("date format is invalid");
            return null;
        }
    }

    @RequestMapping("detail")
    public  String getMessageLogDetail(@RequestParam("msgId") String msgId, Model model){
        ResponseResult<?> res = messageLogService.getMessageLogDetail(msgId);
        model.addAttribute("xml", res.getContent());

        return "sfs/msglogdetail";
    }

    @RequestMapping("/delmsg")
    @ResponseBody
    public ResponseResult<?> delMsg(@RequestParam("id") String id){
        boolean succ=messageLogService.del(id);
        if(!succ){
            return Info.ERROR_RESULT;
        }
        return Info.SUCC_RESULT;
    }

    @RequestMapping("/delallmsg")
    @ResponseBody
    public ResponseResult<?> delAllMsg(){
        boolean succ=messageLogService.del(null);
        if(!succ){
            return Info.ERROR_RESULT;
        }
        return Info.SUCC_RESULT;
    }


}
