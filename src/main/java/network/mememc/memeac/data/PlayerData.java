package network.mememc.memeac.data;

import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class PlayerData {
    
    private final String playerName;
    private final long joinTime;
    
    // Movement data
    private int airTicks = 0;
    private double lastYDiff = 0;
    private double lastDistance = 0;
    private int speedViolations = 0;
    
    // Combat data
    private final Queue<Long> attackTimes = new LinkedList<>();
    private Vector lastLookDirection;
    private Entity lastTarget;
    private long lastAttackTime = 0;
    private int suspiciousRotations = 0;
    private int multiTargetHits = 0;
    private int suspiciousReach = 0;
    private final List<Double> reachSamples = new ArrayList<>();
    
    // Violation tracking
    private int totalViolations = 0;
    private long lastViolationTime = 0;
    
    public PlayerData(String playerName) {
        this.playerName = playerName;
        this.joinTime = System.currentTimeMillis();
    }
    
    // Movement methods
    public void incrementAirTicks() {
        airTicks++;
    }
    
    public void resetAirTicks() {
        airTicks = 0;
    }
    
    public int getAirTicks() {
        return airTicks;
    }
    
    public void setLastYDiff(double yDiff) {
        this.lastYDiff = yDiff;
    }
    
    public double getLastYDiff() {
        return lastYDiff;
    }
    
    public void setLastDistance(double distance) {
        this.lastDistance = distance;
    }
    
    public double getLastDistance() {
        return lastDistance;
    }
    
    public void incrementSpeedViolations() {
        speedViolations = Math.min(speedViolations + 1, 10);
    }
    
    public void decrementSpeedViolations() {
        speedViolations = Math.max(speedViolations - 1, 0);
    }
    
    public int getSpeedViolations() {
        return speedViolations;
    }
    
    // Combat methods
    public void addAttackTime(long time) {
        attackTimes.offer(time);
        if (attackTimes.size() > 20) {
            attackTimes.poll();
        }
    }
    
    public double getAverageCPS() {
        if (attackTimes.size() < 2) return 0;
        
        long totalTime = 0;
        Long[] times = attackTimes.toArray(new Long[0]);
        
        for (int i = 1; i < times.length; i++) {
            totalTime += times[i] - times[i - 1];
        }
        
        if (totalTime == 0) return 0;
        
        return (double) (times.length - 1) * 1000 / totalTime;
    }
    
    public void setLastLookDirection(Vector direction) {
        this.lastLookDirection = direction;
    }
    
    public Vector getLastLookDirection() {
        return lastLookDirection;
    }
    
    public void setLastTarget(Entity target) {
        this.lastTarget = target;
    }
    
    public Entity getLastTarget() {
        return lastTarget;
    }
    
    public void setLastAttackTime(long time) {
        this.lastAttackTime = time;
    }
    
    public long getLastAttackTime() {
        return lastAttackTime;
    }
    
    public void incrementSuspiciousRotations() {
        suspiciousRotations++;
    }
    
    public int getSuspiciousRotations() {
        return suspiciousRotations;
    }
    
    public void incrementMultiTargetHits() {
        multiTargetHits++;
    }
    
    public int getMultiTargetHits() {
        return multiTargetHits;
    }
    
    public void incrementSuspiciousReach() {
        suspiciousReach = Math.min(suspiciousReach + 1, 10);
    }
    
    public void decrementSuspiciousReach() {
        suspiciousReach = Math.max(suspiciousReach - 1, 0);
    }
    
    public int getSuspiciousReach() {
        return suspiciousReach;
    }
    
    public void addReachSample(double reach) {
        reachSamples.add(reach);
        if (reachSamples.size() > 20) {
            reachSamples.remove(0);
        }
    }
    
    public List<Double> getReachSamples() {
        return reachSamples;
    }
    
    public double getAverageReach() {
        if (reachSamples.isEmpty()) return 0;
        return reachSamples.stream().mapToDouble(Double::doubleValue).average().orElse(0);
    }
    
    public double getMaxReachInSamples() {
        if (reachSamples.isEmpty()) return 0;
        return reachSamples.stream().mapToDouble(Double::doubleValue).max().orElse(0);
    }
    
    // General methods
    public String getPlayerName() {
        return playerName;
    }
    
    public long getJoinTime() {
        return joinTime;
    }
    
    public void incrementTotalViolations() {
        totalViolations++;
        lastViolationTime = System.currentTimeMillis();
    }
    
    public int getTotalViolations() {
        return totalViolations;
    }
    
    public long getLastViolationTime() {
        return lastViolationTime;
    }
    
    public void resetCombatData() {
        suspiciousRotations = 0;
        multiTargetHits = 0;
    }
}
