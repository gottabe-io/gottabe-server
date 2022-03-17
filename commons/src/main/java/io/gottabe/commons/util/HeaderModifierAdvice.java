package io.gottabe.commons.util;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@ControllerAdvice
public class HeaderModifierAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(final MethodParameter returnType, final Class<? extends HttpMessageConverter<?>> converterType) {
        return returnType.getParameterType().equals(ResponseEntity.class);
    }

    @Override
    public Object beforeBodyWrite(final Object body,
                                  final MethodParameter returnType,
                                  final MediaType selectedContentType,
                                  final Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  final ServerHttpRequest request,
                                  final ServerHttpResponse response) {
        if (CollectionUtils.isEmpty(response.getHeaders().get("Cache-Control"))
            && CollectionUtils.isEmpty(response.getHeaders().get("Pragma"))) {
            response.getHeaders().add("Cache-Control", "no-cache, no-store, must-revalidate");
            response.getHeaders().add("Pragma", "no-cache");
            response.getHeaders().add("Expires", "0");
        }
        return body;
    }
}