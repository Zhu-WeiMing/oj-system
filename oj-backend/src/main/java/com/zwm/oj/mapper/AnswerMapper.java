package com.zwm.oj.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zwm.oj.model.vo.AnswerGetVo;
import com.zwm.oj.model.entity.Answer;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface AnswerMapper extends BaseMapper<Answer> {

    @Select("select * from answer a,user u where questionId = #{questionId} and a.userId = u.id")
    List<AnswerGetVo> getAnswerByQuestionId(Long questionId);

    @Update("update answer set isDelete = 1 where questionId = #{questionId}")
    void deleteAnswer(Long questionId);
}
