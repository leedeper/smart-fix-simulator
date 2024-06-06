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
import smart.fixsimulator.dao.MessageLogMapper;
import smart.fixsimulator.dataobject.MessageLogDO;

import smart.fixsimulator.dto.MessageLogDTO;
import smart.fixsimulator.service.MessageLogService;
import smart.fixsimulator.web.response.PageResponseResult;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

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
    public PageResponseResult<List<MessageLogDTO>> getMessageLog(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        List<MessageLogDO> all = messageLogMapper.selectList(null);
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

}
