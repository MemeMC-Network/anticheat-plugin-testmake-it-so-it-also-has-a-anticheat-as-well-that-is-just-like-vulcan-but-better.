package network.mememc.memeac.data;

import org.bukkit.Location;
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
    
    // Advanced tracking for new checks
    // Timer check data
    private long lastMoveTime = 0;
    private final List<Double> timerSamples = new ArrayList<>();
    private int timerViolations = 0;
    
    // Invalid packet data
    private int duplicatePackets = 0;
    private int rapidRotations = 0;
    private Float lastYaw = null;
    private Float lastPitch = null;
    
    // Elytra exploit data
    private int elytraViolations = 0;
    private Boolean lastGlidingState = null;
    private final List<Double> speedSamples = new ArrayList<>();
    
    // XRay detection data
    private int oresMined = 0;
    private int blocksBroken = 0;
    private int hiddenOres = 0;
    private int directOreFinds = 0;
    private int recentOres = 0;
    private long lastOreMineTime = 0;
    private Location lastOreLocation = null;
    private final List<Location> recentBreakLocations = new ArrayList<>();
    
    // AI behavior analysis data
    private double movementPrecision = 0;
    private double hitAccuracy = 0;
    private int totalAttacks = 0;
    private int successfulHits = 0;
    private long totalReactionTime = 0;
    private int reactionTimeCount = 0;
    private double averageMiningSpeed = 0;
    private final Queue<Long> attackTimes = new LinkedList<>();
    
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
    
    // Timer check methods
    public void setLastMoveTime(long time) {
        this.lastMoveTime = time;
    }
    
    public long getLastMoveTime() {
        return lastMoveTime;
    }
    
    public void addTimerSample(double ratio) {
        timerSamples.add(ratio);
        if (timerSamples.size() > 20) {
            timerSamples.remove(0);
        }
    }
    
    public List<Double> getTimerSamples() {
        return new ArrayList<>(timerSamples);
    }
    
    public double getAverageTimerRatio() {
        if (timerSamples.isEmpty()) return 1.0;
        return timerSamples.stream().mapToDouble(Double::doubleValue).average().orElse(1.0);
    }
    
    public void incrementTimerViolations() {
        timerViolations = Math.min(timerViolations + 1, 10);
    }
    
    public void decrementTimerViolations() {
        timerViolations = Math.max(timerViolations - 1, 0);
    }
    
    public int getTimerViolations() {
        return timerViolations;
    }
    
    // Invalid packet methods
    public void incrementDuplicatePackets() {
        duplicatePackets++;
    }
    
    public void resetDuplicatePackets() {
        duplicatePackets = 0;
    }
    
    public int getDuplicatePackets() {
        return duplicatePackets;
    }
    
    public void incrementRapidRotations() {
        rapidRotations = Math.min(rapidRotations + 1, 10);
    }
    
    public void decrementRapidRotations() {
        rapidRotations = Math.max(rapidRotations - 1, 0);
    }
    
    public int getRapidRotations() {
        return rapidRotations;
    }
    
    public void setLastYaw(float yaw) {
        this.lastYaw = yaw;
    }
    
    public Float getLastYaw() {
        return lastYaw;
    }
    
    public void setLastPitch(float pitch) {
        this.lastPitch = pitch;
    }
    
    public Float getLastPitch() {
        return lastPitch;
    }
    
    // Elytra exploit methods
    public void incrementElytraViolations() {
        elytraViolations = Math.min(elytraViolations + 1, 10);
    }
    
    public void resetElytraViolations() {
        elytraViolations = 0;
    }
    
    public int getElytraViolations() {
        return elytraViolations;
    }
    
    public void setLastGlidingState(boolean gliding) {
        this.lastGlidingState = gliding;
    }
    
    public Boolean getLastGlidingState() {
        return lastGlidingState;
    }
    
    public void addSpeedSample(double speed) {
        speedSamples.add(speed);
        if (speedSamples.size() > 30) {
            speedSamples.remove(0);
        }
    }
    
    public List<Double> getSpeedSamples() {
        return new ArrayList<>(speedSamples);
    }
    
    public double getAverageSpeed() {
        if (speedSamples.isEmpty()) return 0;
        return speedSamples.stream().mapToDouble(Double::doubleValue).average().orElse(0);
    }
    
    // XRay detection methods
    public void incrementOresMined() {
        oresMined++;
    }
    
    public int getOresMined() {
        return oresMined;
    }
    
    public void incrementBlocksBroken() {
        blocksBroken++;
    }
    
    public int getBlocksBroken() {
        return blocksBroken;
    }
    
    public void incrementHiddenOres() {
        hiddenOres = Math.min(hiddenOres + 1, 20);
    }
    
    public int getHiddenOres() {
        return hiddenOres;
    }
    
    public void incrementDirectOreFinds() {
        directOreFinds = Math.min(directOreFinds + 1, 10);
    }
    
    public int getDirectOreFinds() {
        return directOreFinds;
    }
    
    public void incrementRecentOres() {
        recentOres++;
    }
    
    public void resetRecentOres() {
        recentOres = 0;
    }
    
    public int getRecentOres() {
        return recentOres;
    }
    
    public void setLastOreMineTime(long time) {
        this.lastOreMineTime = time;
    }
    
    public long getLastOreMineTime() {
        return lastOreMineTime;
    }
    
    public void setLastOreLocation(Location location) {
        this.lastOreLocation = location;
    }
    
    public Location getLastOreLocation() {
        return lastOreLocation;
    }
    
    public void addRecentBreakLocation(Location location) {
        recentBreakLocations.add(location);
        if (recentBreakLocations.size() > 50) {
            recentBreakLocations.remove(0);
        }
    }
    
    public List<Location> getRecentBreakLocations() {
        return new ArrayList<>(recentBreakLocations);
    }
    
    // AI behavior analysis methods
    public void updateMovementPrecision(double precision) {
        this.movementPrecision = precision;
    }
    
    public double getMovementPrecision() {
        return movementPrecision;
    }
    
    public void recordHit(boolean successful) {
        totalAttacks++;
        if (successful) {
            successfulHits++;
        }
        hitAccuracy = (double) successfulHits / totalAttacks * 100;
    }
    
    public double getHitAccuracy() {
        return hitAccuracy;
    }
    
    public int getTotalAttacks() {
        return totalAttacks;
    }
    
    public void addReactionTime(long reactionTime) {
        totalReactionTime += reactionTime;
        reactionTimeCount++;
    }
    
    public double getAverageReactionTime() {
        if (reactionTimeCount == 0) return 0;
        return (double) totalReactionTime / reactionTimeCount;
    }
    
    public void updateAverageMiningSpeed(double speed) {
        this.averageMiningSpeed = speed;
    }
    
    public double getAverageMiningSpeed() {
        return averageMiningSpeed;
    }
    
    public Queue<Long> getAttackTimes() {
        return new LinkedList<>(attackTimes);
    }
}
