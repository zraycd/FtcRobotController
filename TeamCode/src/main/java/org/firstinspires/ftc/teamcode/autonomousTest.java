package org.firstinspires.ftc.teamcode;

import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.pedropathing.follower.Follower;
import com.pedropathing.paths.PathChain;

import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
import org.firstinspires.ftc.teamcode.pedroPathing.generatedPaths;

@Autonomous(name="autonomousTest", group = "testing")
public class autonomousTest extends LinearOpMode {
    hwManager hwManager;
//    drivetrain drivetrain;
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

//        drivetrain = new drivetrain(hwManager);
        supply = new supply(hwManager);
        shooter = new shooter(hwManager);
        intake = new intake(hwManager);

        hwManager.supply.setPosition(0.85);
//        drivetrain.resetYaw();

        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(new Pose(0.000, 0.000, 0));

        telemetry.addData("init", "complete");
        telemetry.update();

        waitForStart();

        if (opModeIsActive()) {
            pathState = 1;
            currentPath = generatedPaths.buildPath1(follower);
            follower.followPath(currentPath);

            while (opModeIsActive() && follower.isBusy()) {
                follower.update();

                telemetry.addData("State", pathState);
                telemetry.addData("X", follower.getPose().getX());
                telemetry.addData("Y", follower.getPose().getY());
                telemetry.addData("Heading", Math.toDegrees(follower.getPose().getHeading()));
                telemetry.addData("Is Busy", follower.isBusy());
                telemetry.update();
            }

            follower.breakFollowing();
            sleep(5000);

//            pathState = 2;
//            currentPath = generatedPaths.buildPath2(follower);
//            follower.followPath(currentPath);
//
//            while (opModeIsActive() && follower.isBusy()) {
//                follower.update();
//
//                telemetry.addData("State", "Path 2");
//                telemetry.addData("X", follower.getPose().getX());
//                telemetry.addData("Y", follower.getPose().getY());
//                telemetry.addData("Heading", Math.toDegrees(follower.getPose().getHeading()));
//                telemetry.addData("Is Busy", follower.isBusy());
//                telemetry.update();
//            }
//
//            follower.breakFollowing();
//            shooter.toggle(1850, 0.57);
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//            drivetrain.drive(0, 0, 0);
//            try {
//                Thread.sleep(300);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//            supply.send();
//            try {
//                Thread.sleep(620);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//            supply.update();
//            try {
//                Thread.sleep(800);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//            supply.send();
//            try {
//                Thread.sleep(620);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//            supply.update();
//            intake.toggle();
//            try {
//                Thread.sleep(800);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//            supply.send();
//            intake.toggle();
//            try {
//                Thread.sleep(620);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//            supply.update();
//            shooter.toggle(0, 0);
//            try {
//                Thread.sleep(30000);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
        }
    }
}