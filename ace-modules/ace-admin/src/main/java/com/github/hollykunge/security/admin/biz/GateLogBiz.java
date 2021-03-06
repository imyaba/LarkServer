package com.github.hollykunge.security.admin.biz;

import com.github.hollykunge.security.admin.entity.GateLog;
import com.github.hollykunge.security.admin.mapper.GateLogMapper;
import com.github.hollykunge.security.common.biz.BaseBiz;
import com.github.hollykunge.security.common.exception.BaseException;
import com.github.hollykunge.security.common.msg.TableResultResponse;
import com.github.hollykunge.security.common.util.EntityUtils;
import com.github.hollykunge.security.common.util.Query;
import com.github.hollykunge.security.common.util.UUIDUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.lang.reflect.ParameterizedType;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * ${DESCRIPTION}
 *
 * @author 协同设计小组
 * @create 2017-07-01 14:36
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class GateLogBiz extends BaseBiz<GateLogMapper,GateLog> {

    @Override
    public void insert(GateLog entity) {
        mapper.insert(entity);
    }

    @Override
    public void insertSelective(GateLog entity) {
        entity.setId(UUIDUtils.generateShortUuid());
        mapper.insertSelective(entity);
    }

    @Override
    protected String getPageName() {
        return null;
    }

    @Override
    public TableResultResponse<GateLog> selectByQuery(Query query) {
        Example example = new Example(GateLog.class);
        if(query.entrySet().size()>0) {
            Example.Criteria criteria = example.createCriteria();
            for (Map.Entry<String, Object> entry : query.entrySet()) {
                boolean exitTime = this.setCreTimeCondition(criteria, entry);
                if(exitTime){
                    continue;
                }
                criteria.andLike(entry.getKey(), "%" + entry.getValue().toString() + "%");
            }
        }
        example.setOrderByClause("CRT_TIME DESC");
        Page<Object> result = PageHelper.startPage(query.getPageNo(), query.getPageSize());
        List<GateLog> list = mapper.selectByExample(example);
        return new TableResultResponse<GateLog>(result.getPageSize(), result.getPageNum() ,result.getPages(), result.getTotal(), list);
    }

    @Override
    public TableResultResponse<GateLog> selectByQueryM(Query query, String type) {
        Example example = new Example(GateLog.class);
        if(query.entrySet().size()>0) {
            Example.Criteria criteria = example.createCriteria();
            for (Map.Entry<String, Object> entry : query.entrySet()) {
                List<String> valueList = new ArrayList<>();
                valueList = (List<String>) entry.getValue();
                if (type.equals("log")){
                    criteria.andNotEqualTo(entry.getKey(),  valueList.get(0) ).andNotEqualTo(entry.getKey(),  valueList.get(1));
                }
                else if (type.equals("Security")){
                    criteria.orEqualTo(entry.getKey(),  valueList.get(0) ).orEqualTo(entry.getKey(),  valueList.get(1) );
                }
                boolean exitTime = this.setCreTimeCondition(criteria, entry);
                if(exitTime){
                    continue;
                }
            }
        }
        Page<Object> result = PageHelper.startPage(query.getPageNo(), query.getPageSize());
        List<GateLog> list = mapper.selectByExample(example);
        return new TableResultResponse<GateLog>(result.getPageSize(), result.getPageNum() ,result.getPages(), result.getTotal(), list);
    }
    private Date stringToDate(String source, String pattern) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        Date date = null;
        try {
            date = simpleDateFormat.parse(source);
        } catch (Exception e) {
        }
        return date;
    }
    private boolean setCreTimeCondition(Example.Criteria criteria,Map.Entry<String, Object> entry ){
        if("crtTime".equals(entry.getKey())){
            if(StringUtils.isEmpty(entry.getValue())){
                throw new BaseException("输入时间不能为空...");
            }
            String[] dateSplits = entry.getValue().toString().trim().split(",");
            Date beginDate = this.stringToDate(dateSplits[0], "yyyy-MM-dd");
            Date endDate = this.stringToDate(dateSplits[1], "yyyy-MM-dd");
            criteria.andBetween(entry.getKey(),beginDate,endDate);
            return true;
        }
        return false;
    }
}
