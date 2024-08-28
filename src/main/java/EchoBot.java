import java.util.Scanner;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;


public class EchoBot {
    private static final TaskList tasks = new TaskList();

    public static void main(String[] args) {
        Storage.loadTasksFromFile(tasks); // Load tasks when starting
        Scanner scanner = new Scanner(System.in);


        System.out.println("____________________________________________________________");
        System.out.println(" Hello! I'm EchoBot");
        System.out.println(" What can I do for you?");
        System.out.println("____________________________________________________________");

        label:
        while (true) {
            String userInput = scanner.nextLine();
            String[] inputParts = userInput.split(" ", 2); // Splits input into command and the rest

            String command = inputParts[0];

            switch (command) {
                case "bye":
                    System.out.println("____________________________________________________________");
                    System.out.println(" Bye! Hope to see you again soon!");
                    System.out.println("____________________________________________________________");
                    break label;
                case "list":
                    listTasks();
                    break;
                case "mark": {
                    handleMarkCommand(inputParts);
                    Storage.saveTasksToFile(tasks);
                    break;
                }
                case "unmark": {
                    handleUnmarkCommand(inputParts);
                    Storage.saveTasksToFile(tasks);
                    break;
                }
                case "todo": {
                    handleTodoCommand(inputParts);
                    Storage.saveTasksToFile(tasks);
                    break;
                }
                case "deadline": {
                    try {
                        handleDeadlineCommand(inputParts);
                        Storage.saveTasksToFile(tasks);
                    } catch (IllegalArgumentException e) {
                        System.out.println("____________________________________________________________");
                        System.out.println(" " + e.getMessage());
                        System.out.println(" Please enter date in the format dd/MM/yyyy?");
                        System.out.println("____________________________________________________________");
                    }
                    break;
                }
                case "event": {
                    try {
                        handleEventCommand(inputParts);
                        Storage.saveTasksToFile(tasks);
                    } catch (IllegalArgumentException e) {
                        System.out.println("____________________________________________________________");
                        System.out.println(" " + e.getMessage());
                        System.out.println(" Please enter date in the format dd/MM/yyyy?");
                        System.out.println("____________________________________________________________");
                    }
                    break;
                }
                case "delete": {
                    handleDeleteCommand(inputParts);
                    Storage.saveTasksToFile(tasks);
                    break;
                }
                case "findbydate":
                    handleFindByDateCommand(inputParts);
                    Storage.saveTasksToFile(tasks);
                    break;
                default:
                    System.out.println("____________________________________________________________");
                    System.out.println(" I'm sorry, I don't recognize that command.");
                    System.out.println("____________________________________________________________");
                    break;
            }
        }

        scanner.close();
    }
    private static void listTasks() {
        System.out.println("____________________________________________________________");
        System.out.println(" Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println(" " + (i + 1) + "." + tasks.get(i));
        }
        System.out.println("____________________________________________________________");
    }
    private static void handleMarkCommand(String[] inputParts) {
        if (inputParts.length < 2) {
            System.out.println("____________________________________________________________");
            System.out.println(" Please specify the task number to mark as done.");
            System.out.println("____________________________________________________________");
        } else {
            int taskNumber = Integer.parseInt(inputParts[1]) - 1;
            if (taskNumber < tasks.size() && taskNumber >= 0) {
                tasks.get(taskNumber).markAsDone();
                Storage.saveTasksToFile(tasks);
                System.out.println("____________________________________________________________");
                System.out.println(" Great! I've marked this task as done:");
                System.out.println("   " + tasks.get(taskNumber));
                System.out.println("____________________________________________________________");
            } else {
                System.out.println("____________________________________________________________");
                System.out.println(" Oops! That task number doesn't exist.");
                System.out.println("____________________________________________________________");
            }
        }
    }
    private static void handleUnmarkCommand(String[] inputParts) {
        if (inputParts.length < 2) {
            System.out.println("____________________________________________________________");
            System.out.println(" Please specify the task number to unmark.");
            System.out.println("____________________________________________________________");
        } else {
            int taskNumber = Integer.parseInt(inputParts[1]) - 1;
            if (taskNumber < tasks.size() && taskNumber >= 0) {
                tasks.get(taskNumber).markAsNotDone();
                Storage.saveTasksToFile(tasks);
                System.out.println("____________________________________________________________");
                System.out.println(" OK! I've marked this task as not done yet:");
                System.out.println("   " + tasks.get(taskNumber));
                System.out.println("____________________________________________________________");
            } else {
                System.out.println("____________________________________________________________");
                System.out.println(" Oops! That task number doesn't exist.");
                System.out.println("____________________________________________________________");
            }
        }
    }

    private static void handleTodoCommand(String[] inputParts) {
        if (inputParts.length < 2 || inputParts[1].trim().isEmpty()) {
            System.out.println("____________________________________________________________");
            System.out.println(" The description of a todo cannot be empty.");
            System.out.println("____________________________________________________________");
        } else {
            tasks.addTask(new Todo(inputParts[1]));
            System.out.println("____________________________________________________________");
            System.out.println(" Got it. I've added this task:");
            System.out.println("   " + tasks.get(tasks.size() - 1));
            System.out.println(" Now you have " + tasks.size() + " tasks in the list.");
            System.out.println("____________________________________________________________");
        }
    }

    private static void handleDeadlineCommand(String[] inputParts) {
        if (inputParts.length < 2 || !inputParts[1].contains("/by ")) {
            System.out.println("____________________________________________________________");
            System.out.println(" The description of a deadline must include a due date.");
            System.out.println("____________________________________________________________");
        } else {
            String[] details = inputParts[1].split(" /by ");
            if (details.length < 2 || details[0].trim().isEmpty()) {
                System.out.println("____________________________________________________________");
                System.out.println(" The description of a deadline cannot be empty.");
                System.out.println("____________________________________________________________");
            } else {
                tasks.addTask(new Deadline(details[0], details[1]));
                System.out.println("____________________________________________________________");
                System.out.println(" Got it. I've added this task:");
                System.out.println("   " + tasks.get(tasks.size() - 1));
                System.out.println(" Now you have " + tasks.size() + " tasks in the list.");
                System.out.println("____________________________________________________________");
            }
        }
    }

    private static void handleEventCommand(String[] inputParts) {
        if (inputParts.length < 2 || !inputParts[1].contains("/from ") || !inputParts[1].contains("/to ")) {
            System.out.println("____________________________________________________________");
            System.out.println(" The description of an event must include start and end times.");
            System.out.println("____________________________________________________________");
        } else {
            String[] details = inputParts[1].split(" /from | /to ");
            if (details.length < 3 || details[0].trim().isEmpty()) {
                System.out.println("____________________________________________________________");
                System.out.println(" The description of an event cannot be empty.");
                System.out.println("____________________________________________________________");
            } else {
                tasks.addTask(new Event(details[0], details[1], details[2]));
                System.out.println("____________________________________________________________");
                System.out.println(" Got it. I've added this task:");
                System.out.println("   " + tasks.get(tasks.size() - 1));
                System.out.println(" Now you have " + tasks.size() + " tasks in the list.");
                System.out.println("____________________________________________________________");
            }
        }
    }
    private static void handleDeleteCommand(String[] inputParts) {
        if (inputParts.length < 2) {
            System.out.println("____________________________________________________________");
            System.out.println(" Please specify the task number to delete.");
            System.out.println("____________________________________________________________");
        } else {
            int taskNumber = Integer.parseInt(inputParts[1]) - 1;
            if (taskNumber < tasks.size() && taskNumber >= 0) {
                Task removedTask = tasks.removeTask(taskNumber);
                System.out.println("____________________________________________________________");
                System.out.println(" Noted. I've removed this task:");
                System.out.println("   " + removedTask);
                System.out.println(" Now you have " + tasks.size() + " tasks in the list.");
                System.out.println("____________________________________________________________");
            } else {
                System.out.println("____________________________________________________________");
                System.out.println(" Oops! That task number doesn't exist.");
                System.out.println("____________________________________________________________");
            }
        }
    }
    private static void handleFindByDateCommand(String[] inputParts) {
        if (inputParts.length < 2) {
            System.out.println("Please specify a date in the format yyyy-mm-dd.");
            return;
        }

        String dateString = inputParts[1];  // Assuming the date is the second element
        try {
            LocalDate queryDate = LocalDate.parse(dateString);
            System.out.println("Tasks occurring on " + queryDate.format(DateTimeFormatter.ofPattern("MMM dd yyyy")) + ":");
            boolean hasTasks = false;
            for (Task task : tasks.getTasks()) {
                if (task instanceof Deadline && ((Deadline) task).by.equals(queryDate)) {
                    System.out.println(task);
                    hasTasks = true;
                } else if (task instanceof Event && (((Event) task).from.equals(queryDate) || ((Event) task).to.equals(queryDate))) {
                    System.out.println(task);
                    hasTasks = true;
                }
            }
            if (!hasTasks) {
                System.out.println("No tasks found on this date.");
            }
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format. Please use yyyy-mm-dd.");
        }
    }
}
