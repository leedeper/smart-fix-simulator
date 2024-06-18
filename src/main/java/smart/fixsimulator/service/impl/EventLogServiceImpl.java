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
import smart.fixsimulator.dao.EventLogMapper;
import smart.fixsimulator.dao.MessageLogMapper;
import smart.fixsimulator.dataobject.EventLogDO;
import smart.fixsimulator.dataobject.MessageLogDO;
import smart.fixsimulator.dto.EventLogDTO;
import smart.fixsimulator.dto.MessageLogDTO;
import smart.fixsimulator.service.EventLogService;
import smart.fixsimulator.web.response.PageResponseResult;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Desc:
 *
 * @author Leedeper
 */
@Slf4j
@Service
public class EventLogServiceImpl implements EventLogService<List<EventLogDTO>> {
    @Autowired
    private EventLogMapper eventLogMapper;
    @Override
    public PageResponseResult<List<EventLogDTO>> getMessageLog(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<EventLogDO> all = eventLogMapper.getAllEventLogDesc();
        List<EventLogDTO> resultMsgList = new ArrayList<>();
        for(EventLogDO eventLogDo : all){
            EventLogDTO dto = new EventLogDTO();
            BeanUtils.copyProperties(eventLogDo,dto);
            dto.setTime(new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(eventLogDo.getTime()));
            resultMsgList.add(dto);
        }
        PageResponseResult<List<EventLogDTO>> result=new PageResponseResult<>();
        if(!resultMsgList.isEmpty()){
            int countNum = (int)((Page<EventLogDO>) all).getTotal();
            result.setContent(resultMsgList);
            result.setTotalNum(countNum);
            result.setPageNum(pageNum);
            result.setPageSize(pageSize);
        }
        return result;
    }

    @Override
    public boolean deleteAll() {
        int total = eventLogMapper.delete(new EventLogDO());
        log.info("delete recored : {}",total);
        return true;
    }
}
