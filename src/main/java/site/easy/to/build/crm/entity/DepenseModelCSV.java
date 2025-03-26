package site.easy.to.build.crm.entity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DepenseModelCSV {
    private String customer_email;
    private String subject_or_email;
    private String type;
    private String status;
    private double expense;

    public String getCustomer_email() {
        return customer_email;
    }

    public void setCustomer_email(String customer_email) {
        this.customer_email = customer_email;
    }

    public String getSubject_or_email() {
        return subject_or_email;
    }

    public void setSubject_or_email(String subject_or_email) {
        this.subject_or_email = subject_or_email;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type.toLowerCase().trim();

    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        if (getType().equals("lead")){
            List<String> options = new ArrayList<>();
            options.add("meeting-to-schedule");
            options.add("assign-to-sales");
            options.add("archived");
            options.add("success");
            if (!options.contains(status.toLowerCase().trim())) {
                throw new IllegalArgumentException("Status invalid : " + status);
            }
        } else if (getType().equals("ticket")) {
            List<String> options = new ArrayList<>();
            options.add("open");
            options.add("assigned");
            options.add("on-hold");
            options.add("in-progress");
            options.add("resolved");
            options.add("closed");
            options.add("reopened");
            options.add("pending-customer-response");
            options.add("escalated");
            options.add("archived");
            if (!options.contains(status.toLowerCase().trim())) {
                throw new IllegalArgumentException("Status invalid : " + status);
            }
        }
        this.status = status.toLowerCase().trim();
    }

    public double getExpense() {
        return expense;
    }

    public void setExpense(double expense) {
        this.expense = expense;
    }

    public void setExpense(String expense) {
        if (!isValidNumber(expense)) {
            throw new IllegalArgumentException("Expense format invalid : " + expense);
        }
        double expenseDouble = Double.parseDouble(expense.replace(" ", "").replace(".", "").replace(",", ".").trim());
        if (expenseDouble < 0) {
            throw new IllegalArgumentException("Expense must be a positive number");
        }
        // Arrondir à 2 chiffres après la virgule
        BigDecimal expenseBigDecimal = new BigDecimal(expenseDouble);
        expenseBigDecimal = expenseBigDecimal.setScale(2, RoundingMode.HALF_UP);

        this.expense = expenseBigDecimal.doubleValue();
    }

    public static boolean isValidNumber(String input) {
        try {
            // Essayer avec le format anglais (1,234.56)
            NumberFormat formatUS = NumberFormat.getInstance(Locale.US);
            formatUS.parse(input);
            return true;
        } catch (ParseException e1) {
            try {
                // Essayer avec le format français (1.234,56)
                NumberFormat formatFR = NumberFormat.getInstance(Locale.FRANCE);
                formatFR.parse(input);
                return true;
            } catch (ParseException e2) {
                return false; // Aucun format ne correspond
            }
        }
    }


}
