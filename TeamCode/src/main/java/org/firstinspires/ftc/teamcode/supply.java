package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.util.ElapsedTime;

public class supply {
    private final hwManager hwManager;
    private final ElapsedTime timer = new ElapsedTime();

    private boolean sending = false;

    public supply(hwManager hwManager) {
        this.hwManager = hwManager;
    }

    public void send() {
        hwManager.supply.setPosition(0.6);
        timer.reset();
        sending = true;
    }

    public void update() {
        if (sending && timer.seconds() > 0.5) {
            hwManager.supply.setPosition(0.85);
            sending = false;
        }
    }
}
