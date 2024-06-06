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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import smart.fixsimulator.dao.MessageMapper;
import smart.fixsimulator.dataobject.MessageDO;
import smart.fixsimulator.dto.MessageDTO;
import smart.fixsimulator.service.MessageService;
import smart.fixsimulator.web.response.PageResponseResult;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * implement message svr
 *
 * @author Leedeper
 */
@Slf4j
@Service
public class MessageServiceImpl implements MessageService<List<MessageDTO>> {

    @Autowired
    private MessageMapper messageMapper;
    @Override
    public PageResponseResult<List<MessageDTO>> getMessage(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        List<MessageDO> all = messageMapper.selectList(null);
        int countNum = (int)((Page<MessageDO>) all).getTotal();
        log.debug("find msg from db total : {}/{}", all.size(),countNum);
        List<MessageDTO> resultMsgList = new ArrayList<>();
        for(MessageDO msgDo : all){
            MessageDTO msgDTO = new MessageDTO();
            BeanUtils.copyProperties(msgDo,msgDTO);
            resultMsgList.add(msgDTO);
        }
        log.debug("dto total : {}", resultMsgList.size());

        PageResponseResult<List<MessageDTO>> result=new PageResponseResult<>();
        if(!resultMsgList.isEmpty()){
            result.setContent(resultMsgList);
            result.setTotalNum(countNum);
            result.setPageNum(pageNum);
            result.setPageSize(pageSize);
        }

        log.debug("query all message : {}，{}", result,result.getContent());
        return result;
    }
}
