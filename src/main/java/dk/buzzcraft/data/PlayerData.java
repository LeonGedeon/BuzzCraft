package dk.buzzcraft.data;

public class PlayerData {
    private int snusTaken;
    /**
     * Tolerance builds up as Snus is used and decays over time.
     * It drives which buff/debuff tier the player is in.
     */
    private int tolerance;
    private boolean showScoreboard;
    private long lastSnusTime;

    public void incrementSnusTaken() {
        snusTaken++;
    }
    public int getSnusTaken() {
        return snusTaken;
    }

    public boolean isShowScoreboard() {
        return showScoreboard;
    }

    public void setShowScoreboard(boolean showScoreboard) {
        this.showScoreboard = showScoreboard;
    }

    public void setSnusTaken(int snusTaken) {
        this.snusTaken = snusTaken;
    }

    public int getTolerance() {
        return tolerance;
    }

    public void setTolerance(int tolerance) {
        this.tolerance = tolerance;
    }

    public long getLastSnusTime() {
        return lastSnusTime;
    }

    public void setLastSnusTime(long lastSnusTime) {
        this.lastSnusTime = lastSnusTime;
    }
}