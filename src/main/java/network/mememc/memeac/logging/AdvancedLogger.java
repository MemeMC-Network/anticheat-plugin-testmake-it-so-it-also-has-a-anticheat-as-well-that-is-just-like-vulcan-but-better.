package network.mememc.memeac.logging;

import network.mememc.memeac.MemeAC;
import network.mememc.memeac.data.ViolationData;
import org.bukkit.entity.Player;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AdvancedLogger {
    
    private final MemeAC plugin;
    private final ExecutorService logExecutor = Executors.newSingleThreadExecutor();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final SimpleDateFormat fileFormat = new SimpleDateFormat("yyyy-MM-dd");
    
    private PrintWriter violationWriter;
    private PrintWriter debugWriter;
    private PrintWriter aiWriter;
    
    public AdvancedLogger(MemeAC plugin) {
        this.plugin = plugin;
        initializeLogFiles();
    }
    
    private void initializeLogFiles() {
        try {
            // Create logs directory
            Path logsDir = Paths.get(plugin.getDataFolder().getPath(), "logs");
            Files.createDirectories(logsDir);
            
            String dateStr = fileFormat.format(new Date());
            
            // Initialize violation log
            File violationFile = new File(logsDir.toFile(), "violations-" + dateStr + ".log");
            violationWriter = new PrintWriter(new FileWriter(violationFile, true));
            
            // Initialize debug log
            File debugFile = new File(logsDir.toFile(), "debug-" + dateStr + ".log");
            debugWriter = new PrintWriter(new FileWriter(debugFile, true));
            
            // Initialize AI analysis log
            File aiFile = new File(logsDir.toFile(), "ai-analysis-" + dateStr + ".log");
            aiWriter = new PrintWriter(new FileWriter(aiFile, true));
            
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to initialize log files: " + e.getMessage());
        }
    }
    
    public void logViolation(Player player, ViolationData violation) {
        if (violationWriter == null) return;
        
        CompletableFuture.runAsync(() -> {
            String timestamp = dateFormat.format(new Date());
            String playerInfo = String.format("%s (%s)", player.getName(), player.getUniqueId());
            String locationInfo = String.format("%.2f,%.2f,%.2f in %s", 
                player.getLocation().getX(),
                player.getLocation().getY(), 
                player.getLocation().getZ(),
                player.getWorld().getName());
            
            String logLine = String.format("[%s] VIOLATION | Player: %s | Check: %s | Certainty: %d%% | Reason: %s | Location: %s",
                timestamp, playerInfo, violation.getCheck().getName(), violation.getCertainty(), 
                violation.getReason(), locationInfo);
            
            violationWriter.println(logLine);
            violationWriter.flush();
            
        }, logExecutor);
    }
    
    public void logDebug(String message) {
        if (!plugin.getConfigManager().getBoolean("general.debug-mode") || debugWriter == null) return;
        
        CompletableFuture.runAsync(() -> {
            String timestamp = dateFormat.format(new Date());
            String logLine = String.format("[%s] DEBUG | %s", timestamp, message);
            
            debugWriter.println(logLine);
            debugWriter.flush();
            
        }, logExecutor);
    }
    
    public void logAIAnalysis(Player player, String analysis) {
        if (aiWriter == null) return;
        
        CompletableFuture.runAsync(() -> {
            String timestamp = dateFormat.format(new Date());
            String playerInfo = String.format("%s (%s)", player.getName(), player.getUniqueId());
            String logLine = String.format("[%s] AI-ANALYSIS | Player: %s | %s", 
                timestamp, playerInfo, analysis);
            
            aiWriter.println(logLine);
            aiWriter.flush();
            
        }, logExecutor);
    }
    
    public void logCheckPerformance(String checkName, long executionTime, boolean triggered) {
        if (!plugin.getConfigManager().getBoolean("general.debug-mode")) return;
        
        logDebug(String.format("CHECK-PERF | %s | %d ns | %s", 
            checkName, executionTime, triggered ? "TRIGGERED" : "CLEAN"));
    }
    
    public void logPlayerAction(Player player, String action, Object... details) {
        if (!plugin.getConfigManager().getBoolean("features.detailed-logging")) return;
        
        CompletableFuture.runAsync(() -> {
            String timestamp = dateFormat.format(new Date());
            String playerInfo = String.format("%s (%s)", player.getName(), player.getUniqueId());
            String detailsStr = details.length > 0 ? " | Details: " + formatDetails(details) : "";
            
            String logLine = String.format("[%s] PLAYER-ACTION | Player: %s | Action: %s%s",
                timestamp, playerInfo, action, detailsStr);
            
            debugWriter.println(logLine);
            debugWriter.flush();
            
        }, logExecutor);
    }
    
    private String formatDetails(Object... details) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < details.length; i++) {
            if (i > 0) sb.append(", ");
            sb.append(details[i]);
        }
        return sb.toString();
    }
    
    public CompletableFuture<String> exportViolations(String playerName, int days) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                StringBuilder export = new StringBuilder();
                export.append("=== MemeAC Violation Export ===\\n");
                export.append("Player: ").append(playerName).append("\\n");
                export.append("Export Date: ").append(dateFormat.format(new Date())).append("\\n");
                export.append("Days: ").append(days).append("\\n\\n");
                
                // Search through log files for violations
                Path logsDir = Paths.get(plugin.getDataFolder().getPath(), "logs");
                
                if (Files.exists(logsDir)) {
                    Files.walk(logsDir)
                        .filter(path -> path.getFileName().toString().startsWith("violations-"))
                        .filter(path -> path.getFileName().toString().endsWith(".log"))
                        .sorted()
                        .forEach(file -> {
                            try {
                                Files.lines(file)
                                    .filter(line -> line.contains(playerName))
                                    .forEach(line -> export.append(line).append("\\n"));
                            } catch (IOException e) {
                                // Skip file if error
                            }
                        });
                }
                
                return export.toString();
                
            } catch (Exception e) {
                return "Error exporting violations: " + e.getMessage();
            }
        }, logExecutor);
    }
    
    public void rotateLogFiles() {
        CompletableFuture.runAsync(() -> {
            try {
                // Close current writers
                if (violationWriter != null) violationWriter.close();
                if (debugWriter != null) debugWriter.close();
                if (aiWriter != null) aiWriter.close();
                
                // Reinitialize with new date
                initializeLogFiles();
                
                plugin.getLogger().info("Log files rotated successfully");
                
            } catch (Exception e) {
                plugin.getLogger().severe("Failed to rotate log files: " + e.getMessage());
            }
        }, logExecutor);
    }
    
    public void cleanup() {
        try {
            if (violationWriter != null) violationWriter.close();
            if (debugWriter != null) debugWriter.close();
            if (aiWriter != null) aiWriter.close();
            
            logExecutor.shutdown();
            
        } catch (Exception e) {
            plugin.getLogger().warning("Error during logger cleanup: " + e.getMessage());
        }
    }
}