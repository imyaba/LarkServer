package com.github.hollykunge.security.portal.service;

import com.github.hollykunge.security.common.biz.BaseBiz;
import com.github.hollykunge.security.common.msg.TableResultResponse;
import com.github.hollykunge.security.common.util.Query;
import com.github.hollykunge.security.entity.Feedback;
import com.github.hollykunge.security.mapper.FeedbackMapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;

/**
 * @ClassName FeedBackService
 * @Description TODO
 * @Author hollykunge
 * @Date 2019/6/30 13:29
 * @Version 1.0
 **/
@Service
@Transactional(rollbackFor = Exception.class)
public class FeedbackService extends BaseBiz<FeedbackMapper, Feedback> {
    @Override
    protected String getPageName() {
        return null;
    }

    @Override
    public TableResultResponse<Feedback> selectByQuery(Query query) {
        Class<Feedback> clazz = (Class<Feedback>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1];
        Example example = new Example(clazz);
        if(query.entrySet().size()>0) {
            Example.Criteria criteria = example.createCriteria();
            for (Map.Entry<String, Object> entry : query.entrySet()) {
                criteria.andLike(entry.getKey(), "%" + entry.getValue().toString() + "%");
            }
        }
        Page<Object> result = PageHelper.startPage(query.getPageNo(), query.getPageSize());
        List<Feedback> list = mapper.selectByExample(example);
        return new TableResultResponse<Feedback>(result.getPageSize(), result.getPageNum() ,list.size()/10+1, list.size(), list);
    }
}
