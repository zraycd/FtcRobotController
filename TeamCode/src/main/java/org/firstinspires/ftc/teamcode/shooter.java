package org.firstinspires.ftc.teamcode;
public class shooter {
    hwManager hwManager;
    public shooter(hwManager hwManager) {
        this.hwManager = hwManager;
    }
    int currentMode = 0;
    public void turnOff() {
        hwManager.shooter.setVelocity(0);
        hwManager.angle.setPosition(0.57);
        currentMode = 0;
    }
    public void shootFar() {
        if (currentMode == 1) {turnOff(); return;}
        hwManager.shooter.setVelocity(2300);
        hwManager.angle.setPosition(0.50);
        currentMode = 1;
    }
    public void shootClose() {
        if (currentMode == 2) {turnOff(); return;}
        hwManager.shooter.setVelocity(1850);
        hwManager.angle.setPosition(0.55);
        currentMode = 2;
    }
}
