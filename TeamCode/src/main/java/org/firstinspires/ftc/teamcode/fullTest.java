package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name="fullTest", group = "testing")
public class fullTest extends OpMode {
    hwManager hwManager;
    drivetrain drivetrain;
    supply supply;
    shooter shooter;
    intake intake;

    boolean prevCross = false;
    boolean prevDUp = false;
    boolean prevDDown = false;
    boolean prevRBumper = false;
    boolean prevOptions = false;
    double currentPos = 0;

    @Override
    public void init() {
        hwManager = new hwManager();
        hwManager.initHardware(hardwareMap);

        drivetrain = new drivetrain(hwManager);
        supply = new supply(hwManager);
        shooter = new shooter(hwManager);
        intake = new intake(hwManager);

        telemetry.addData("init", "complete");
    }

    @Override
    public void loop() {
        hwManager.pinpoint.update();

        double slowMode = gamepad1.right_trigger;
        double superSlow = gamepad1.left_trigger;

        double x = -gamepad1.left_stick_x;
        double y = gamepad1.left_stick_y;
        double rx = -gamepad1.right_stick_x;
        boolean optionsPressed = gamepad1.options;

        if (slowMode > 0.05) {
            x *= 0.5;
            y *= 0.5;
            rx *= 0.5;
        }
        if (superSlow > 0.05) {
            x *= 0.25;
            y *= 0.25;
            rx *= 0.25;
        }

        drivetrain.drive(x, y, rx);

        boolean crossPressed = gamepad1.cross;
        boolean rBumperPressed = gamepad1.right_bumper;
        boolean close = gamepad1.dpad_up;
        boolean far = gamepad1.dpad_down;

        if (crossPressed && !prevCross) {
            supply.send();
        }
        if (close && !prevDUp) {
            shooter.shootClose();
        }
        if (far && !prevDDown) {
            shooter.shootFar();
        }
        if (rBumperPressed && !prevRBumper) {
            intake.toggle();
        }
        if (optionsPressed && !prevOptions) {
            drivetrain.resetYaw();
        }

        prevCross = crossPressed;
        prevDUp = close;
        prevDDown = far;
        prevRBumper = rBumperPressed;
        prevOptions = optionsPressed;

        supply.update();

        telemetry.addData("vel", hwManager.shooter.getVelocity());
        double heading = hwManager.pinpoint.getHeading();
        telemetry.addData("Heading (deg)", Math.toDegrees(heading));
        telemetry.addData("Position", "X: %.1f, Y: %.1f",
                hwManager.pinpoint.getPosX(), hwManager.pinpoint.getPosY());


        telemetry.update();
    }
}
