package site.easy.to.build.crm.controller.restAPI;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import site.easy.to.build.crm.entity.*;
import site.easy.to.build.crm.service.customer.CustomerService;
import site.easy.to.build.crm.service.depense.BudgetService;
import site.easy.to.build.crm.service.depense.DepenseService;
import site.easy.to.build.crm.service.depense.TauxService;
import site.easy.to.build.crm.service.export.ExportService;
import site.easy.to.build.crm.service.lead.LeadService;
import site.easy.to.build.crm.service.ticket.TicketService;
import site.easy.to.build.crm.service.user.UserService;
import site.easy.to.build.crm.util.AuthenticationUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/rest")
public class RestAPIController {
    @Autowired
    private BudgetService budgetService;
    @Autowired
    private DepenseService depenseService;
    @Autowired
    private TauxService tauxService;
    @Autowired
    private LeadService leadService;
    @Autowired
    private TicketService ticketService;
    @Autowired
    private AuthenticationUtils authenticationUtils;
    @Autowired
    private UserService userService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private ExportService exportService;



    @GetMapping("/budgets")
    public ResponseEntity<List<Budget>> getAllBudgets() {
        List<Budget> budgets = budgetService.findAll();
        return ResponseEntity.ok(budgets);  // Renvoie la liste des budgets en JSON
    }

    @GetMapping("/depenses")
    public ResponseEntity<List<Depense>> getAllDepenses() {
        List<Depense> depenses = depenseService.findAll();
        return ResponseEntity.ok(depenses);
    }

    @PostMapping("/modif-lead/{leadId}/{amount}")
    public ResponseEntity<String> modifyLead(@PathVariable int leadId, @PathVariable double amount) {
        try {
            Depense depense = depenseService.findByLeadId(leadId);
            depense.setAmount(BigDecimal.valueOf(amount));
            depenseService.save(depense);
            return ResponseEntity.ok("modify");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lead introuvable avec l'ID : " + leadId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la modification du lead.");
        }
    }

    @PostMapping("/modif-ticket/{ticketId}/{amount}")
    public ResponseEntity<String> modifyTicket(@PathVariable int ticketId, @PathVariable double amount) {
        try {
            Depense depense = depenseService.findByTicketId(ticketId);
            depense.setAmount(BigDecimal.valueOf(amount));
            depenseService.save(depense);
            return ResponseEntity.ok("modify");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ticket introuvable avec l'ID : " + ticketId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la modification du ticket.");
        }
    }

    @PostMapping("/delete-lead/{leadId}")
    public ResponseEntity<String> deleteLead(@PathVariable int leadId) {
        try {
            leadService.delete(leadService.findByLeadId(leadId));
            depenseService.deleteByLeadId(leadId);
            return ResponseEntity.ok("deleted");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lead introuvable avec l'ID : " + leadId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la modification du lead.");
        }
    }

    @PostMapping("/delete-ticket/{ticketId}")
    public ResponseEntity<String> deleteTicket(@PathVariable int ticketId) {
        try {
            ticketService.delete(ticketService.findByTicketId(ticketId));
            depenseService.deleteByTicketId(ticketId);
            return ResponseEntity.ok("deleted");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ticket introuvable avec l'ID : " + ticketId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la modification du ticket.");
        }
    }

    @PostMapping("/insert-taux/{pourcentage}")
    public ResponseEntity<String> insertTaux(@PathVariable double pourcentage) {
        try {
            Taux taux = new Taux();
            taux.setCreatedAt(LocalDateTime.now());
            taux.setPourcentage(BigDecimal.valueOf(pourcentage));
            tauxService.save(taux);
            return ResponseEntity.ok("inserted taux");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Taux inserted");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de l'insertion.");
        }
    }

    @GetMapping("/last-taux")
    public ResponseEntity<Taux> lastTaux() {
        Taux taux = tauxService.getLast();
        return ResponseEntity.ok(taux);
    }

    @PostMapping("/export/{fileName}")
    public ResponseEntity<String> export(@PathVariable String fileName) {
        String filename = "/Users/hedyhamael/ITU/S6/Eval/dataExport/"+fileName;
        Customer customer = new Customer();


        User user = userService.findAll().get(0);
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filename))) {
            String data = bufferedReader.readLine();
            String[] split = data.split("/");

            String[] customerSrting = split[0].split(";");

            customer.setName(customerSrting[1].replace("name:", ""));
            customer.setEmail(customerSrting[2].replace("email:", ""));
            customer.setCreatedAt(LocalDateTime.now());
            customer.setCountry("Madagascar");
            customer.setPhone("123");
            customer.setUser(user);
            customerService.save(customer);

            exportService.exportBudget(data, customer);

            exportService.exportDepense(data, user, customer);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok("export");
    }




}
