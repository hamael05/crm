package site.easy.to.build.crm.service.importCSV;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import site.easy.to.build.crm.entity.*;
import site.easy.to.build.crm.service.customer.CustomerService;
import site.easy.to.build.crm.service.depense.BudgetService;
import site.easy.to.build.crm.service.depense.DepenseService;
import site.easy.to.build.crm.service.lead.LeadService;
import site.easy.to.build.crm.service.ticket.TicketService;
import site.easy.to.build.crm.service.user.UserService;
import site.easy.to.build.crm.util.AuthenticationUtils;

import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ImportService {

    @Autowired
    CustomerService customerService;
    @Autowired
    TicketService ticketService;
    @Autowired
    LeadService leadService;
    @Autowired
    DepenseService depenseService;
    @Autowired
    AuthenticationUtils authenticationUtils;
    @Autowired
    BudgetService budgetService;



    public void importCustomer(String filePathOriginal, User user) {
        String filePath = "/Users/hedyhamael/ITU/S6/Eval/dataCSV/" + filePathOriginal;
        if (filePath == null || filePath.trim().isEmpty()) {
            System.err.println("Le chemin du fichier CSV ne peut pas être vide ou null.");
            return;
        }

        try (CSVReader reader = new CSVReaderBuilder(new FileReader(filePath))
                .withCSVParser(new CSVParserBuilder().withSeparator(';').build())
                .build()) {

            reader.skip(1);
            List<String[]> records = reader.readAll();

            System.out.println("\n--- Début du traitement des données ---");



            for (String[] record : records) {
                if (record.length < 2) { // Vérifie que la ligne contient assez de colonnes
                    continue;
                }

                Customer customer = new Customer();
                customer.setEmail(record[0]);
                customer.setName(record[1]);
                customer.setCountry("Madagascar");
                customer.setPhone("123");
                customer.setUser(user);

                customerService.save(customer);
            }

            System.out.println("Importation terminée avec succès !");
        } catch (IOException | CsvException e) {
            System.err.println("Erreur lors de l'importation du fichier CSV : " + e.getMessage());
        }
    }

    public String checkErrorListDepenseCSV(String filePathOriginal) {
        String filePath = "/Users/hedyhamael/ITU/S6/Eval/dataCSV/" + filePathOriginal;
        if (filePath == null || filePath.trim().isEmpty()) {
            System.err.println("Le chemin du fichier CSV ne peut pas être vide ou null.");
            return "Le chemin du fichier CSV ne peut pas être vide ou null.";
        }

        try (CSVReader reader = new CSVReaderBuilder(new FileReader(filePath))
                .withCSVParser(new CSVParserBuilder().withSeparator(';').build())
                .build()) {

            reader.skip(1);
            List<String[]> records = reader.readAll();

            int ligne = 1;

            for (String[] record : records) {
                if (record.length < 5) { // Vérifie que la ligne contient assez de colonnes
                    continue;
                }
                Customer customer = customerService.findByEmail(record[0]);
                if (customer == null) {
                    return "Email inexistant : " + record[0] + " a la  ligne " + ligne + " dans depense";
                }
                DepenseModelCSV depenseModelCSV = new DepenseModelCSV();
                try {
                    depenseModelCSV.setExpense(record[4]);
                    depenseModelCSV.setType(record[2]);
                    depenseModelCSV.setStatus(record[3]);
                } catch (IllegalArgumentException e) {
                    return e.getMessage() + " a la ligne " + ligne + " de la depense";
                }
                ligne++;
            }

            System.out.println("Importation terminée avec succès !");
        } catch (IOException | CsvException e) {
            System.err.println("Erreur lors de l'importation du fichier CSV : " + e.getMessage());
        }
        return "success";
    }

    public void importDepense(String filePathOriginal, User user) {
        String filePath = "/Users/hedyhamael/ITU/S6/Eval/dataCSV/" + filePathOriginal;
        if (filePath == null || filePath.trim().isEmpty()) {
            System.err.println("Le chemin du fichier CSV ne peut pas être vide ou null.");
            return; // Arrêt de la méthode si le chemin est incorrect
        }

        try (CSVReader reader = new CSVReaderBuilder(new FileReader(filePath))
                .withCSVParser(new CSVParserBuilder().withSeparator(';').build())
                .build()) {

            reader.readNext(); // Skip la première ligne (en-tête)

            String[] record;
            while ((record = reader.readNext()) != null) { // Boucle pour lire chaque ligne
                if (record.length < 5) { // Vérifie que la ligne contient assez de colonnes
                    continue;
                }

                DepenseModelCSV depenseModelCSV = new DepenseModelCSV();
                depenseModelCSV.setExpense(record[4]);
                Customer customer = customerService.findByEmail(record[0]);
                Depense depense = new Depense();
                depense.setCreatedAt(LocalDateTime.now());
                depense.setAmount(BigDecimal.valueOf(depenseModelCSV.getExpense()));

                if (record[2].equals("ticket")) {
                    Ticket ticket = new Ticket();
                    ticket.setCustomer(customer);
                    ticket.setSubject(record[1]);
                    ticket.setCreatedAt(LocalDateTime.now());
                    ticket.setStatus(record[3]);
                    ticket.setAmount(depenseModelCSV.getExpense());
                    ticket.setPriority("low");
                    ticket.setEmployee(user);

                    ticketService.save(ticket);

                    depense.setTicket(ticket);
                    depenseService.save(depense);
                }

                if (record[2].equals("lead")) {
                    Lead lead = new Lead();
                    lead.setCustomer(customer);
                    lead.setName(record[1]);
                    lead.setCreatedAt(LocalDateTime.now());
                    lead.setStatus(record[3]);
                    lead.setAmount(depenseModelCSV.getExpense());
                    lead.setEmployee(user);

                    leadService.save(lead);

                    depense.setLead(lead);
                    depenseService.save(depense);
                }
            }

            System.out.println("Importation terminée avec succès !");
        } catch (IOException | CsvException e) {
            System.err.println("Erreur lors de l'importation du fichier CSV : " + e.getMessage());
        }
    }


    public void importBudget(String filePathOriginal) {
        String filePath = "/Users/hedyhamael/ITU/S6/Eval/dataCSV/" + filePathOriginal;
        if (filePath == null || filePath.trim().isEmpty()) {
            System.err.println("Le chemin du fichier CSV ne peut pas être vide ou null.");
            return; // Arrêt de la méthode si le chemin est incorrect
        }

        try (CSVReader reader = new CSVReaderBuilder(new FileReader(filePath))
                .withCSVParser(new CSVParserBuilder().withSeparator(';').build())
                .build()) {

            reader.readNext(); // Skip la première ligne (en-tête)

            String[] record;

            while ((record = reader.readNext()) != null) { // Boucle pour lire chaque ligne
                if (record.length < 2) { // Vérifie que la ligne contient assez de colonnes
                    continue;
                }
                Customer customer = customerService.findByEmail(record[0]);

                Budget budget = new Budget();
                budget.setCustomer(customer);
                budget.setCreatedAt(LocalDateTime.now());
                budget.setAmount(BigDecimal.valueOf(Double.parseDouble(record[1])));

                budgetService.save(budget);

            }

            System.out.println("Importation terminée avec succès !");
        } catch (IOException | CsvException e) {
            System.err.println("Erreur lors de l'importation du fichier CSV : " + e.getMessage());
        }

    }

    public String checkErrorBudget(String filePathOriginal) {
        String filePath = "/Users/hedyhamael/ITU/S6/Eval/dataCSV/" + filePathOriginal;
        if (filePath == null || filePath.trim().isEmpty()) {
            System.err.println("Le chemin du fichier CSV ne peut pas être vide ou null.");
            return "Le chemin du fichier CSV ne peut pas être vide ou null."; // Arrêt de la méthode si le chemin est incorrect
        }

        try (CSVReader reader = new CSVReaderBuilder(new FileReader(filePath))
                .withCSVParser(new CSVParserBuilder().withSeparator(';').build())
                .build()) {

            reader.readNext(); // Skip la première ligne (en-tête)

            String[] record;

            int ligne = 1;

            while ((record = reader.readNext()) != null) { // Boucle pour lire chaque ligne
                if (record.length < 2) { // Vérifie que la ligne contient assez de colonnes
                    continue;
                }
                Customer customer = customerService.findByEmail(record[0]);
                if (customer == null) {
                    return "Email inexistant : " + record[0] + " a la  ligne " + ligne + " dans budget";
                }
                Budget budget = new Budget();
                try {
                    budget.setAmount(record[1]);
                } catch (IllegalArgumentException e) {
                    return e.getMessage() + "a la ligne " + ligne + " du budget";
                }
                ligne++;
            }

            System.out.println("Importation terminée avec succès !");
        } catch (IOException | CsvException e) {
            System.err.println("Erreur lors de l'importation du fichier CSV : " + e.getMessage());
        }
        return "success";
    }

    public String checkAllError (String filePathOriginalBudget, String filePathOriginalDepense) {
        if (!checkErrorBudget(filePathOriginalBudget).equals("success")) {
            return checkErrorBudget(filePathOriginalBudget);
        } else if (!checkErrorListDepenseCSV(filePathOriginalDepense).equals("success")) {
            return checkErrorListDepenseCSV(filePathOriginalDepense);
        }
        return "success";
    }

    public void deleteCustomer(String filePathOriginal) {
        String filePath = "/Users/hedyhamael/ITU/S6/Eval/dataCSV/" + filePathOriginal;
        if (filePath == null || filePath.trim().isEmpty()) {
            System.err.println("Le chemin du fichier CSV ne peut pas être vide ou null.");
            return;
        }

        try (CSVReader reader = new CSVReaderBuilder(new FileReader(filePath))
                .withCSVParser(new CSVParserBuilder().withSeparator(';').build())
                .build()) {

            reader.skip(1);
            List<String[]> records = reader.readAll();

            System.out.println("\n--- Début du traitement des données ---");

            for (String[] record : records) {
                if (record.length < 2) { // Vérifie que la ligne contient assez de colonnes
                    continue;
                }

                Customer customer = customerService.findByEmail(record[0]);
                customerService.delete(customer);

            }

            System.out.println("Importation terminée avec succès !");
        } catch (IOException | CsvException e) {
            System.err.println("Erreur lors de l'importation du fichier CSV : " + e.getMessage());
        }
    }
}
