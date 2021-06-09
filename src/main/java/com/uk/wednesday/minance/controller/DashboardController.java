package com.uk.wednesday.minance.controller;

import com.uk.wednesday.minance.service.BaseService;
import com.uk.wednesday.minance.service.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

@Controller
@RequestMapping(DashboardController.SERVICE_URI)
public class DashboardController {

    private BaseService baseService;

    private StatsService statsService;

    public static final String SERVICE_URI = "/minance";
    @Autowired
    public DashboardController(BaseService baseService, StatsService statsService) {
        this.baseService = baseService;
        this.statsService = statsService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {

        return "/dashboard";
    }

    @GetMapping("/run")
    public String run() throws InterruptedException {

        //baseService.start();

        return "/dashboard";
    }

    @GetMapping("/stats")
    public String analyseStats() throws IOException {

        statsService.analyseTotalStats();

        return "/dashboard";
    }
}
