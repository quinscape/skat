package de.fforw.skat.runtime.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class JsEntryPointController
{
    @RequestMapping("/game/**")
    public String serveGame()
    {
        return "game";
    }

    @RequestMapping("/admin/**")
    public String serveAdminApplicationEndpoint()
    {
        return "admin";
    }

    @RequestMapping("/login")
    public String serveLogin()
    {
        return "login";
    }
}
