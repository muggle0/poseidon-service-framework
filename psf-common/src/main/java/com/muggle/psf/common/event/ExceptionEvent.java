package com.muggle.psf.common.event;

import org.springframework.context.ApplicationEvent;

/**
 * @Description:
 * @Author: muggle
 * @Date: 2020/4/2$
 **/
public class ExceptionEvent extends ApplicationEvent {
    private static final long serialVersionUID = -773875079317416802L;
    private String message;

    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public ExceptionEvent(final String message, final Object source) {
        super(source);
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}
