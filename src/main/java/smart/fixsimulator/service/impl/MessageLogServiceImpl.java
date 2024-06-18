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

package smart.fixsimulator.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import quickfix.*;
import quickfix.field.ApplVerID;
import smart.fixsimulator.common.FixMessageUtil;
import smart.fixsimulator.common.Info;
import smart.fixsimulator.dao.MessageLogMapper;
import smart.fixsimulator.dataobject.MessageLogDO;

import smart.fixsimulator.dto.MessageLogDTO;
import smart.fixsimulator.dto.MessageLogDetailDTO;
import smart.fixsimulator.service.MessageLogService;
import smart.fixsimulator.web.response.PageResponseResult;
import smart.fixsimulator.web.response.ResponseResult;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static smart.fixsimulator.common.Info.ERROR_RESULT;

/**
 * implement message log svr
 *
 * @author Leedeper
 */
@Slf4j
@Service
public class MessageLogServiceImpl implements MessageLogService<List<MessageLogDTO>> {

    @Autowired
    private MessageLogMapper messageLogMapper;
    @Override
    public PageResponseResult<List<MessageLogDTO>> getMessageLog(Integer pageNum, Integer pageSize
            ,MessageLogDO condition,Date startDate, Date endDate) {
        PageHelper.startPage(pageNum, pageSize);
        log.debug("Condition {}",condition);
        List<MessageLogDO> all = messageLogMapper.getAllMessageLogDesc(condition,startDate,endDate);
        int countNum = (int)((Page<MessageLogDO>) all).getTotal();
        log.debug("find msg from db total : {}/{}", all.size(),countNum);
        List<MessageLogDTO> resultMsgList = new ArrayList<>();
        for(MessageLogDO msgLogDo : all){
            MessageLogDTO msgLogDTO = new MessageLogDTO();
            BeanUtils.copyProperties(msgLogDo,msgLogDTO);
            msgLogDTO.setTime(new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(msgLogDo.getTime()));
            resultMsgList.add(msgLogDTO);
        }
        log.debug("dto total : {}", resultMsgList.size());

        PageResponseResult<List<MessageLogDTO>> result=new PageResponseResult<>();
        if(!resultMsgList.isEmpty()){
            result.setContent(resultMsgList);
            result.setTotalNum(countNum);
            result.setPageNum(pageNum);
            result.setPageSize(pageSize);
        }

        log.debug("query all message log : {}，{}", result,result.getContent());
        return result;
    }

    @Override
    public ResponseResult<String> getMessageLogDetail(String id) {
        MessageLogDO msgLogDO = messageLogMapper.selectByPrimaryKey(Long.parseLong(id)).orElse(null);
        if(msgLogDO==null){
            log.warn("no found for message id {}",id);
            return ERROR_RESULT;
        }
        String resText = toXML(msgLogDO);
        ResponseResult res = new ResponseResult();
        res.setContent(resText);
        return res;
    }

    @Override
    public List<String> getAllMsgType() {
        return messageLogMapper.getMsgTypeDistinct().stream()
                .map(e->e.getMsgType()).collect(Collectors.toList());
    }

    @Override
    public boolean del(String id) {
        int total=0;
        if(id==null){
            total = messageLogMapper.delete(new MessageLogDO());
        }else{
            total = messageLogMapper.deleteByPrimaryKey(Long.parseLong(id));
        }
        log.info("delete recored : {}",total);
        return true;
    }

    private String toXML(MessageLogDO msgLogDO)  {
        String beginString = msgLogDO.getBeginString();
        String qualifier = msgLogDO.getSessionQualifier();
        String senderCompID ;
        String targetCompID ;
        if(Info.Side.Out.name().equals(msgLogDO.getSide())) {
            senderCompID = msgLogDO.getSenderCompID();
            targetCompID = msgLogDO.getTargetCompID();
        }else if(Info.Side.In.name().equals(msgLogDO.getSide())) {
            senderCompID = msgLogDO.getTargetCompID();
            targetCompID = msgLogDO.getSenderCompID();
        }else{
            throw new RuntimeException("Invalid side type "+msgLogDO.getSide());
        }

        return FixMessageUtil.toXML(beginString,senderCompID
                ,targetCompID, qualifier, msgLogDO.getText());
    }

}
