package org.sader.security.controller;


import org.sader.security.model.Port;
import org.sader.security.service.RestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Set;

@RestController
public class RestAPI {
    Logger logger = LoggerFactory.getLogger(RestAPI.class);
    @Autowired
    RestService restService;

    @GetMapping("/allowMe")
    public ServiceResponse<Boolean> allowMe(HttpServletRequest request, HttpServletResponse response) {

        logger.info("Allow me request from  {}", request.getRemoteAddr());
        restService.openSSHForIp(request.getRemoteAddr());
        return new ServiceResponse<>(true);

    }

    @GetMapping("/checkMe")
    public ServiceResponse<Set<Port>> checkMe(HttpServletRequest request, HttpServletResponse response) {
        logger.info("check port status {}", request.getRemoteAddr());
        return new ServiceResponse<>(restService.checkPorts(request.getRemoteAddr()));

    }

    @GetMapping("/log")
    public ServiceResponse<Void> log(HttpServletRequest request, HttpServletResponse response) {
        logger.info("Log request from  {}", request.getRemoteAddr());
        restService.log(request);
        return new ServiceResponse<>(null);

    }


    @GetMapping("/checkAvailability")
    public ServiceResponse<Boolean> checkAvailability(@RequestParam String url, HttpServletRequest request, HttpServletResponse response) {
        logger.info("Url request from  {} {}", request.getRemoteAddr(), url);
        return new ServiceResponse<>(restService.checkURL(url));

    }

    @GetMapping("IRANAccess")
    public ServiceResponse<String> iranAccess(HttpServletRequest request) {
        logger.info("Iran access ");
        boolean res = restService.toggleIranAccess();
        return new ServiceResponse<>("Iran Access status : " + res);
    }
}
