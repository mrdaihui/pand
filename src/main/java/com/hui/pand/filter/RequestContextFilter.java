package com.hui.pand.filter;


import com.hui.pand.context.RequestContext;
import org.slf4j.MDC;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

@Component
@ServletComponentScan
@WebFilter(urlPatterns = "/**")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestContextFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        // 业务ID
        MDC.put("serviceid", RequestContext.getContext().rebuildRequestId());

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}