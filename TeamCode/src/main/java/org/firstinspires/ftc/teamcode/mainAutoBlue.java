package org.firstinspires.ftc.teamcode;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
import org.firstinspires.ftc.teamcode.pedroPathing.generatedPaths;

@Autonomous(name="mainAutoBlue", group = "testing")
public class mainAutoBlue extends LinearOpMode {
    hwManager hwManager;
    supply supply;
    shooter shooter;
    intake intake;
    Follower follower;
    PathChain currentPath;
    int pathState = 0;
    @Override
    public void runOpMode() {
        hwManager = new hwManager();
        hwManager.initHardware(hardwareMap);

        supply = new supply(hwManager);
        shooter = new shooter(hwManager);
        intake = new intake(hwManager);

        hwManager.supply.setPosition(0.85);

        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(new Pose(0, 0, 0));

        telemetry.addData("init", "complete");
        telemetry.update();

        waitForStart();

        if (opModeIsActive()) {
            shooter.shootClose();
            pathState = 1;
            currentPath = generatedPaths.bluePath1(follower);
            follower.followPath(currentPath);

            while (opModeIsActive() && follower.isBusy()) {
                follower.update();
            }

            sendAll();
            shooter.turnOff();

            pathState = 2;
            currentPath = generatedPaths.bluePath2(follower);
            follower.followPath(currentPath);
            while (opModeIsActive() && follower.isBusy()) {
                follower.update();
            }

            intake.toggle();

            pathState = 3;
            currentPath = generatedPaths.bluePath3(follower);
            follower.followPath(currentPath);
            while (opModeIsActive() && follower.isBusy()) {
                follower.update();
            }

            intake.toggle();
            shooter.shootClose();

            pathState = 4;
            currentPath = generatedPaths.bluePath4(follower);
            follower.followPath(currentPath);
            while (opModeIsActive() && follower.isBusy()) {
                follower.update();
            }

            sendAll();
            shooter.turnOff();

            pathState = 5;
            currentPath = generatedPaths.bluePath5(follower);
            follower.followPath(currentPath);
            while (opModeIsActive() && follower.isBusy()) {
                follower.update();
            }

            intake.toggle();

            pathState = 6;
            currentPath = generatedPaths.bluePath6(follower);
            follower.followPath(currentPath);
            while (opModeIsActive() && follower.isBusy()) {
                follower.update();
            }

            intake.toggle();
            shooter.shootClose();

            pathState = 7;
            currentPath = generatedPaths.bluePath7(follower);
            follower.followPath(currentPath);
            while (opModeIsActive() && follower.isBusy()) {
                follower.update();
            }

            sendAll();
            shooter.turnOff();

            pathState = 8;
            currentPath = generatedPaths.bluePath8(follower);
            follower.followPath(currentPath);
            while (opModeIsActive() && follower.isBusy()) {
                follower.update();
            }

            intake.toggle();

            pathState = 9;
            currentPath = generatedPaths.bluePath9(follower);
            follower.followPath(currentPath);
            while (opModeIsActive() && follower.isBusy()) {
                follower.update();
            }

            intake.toggle();
            try {
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public void sendAll() {
        supply.send();
        try {
            Thread.sleep(620);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        supply.update();
        intake.toggle();
        try {
            Thread.sleep(800);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        supply.send();
        try {
            Thread.sleep(620);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        supply.update();
        try {
            Thread.sleep(800);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        supply.send();
        intake.toggle();
        try {
            Thread.sleep(620);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        supply.update();
    }
}