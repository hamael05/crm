package site.easy.to.build.crm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import site.easy.to.build.crm.entity.User;
import site.easy.to.build.crm.service.importCSV.ImportService;
import site.easy.to.build.crm.service.user.UserService;
import site.easy.to.build.crm.util.AuthenticationUtils;

@Controller
@RequestMapping("/employee/import")
public class ImportController {

    @Autowired
    ImportService importService;
    @Autowired
    AuthenticationUtils authenticationUtils;
    @Autowired
    UserService userService;

    @GetMapping("/manager/import-page")
    public String importPage (@ModelAttribute("alert") String alertMessage, Model model) {
        model.addAttribute("alert", alertMessage);
        return "import/import";
    }

    @PostMapping("/manager/do-import")
    public String doImport(@RequestParam("file1") MultipartFile file1, @RequestParam("file2") MultipartFile file2, @RequestParam("file3") MultipartFile file3, RedirectAttributes redirectAttributes, Authentication authentication) {

        int userId = authenticationUtils.getLoggedInUserId(authentication);
        User user = userService.findById(userId);
        if(user.isInactiveUser()) {
            System.out.println("error/account-inactive");
        }

        importService.importCustomer(file1.getOriginalFilename(), user);

        System.out.println(importService.checkAllError(file2.getOriginalFilename(), file3.getOriginalFilename()));
        if (!importService.checkAllError(file2.getOriginalFilename(), file3.getOriginalFilename()).equals("success")) {
            redirectAttributes.addFlashAttribute("alert", importService.checkAllError(file2.getOriginalFilename(), file3.getOriginalFilename()));
            importService.deleteCustomer(file1.getOriginalFilename());
            return "redirect:/employee/import/manager/import-page";
        }

        importService.importBudget(file2.getOriginalFilename());
        importService.importDepense(file3.getOriginalFilename(), user);

        return "redirect:/employee/import/manager/import-page";
    }


}
