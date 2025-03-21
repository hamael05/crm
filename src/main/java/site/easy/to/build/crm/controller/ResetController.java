package site.easy.to.build.crm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import site.easy.to.build.crm.service.reset.ResetService;

@Controller
public class ResetController {

    @Autowired
    ResetService resetService;

    @PostMapping("/manager/reset")
    public String resetDatabase () {
        resetService.resetDatabase();
        return "redirect:/";
    }
}
