package com.atex.plugins.textmining.service;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import com.atex.onecms.ws.common.ErrorResponseExceptionMapper;
import com.atex.onecms.ws.service.ErrorResponseException;
import com.polopoly.common.logging.LogUtil;

@Provider
public class ExceptionMapper implements
    javax.ws.rs.ext.ExceptionMapper<Exception>
{
    private static final Logger LOG = LogUtil.getLog();

    @Override
    public Response toResponse(Exception ex)
    {
        int status = 500;

        if (ex instanceof WebApplicationException) {
            status = ((WebApplicationException)ex).getResponse().getStatus();
        } else if (ex instanceof ErrorResponseException) {
            return new ErrorResponseExceptionMapper().toResponse((ErrorResponseException) ex);
        } else {
            LOG.log(Level.WARNING, "Exception: ", ex);
        }

        return Response.status(status)
            .entity(ex.getMessage())
            .type("text/plain")
            .build();
    }
}
