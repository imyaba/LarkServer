package com.github.hollykunge.security.portal.controller;

import com.github.hollykunge.security.common.rest.BaseController;
import com.github.hollykunge.security.entity.Feedback;
import com.github.hollykunge.security.portal.service.FeedbackService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 问题反馈
 * @author hollykunge
 */
@RestController
@RequestMapping("feedback")
public class FeedbackController extends BaseController<FeedbackService, Feedback> {

}
