package org.bsm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author GZC
 * @create 2021-10-25 11:27
 * @desc RouterController
 */
@Controller
public class RouterController {
    @GetMapping({"/", "index", "index.html"})
    public String index() {
        return "views/index";
    }

    @GetMapping({"/noauth"})
    public String noAuth() {
        return "views/noauth";
    }

    @GetMapping("/toLogin")
    public String toLogin() {
        return "login";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/level1/{id}")
    public String level1(@PathVariable("id") int id) {
        return "views/level1/" + id;
    }

    @GetMapping("/level2/{id}")
    public String level2(@PathVariable("id") int id) {
        return "views/level2/" + id;
    }

    @GetMapping("/level3/{id}")
    public String level3(@PathVariable("id") int id) {
        return "views/level3/" + id;
    }

    /*CSRF ROUTER*/
    @GetMapping("/toUpdate")
    public String test(Model model) {
        return "csrf/csrfTest";
    }

//    @PostMapping("/update_token")
//    public String getToken() {
//        return "csrf/csrf_token";
//    }
}
