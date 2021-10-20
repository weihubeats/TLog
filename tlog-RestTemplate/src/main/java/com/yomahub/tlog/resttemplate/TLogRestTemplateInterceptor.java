package com.yomahub.tlog.resttemplate;

import com.yomahub.tlog.constant.TLogConstants;
import com.yomahub.tlog.context.SpanIdGenerator;
import com.yomahub.tlog.context.TLogContext;
import com.yomahub.tlog.spring.TLogSpringAware;
import com.yomahub.tlog.utils.LocalhostUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.NonNull;

import java.io.IOException;

/**
 * @author : wh
 * @date : 2021/10/20 17:32
 * @description:
 */
public class TLogRestTemplateInterceptor implements ClientHttpRequestInterceptor {

    private static final Logger log = LoggerFactory.getLogger(TLogRestTemplateInterceptor.class);

    @Override
    public ClientHttpResponse intercept(@NonNull HttpRequest request,@NonNull byte[] body,@NonNull ClientHttpRequestExecution execution) throws IOException {

        String traceId = TLogContext.getTraceId();
        if(StringUtils.isNotBlank(traceId)) {
            String appName = TLogSpringAware.getProperty("spring.application.name");
            HttpHeaders headers = request.getHeaders();
            headers.add(TLogConstants.TLOG_TRACE_KEY, traceId);
            headers.add(TLogConstants.TLOG_SPANID_KEY, SpanIdGenerator.generateNextSpanId());
            headers.add(TLogConstants.PRE_IVK_APP_KEY, appName);
            headers.add(TLogConstants.PRE_IVK_APP_HOST, LocalhostUtil.getHostName());
            headers.add(TLogConstants.PRE_IP_KEY, LocalhostUtil.getHostIp());
        } else {
            log.debug("[TLOG]本地threadLocal变量没有正确传递traceId,本次调用不传递traceId");
        }
        return execution.execute(request, body);
    }
}
