import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

public class SmartPlanner extends JFrame {
    private DefaultListModel<Task> taskListModel;
    private JList<Task> taskList;
    private JTextField taskInput;
    private JLabel dateLabel;
    private JTextArea dailyTipArea;
    private Map<LocalDate, List<Task>> tasksByDate;
    private LocalDate currentDate;

    // Daily tips arrays
    private String[] motivationalQuotes = {
            "The only way to do great work is to love what you do. - Steve Jobs",
            "Success is not final, failure is not fatal: it is the courage to continue that counts. - Winston Churchill",
            "Don't watch the clock; do what it does. Keep going. - Sam Levenson",
            "The future depends on what you do today. - Mahatma Gandhi",
            "It always seems impossible until it's done. - Nelson Mandela",
            "Your limitation—it's only your imagination.",
            "Push yourself, because no one else is going to do it for you.",
            "Great things never come from comfort zones.",
            "Dream it. Wish it. Do it.",
            "Success doesn't just find you. You have to go out and get it."
    };

    private String[] healthTips = {
            "💧 Drink at least 8 glasses of water today to stay hydrated!",
            "🚶 Take a 10-minute walk every hour to boost your energy.",
            "🥗 Include colorful vegetables in your meals for better nutrition.",
            "😴 Aim for 7-9 hours of quality sleep tonight.",
            "🧘 Practice 5 minutes of deep breathing to reduce stress.",
            "🏃 Do 10 jumping jacks to get your blood flowing.",
            "🍎 Eat a healthy snack like fruits or nuts between meals.",
            "📱 Take breaks from screens every 20 minutes to rest your eyes.",
            "🌅 Get some sunlight exposure to boost your vitamin D.",
            "🤸 Stretch for 5 minutes to improve flexibility and posture."
    };

    private String[] productivityHacks = {
            "🍅 Use the Pomodoro Technique: 25 minutes focused work, 5-minute break.",
            "📝 Write down your top 3 priorities for the day each morning.",
            "📧 Check emails only at specific times to avoid constant interruptions.",
            "🎯 Break large tasks into smaller, manageable chunks.",
            "🚫 Learn to say 'no' to non-essential commitments.",
            "📱 Put your phone in another room while working on important tasks.",
            "⏰ Use time-blocking to schedule specific activities throughout your day.",
            "🧹 Keep your workspace clean and organized for better focus.",
            "💡 Do your most challenging work when your energy is highest.",
            "📊 Review your progress weekly and adjust your strategies."
    };

    public SmartPlanner() {
        tasksByDate = new HashMap<>();
        currentDate = LocalDate.now();
        initializeUI();
        updateDailyContent();
    }

    private void initializeUI() {
        setTitle("Smart Planner - Daily Task Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Header Panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Main Content Panel
        JPanel mainPanel = createMainPanel();
        add(mainPanel, BorderLayout.CENTER);

        // Footer Panel
        JPanel footerPanel = createFooterPanel();
        add(footerPanel, BorderLayout.SOUTH);

        setSize(800, 600);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(74, 144, 226));
        panel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("Smart Planner");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        dateLabel = new JLabel();
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        dateLabel.setForeground(Color.WHITE);
        updateDateLabel();

        JPanel dateNavPanel = new JPanel(new FlowLayout());
        dateNavPanel.setOpaque(false);

        JButton prevDayBtn = new JButton("◀ Previous");
        JButton nextDayBtn = new JButton("Next ▶");
        JButton todayBtn = new JButton("Today");

        styleButton(prevDayBtn);
        styleButton(nextDayBtn);
        styleButton(todayBtn);

        prevDayBtn.addActionListener(e -> {
            currentDate = currentDate.minusDays(1);
            updateDailyContent();
        });

        nextDayBtn.addActionListener(e -> {
            currentDate = currentDate.plusDays(1);
            updateDailyContent();
        });

        todayBtn.addActionListener(e -> {
            currentDate = LocalDate.now();
            updateDailyContent();
        });

        dateNavPanel.add(prevDayBtn);
        dateNavPanel.add(todayBtn);
        dateNavPanel.add(nextDayBtn);

        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(dateLabel, BorderLayout.CENTER);
        panel.add(dateNavPanel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 0));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Task Panel
        JPanel taskPanel = createTaskPanel();
        panel.add(taskPanel);

        // Daily Tips Panel
        JPanel tipsPanel = createTipsPanel();
        panel.add(tipsPanel);

        return panel;
    }

    private JPanel createTaskPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Daily Tasks"));

        // Task input
        JPanel inputPanel = new JPanel(new BorderLayout(5, 0));
        taskInput = new JTextField();
        taskInput.setFont(new Font("Arial", Font.PLAIN, 14));

        JButton addButton = new JButton("Add Task");
        addButton.setBackground(new Color(76, 175, 80));
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);

        inputPanel.add(taskInput, BorderLayout.CENTER);
        inputPanel.add(addButton, BorderLayout.EAST);

        // Task list
        taskListModel = new DefaultListModel<>();
        taskList = new JList<>(taskListModel);
        taskList.setCellRenderer(new TaskCellRenderer());
        taskList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(taskList);
        scrollPane.setPreferredSize(new Dimension(350, 300));

        // Task action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton completeButton = new JButton("Complete");
        JButton deleteButton = new JButton("Delete");

        completeButton.setBackground(new Color(33, 150, 243));
        completeButton.setForeground(Color.WHITE);
        completeButton.setFocusPainted(false);

        deleteButton.setBackground(new Color(244, 67, 54));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setFocusPainted(false);

        buttonPanel.add(completeButton);
        buttonPanel.add(deleteButton);

        // Event listeners
        addButton.addActionListener(e -> addTask());
        taskInput.addActionListener(e -> addTask());
        completeButton.addActionListener(e -> toggleTaskCompletion());
        deleteButton.addActionListener(e -> deleteTask());

        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createTipsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Daily Wisdom"));

        dailyTipArea = new JTextArea();
        dailyTipArea.setEditable(false);
        dailyTipArea.setWrapStyleWord(true);
        dailyTipArea.setLineWrap(true);
        dailyTipArea.setFont(new Font("Arial", Font.PLAIN, 14));
        dailyTipArea.setBackground(new Color(248, 249, 250));
        dailyTipArea.setBorder(new EmptyBorder(15, 15, 15, 15));

        JScrollPane scrollPane = new JScrollPane(dailyTipArea);
        scrollPane.setPreferredSize(new Dimension(350, 400));

        JButton refreshTipsButton = new JButton("Get New Tips");
        refreshTipsButton.setBackground(new Color(156, 39, 176));
        refreshTipsButton.setForeground(Color.WHITE);
        refreshTipsButton.setFocusPainted(false);
        refreshTipsButton.addActionListener(e -> updateDailyTips());

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(refreshTipsButton, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.setBackground(new Color(236, 239, 241));

        JLabel statusLabel = new JLabel("Stay productive and motivated! 🚀");
        statusLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        statusLabel.setForeground(new Color(102, 102, 102));

        panel.add(statusLabel);
        return panel;
    }

    private void styleButton(JButton button) {
        button.setBackground(new Color(255, 255, 255, 50));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    }

    private void addTask() {
        String taskText = taskInput.getText().trim();
        if (!taskText.isEmpty()) {
            Task task = new Task(taskText);

            // Add task to current date
            tasksByDate.computeIfAbsent(currentDate, k -> new ArrayList<>()).add(task);

            // Update UI if viewing current date
            updateTaskList();
            taskInput.setText("");
        }
    }

    private void toggleTaskCompletion() {
        int selectedIndex = taskList.getSelectedIndex();
        if (selectedIndex >= 0) {
            Task task = taskListModel.getElementAt(selectedIndex);
            task.setCompleted(!task.isCompleted());
            taskList.repaint();
        }
    }

    private void deleteTask() {
        int selectedIndex = taskList.getSelectedIndex();
        if (selectedIndex >= 0) {
            Task task = taskListModel.getElementAt(selectedIndex);
            List<Task> tasks = tasksByDate.get(currentDate);
            if (tasks != null) {
                tasks.remove(task);
            }
            updateTaskList();
        }
    }

    private void updateDailyContent() {
        updateDateLabel();
        updateTaskList();
        updateDailyTips();
    }

    private void updateDateLabel() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy");
        dateLabel.setText(currentDate.format(formatter));
    }

    private void updateTaskList() {
        taskListModel.clear();
        List<Task> tasks = tasksByDate.get(currentDate);
        if (tasks != null) {
            for (Task task : tasks) {
                taskListModel.addElement(task);
            }
        }
    }

    private void updateDailyTips() {
        Random random = new Random(currentDate.toEpochDay()); // Consistent tips for each date

        String quote = motivationalQuotes[random.nextInt(motivationalQuotes.length)];
        String healthTip = healthTips[random.nextInt(healthTips.length)];
        String productivityHack = productivityHacks[random.nextInt(productivityHacks.length)];

        String tipsText = "🌟 DAILY MOTIVATION\n" +
                quote + "\n\n" +
                "💪 HEALTH TIP\n" +
                healthTip + "\n\n" +
                "⚡ PRODUCTIVITY HACK\n" +
                productivityHack;

        dailyTipArea.setText(tipsText);
    }

    // Task class
    static class Task {
        private String description;
        private boolean completed;
        private LocalDate createdDate;

        public Task(String description) {
            this.description = description;
            this.completed = false;
            this.createdDate = LocalDate.now();
        }

        public String getDescription() { return description; }
        public boolean isCompleted() { return completed; }
        public void setCompleted(boolean completed) { this.completed = completed; }
        public LocalDate getCreatedDate() { return createdDate; }

        @Override
        public String toString() {
            return description;
        }
    }

    // Custom cell renderer for tasks
    static class TaskCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (value instanceof Task) {
                Task task = (Task) value;
                String text = task.getDescription();

                if (task.isCompleted()) {
                    setText("✅ " + text);
                    setForeground(isSelected ? Color.WHITE : new Color(76, 175, 80));
                } else {
                    setText("⏳ " + text);
                    setForeground(isSelected ? Color.WHITE : Color.BLACK);
                }
            }

            setBorder(new EmptyBorder(5, 10, 5, 10));
            return this;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Set system look and feel - alternative approach
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (Exception e) {
                // If Nimbus is not available, fall back to default
                System.out.println("Could not set look and feel: " + e.getMessage());
            }

            new SmartPlanner();
        });
    }
}