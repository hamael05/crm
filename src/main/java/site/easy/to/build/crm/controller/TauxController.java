package site.easy.to.build.crm.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import site.easy.to.build.crm.entity.Budget;
import site.easy.to.build.crm.entity.Customer;
import site.easy.to.build.crm.entity.Taux;
import site.easy.to.build.crm.entity.User;
import site.easy.to.build.crm.service.depense.TauxService;
import site.easy.to.build.crm.util.AuthorizationUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/employee/taux")
public class TauxController {

    @Autowired
    TauxService tauxService;

    @GetMapping("/manager/taux-depense")
    public String modifTaux(Model model) {
        Taux taux = tauxService.getLast();
        model.addAttribute("taux", taux);
        return "taux/taux-depense";
    }

    @PostMapping("/manager/taux-depense")
    public String insertModifTaux(Model model, @ModelAttribute("taux") @Validated Taux taux, BindingResult bindingResult) {

        if(bindingResult.hasErrors()) {
            Taux lastTaux = tauxService.getLast();
            model.addAttribute("taux",lastTaux);
            return "taux/taux-depense";
        }

        Taux newTaux = new Taux();
        newTaux.setPourcentage(taux.getPourcentage());
        newTaux.setCreatedAt(LocalDateTime.now());
        tauxService.save(newTaux);

        model.addAttribute("taux", newTaux);
        return "taux/taux-depense";
    }
}
