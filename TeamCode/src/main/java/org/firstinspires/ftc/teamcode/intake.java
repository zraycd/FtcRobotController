package org.firstinspires.ftc.teamcode;
public class intake {
    hwManager hwManager;
    public intake(hwManager hwManager) {
        this.hwManager = hwManager;
    }
    public void toggle() {
        if (hwManager.intake.getVelocity() == 0) {
            hwManager.intake.setVelocity(2800);
        } else {
            hwManager.intake.setVelocity(0);
        }
    }
}
