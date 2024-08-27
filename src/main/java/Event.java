import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class Event extends Task {
    protected LocalDate from;
    protected LocalDate to;


    public Event(String description, String from, String to) {
        super(description);
        this.from = parseDate(from); // Parse the string into a LocalDate
        this.to = parseDate(to);
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
        return "[E]" + super.toString() + " (from: "
                + from.format(DateTimeFormatter.ofPattern("MMM dd yyyy")) + " to: "
                + to.format(DateTimeFormatter.ofPattern("MMM dd yyyy")) + ")";
    }

    @Override
    public String toFileFormat() {
        return "E | " + super.toFileFormat() + " | " + from + " | " + to;
    }
}
