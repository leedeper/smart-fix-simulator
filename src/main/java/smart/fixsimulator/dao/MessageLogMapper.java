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

package smart.fixsimulator.dao;

import io.mybatis.mapper.Mapper;

import io.mybatis.mapper.example.Example;
import io.mybatis.mapper.example.ExampleMapper;
import org.springframework.util.StringUtils;
import smart.fixsimulator.dataobject.MessageLogDO;

import java.util.List;


/**
 * message log DAO
 *
 * @author Leedeper
 */
public interface MessageLogMapper extends Mapper<MessageLogDO,Long> {


    default List<MessageLogDO> getAllMessageLogDesc(MessageLogDO contition){
        Example<MessageLogDO> example =new Example<>();
        Example.Criteria<MessageLogDO> criteria = example.createCriteria();

        if(!StringUtils.isEmpty(contition.getSide())) {
            criteria.andEqualTo(MessageLogDO::getSide, contition.getSide());
        }

        if(!StringUtils.isEmpty(contition.getMsgType())) {
            criteria.andEqualTo(MessageLogDO::getMsgType, contition.getMsgType());
        }

        if(!StringUtils.isEmpty(contition.getBeginString())) {
            criteria.andEqualTo(MessageLogDO::getBeginString, contition.getBeginString());
        }

        if(!StringUtils.isEmpty(contition.getSenderCompID())) {
            criteria.andEqualTo(MessageLogDO::getSenderCompID, contition.getSenderCompID());
        }

        if(!StringUtils.isEmpty(contition.getTargetCompID())) {
            criteria.andEqualTo(MessageLogDO::getTargetCompID, contition.getTargetCompID());
        }

        example.orderBy(MessageLogDO::getTime, Example.Order.DESC);
        return this.selectByExample(example);
    }

    default List<MessageLogDO> getMsgTypeDistinct(){
        Example<MessageLogDO> example =new Example<>();
        example.setDistinct(true);
        example.selectColumns(MessageLogDO::getMsgType);
        example.orderBy(MessageLogDO::getMsgType, Example.Order.DESC);
        return this.selectByExample(example);
    }

}
