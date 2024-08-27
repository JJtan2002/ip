import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class Deadline extends Task {
    protected LocalDate by;


    public Deadline(String description, String by) {
        super(description);
        this.by = parseDate(by); // Parse the string into a LocalDate
    }
    private LocalDate parseDate(String date) {
        List<DateTimeFormatter> formatters = new ArrayList<>();
        formatters.add(DateTimeFormatter.ofPattern("d/MM/yyyy"));
        formatters.add(DateTimeFormatter.ofPattern("dd/M/yyyy"));
        // Add more patterns as needed

        for (DateTimeFormatter formatter : formatters) {
            try {
                return LocalDate.parse(date, formatter);
            } catch (DateTimeParseException e) {
                // Continue trying other patterns
            }
        }

        throw new IllegalArgumentException("Date format not supported: " + date);
    }
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: "
                + by.format(DateTimeFormatter.ofPattern("MMM dd yyyy")) + ")";
    }
    @Override
    public String toFileFormat() {
        return "D | " + super.toFileFormat() + " | " + by;
    }
}
