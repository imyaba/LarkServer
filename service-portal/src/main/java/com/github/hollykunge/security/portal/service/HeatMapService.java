package com.github.hollykunge.security.portal.service;

import com.github.hollykunge.security.common.biz.BaseBiz;
import com.github.hollykunge.security.entity.HeatMap;
import com.github.hollykunge.security.mapper.HeatMapMapper;
import com.github.hollykunge.security.vo.HeatMapVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 工作热力图业务实现
 *
 * @author zhhongyu
 * @since 2019-06-19
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class HeatMapService extends BaseBiz<HeatMapMapper, HeatMap> {
    @Override
    protected String getPageName() {
        return null;
    }

    public List<HeatMapVO> getHeatMap(List<HeatMap> heatMapList) {
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();

        c.setTime(new Date());
        c.add(Calendar.YEAR, -1);
        Date oneYearAgo = c.getTime();
        Date today = new Date();
        List<HeatMapVO> heatMaps = new ArrayList<>();
        try {
            heatMaps = getHeatMapData(ft.format(oneYearAgo), ft.format(today), heatMapList);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return heatMaps;
    }

    /**
     * @description: 日期类工具
     * @author: dd
     * @since: 2019-06-21
     * <p>
     * "date": "2017-05-03",
     * "commits": 1,
     * "month": 4,
     * "day": 3,
     * "week": "0"
     */
    private List<HeatMapVO> getHeatMapData(String dBegin, String dEnd, List<HeatMap> heatMapList) throws ParseException {
        //日期工具类准备
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        //设置开始时间
        Calendar calBegin = Calendar.getInstance();
        calBegin.setTime(format.parse(dBegin));

        //设置结束时间
        Calendar calEnd = Calendar.getInstance();
        calEnd.setTime(format.parse(dEnd));

        //装返回的日期集合容器
        List<HeatMapVO> heatMaps = new ArrayList<HeatMapVO>();

        // 每次循环给calBegin日期加一天，直到calBegin.getTime()时间等于dEnd
        while (format.parse(dEnd).after(calBegin.getTime())) {

            HeatMapVO heatMap = new HeatMapVO();
            // 根据日历的规则，为给定的日历字段添加或减去指定的时间量
            calBegin.add(Calendar.DAY_OF_MONTH, 1);
            for (HeatMap heatMap1 : heatMapList) {
                if (heatMap1.equals(calBegin)) {
                    heatMap.setCommits(heatMap1.getCommits());
                }else {
                    heatMap.setCommits(0);
                }
            }
            heatMap.setDate(format.format(calBegin.getTime()));
            heatMap.setDay(calBegin.get(Calendar.DAY_OF_WEEK) - 1);
            heatMap.setMonth(calBegin.get(Calendar.MONTH));
            heatMap.setWeek(String.valueOf(calBegin.get(Calendar.WEEK_OF_YEAR) - 1));
            if (isLastDayOfMonth(format.parse(dBegin))){
                heatMap.setLastDay(true);
            }
            if (isLastWeekOfMonth(format.parse(dBegin))){
                heatMap.setLastWeek(true);
            }

            heatMaps.add(heatMap);
        }
        return heatMaps;
    }

    /**
     * 判断是否为当月最后一天
     * @param date
     * @return
     */
    private Boolean isLastDayOfMonth(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DATE, (calendar.get(Calendar.DATE) + 1));
        if (calendar.get(Calendar.DAY_OF_MONTH) == 1) {
            return true;
        }
        return false;
    }

    /**
     * 判断指定的日期是不是这个月的最后一周
     * @param date 判断日期
     * @return
     */
    private Boolean isLastWeekOfMonth(Date date) {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf1=new SimpleDateFormat("yyyy-MM");
        String value1=sdf1.format(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int val=cal.get(Calendar.WEEK_OF_MONTH);
        try {
            cal.setTime(sdf.parse(value1+"-00"));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        cal.add(Calendar.MONTH, 1);
        int val1=cal.get(Calendar.WEEK_OF_MONTH);
        if(val==val1){
            return true;
        }
        return false;
    }
}
