package com.boardtest.board.member;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/member")
public class controller {
    @GetMapping
    public String hello(){
        return "helloWorld";
    }
}
