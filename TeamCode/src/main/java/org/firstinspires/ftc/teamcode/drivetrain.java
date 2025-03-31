package org.firstinspires.ftc.teamcode;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class drivetrain {
    hwManager hwManager;
    public drivetrain(hwManager hwManager) {
        this.hwManager = hwManager;
    }
    public void resetYaw() {
        hwManager.imu.resetYaw();
    }
    public void drive(double x, double y, double rx) {
        double botHeading = hwManager.imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);

        double rotX = x * Math.cos(-botHeading) - y * Math.sin(-botHeading);
        double rotY = x * Math.sin(-botHeading) + y * Math.cos(-botHeading);

        rotX = rotX * 1.1;
        double denominator = Math.max(Math.abs(rotY) + Math.abs(rotX) + Math.abs(rx), 1);
        double frontLeftPower = (rotY + rotX + rx) / denominator;
        double backLeftPower = (rotY - rotX + rx) / denominator;
        double frontRightPower = (rotY - rotX - rx) / denominator;
        double backRightPower = (rotY + rotX - rx) / denominator;

        hwManager.frontLeft.setPower(frontLeftPower);
        hwManager.frontRight.setPower(frontRightPower);
        hwManager.backLeft.setPower(backLeftPower);
        hwManager.backRight.setPower(backRightPower);
    }

}
