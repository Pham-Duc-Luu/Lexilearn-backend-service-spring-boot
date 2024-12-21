package com.MainBackendService.argumentResolver;

import com.MainBackendService.dto.AccessTokenDetailsDto;
import com.MainBackendService.model.User;
import com.MainBackendService.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Optional;

@Component
public class AccessTokenDetailsArgumentResolver implements HandlerMethodArgumentResolver {

    @Autowired
    private UserService userService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        // Check if the parameter type is UserDetailsDto
        return AccessTokenDetailsDto.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, org.springframework.web.bind.support.WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();

        // Extract values from request attributes
        String email = (String) request.getAttribute("email");
        String name = (String) request.getAttribute("name");
        Optional<User> existUser = userService.findUserByEmail(email);
        if (existUser.isEmpty()) throw new IllegalArgumentException("Some thing went wrong");


        if (email == null || name == null) {
            throw new IllegalArgumentException("Required user details not found in the request.");
        }

        return new AccessTokenDetailsDto(existUser.get().getUserId(), email, name);
    }
}