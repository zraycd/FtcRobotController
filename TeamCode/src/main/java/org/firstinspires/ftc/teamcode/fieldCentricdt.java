package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

@TeleOp(name="fieldCentricdt", group = "testing")
public class fieldCentricdt extends OpMode {
    hwManager hwManager;
    drivetrain drivetrain;

    @Override
    public void init() {
        hwManager = new hwManager();
        drivetrain = new drivetrain(hwManager);
        hwManager.initHardware(hardwareMap);
        telemetry.addData("init", "complete");
    }

    @Override
    public void loop() {
        double x = -gamepad1.left_stick_x;
        double y = gamepad1.left_stick_y;
        double rx = -gamepad1.right_stick_x;

        drivetrain.drive(x, y, rx);
    }
}
