package org.apache.flink.sqlclient.api.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class HelloController {
    /**
     * Get all users list.
     *
     * @return the list
     */
    @GetMapping("/greets")
    public String getGreeting() {
        return "Hello Dipanjan";
    }

}