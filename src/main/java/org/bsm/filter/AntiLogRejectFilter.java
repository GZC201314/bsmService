package org.bsm.filter;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

/**
 * @author GZC
 * @create 2022-10-17 22:58
 * @desc 访日志注入过滤器
 */

public class AntiLogRejectFilter extends Filter<ILoggingEvent> {
    String str = "\n;\r\n";

    @Override
    public FilterReply decide(ILoggingEvent iLoggingEvent) {
        String[] split = str.split(";");
        String message = iLoggingEvent.getMessage();
        for (String s : split) {
            if (message.contains(s)) {
                return FilterReply.DENY;
            }
        }
        return FilterReply.ACCEPT;
    }

}
