package org.firstinspires.ftc.teamcode.pedroPathing;

import static org.firstinspires.ftc.teamcode.pedroPathing.Tuning.changes;
import static org.firstinspires.ftc.teamcode.pedroPathing.Tuning.drawOnlyCurrent;
import static org.firstinspires.ftc.teamcode.pedroPathing.Tuning.draw;
import static org.firstinspires.ftc.teamcode.pedroPathing.Tuning.follower;
import static org.firstinspires.ftc.teamcode.pedroPathing.Tuning.stopRobot;
import static org.firstinspires.ftc.teamcode.pedroPathing.Tuning.telemetryM;

import com.bylazar.configurables.PanelsConfigurables;
import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.configurables.annotations.IgnoreConfigurable;
import com.bylazar.field.FieldManager;
import com.bylazar.field.PanelsField;
import com.bylazar.field.Style;
import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.*;
import com.pedropathing.math.*;
import com.pedropathing.paths.*;
import com.pedropathing.telemetry.SelectableOpMode;
import com.pedropathing.util.*;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import java.util.ArrayList;
import java.util.List;

/**
 * This is the Tuning class. It contains a selection menu for various tuning OpModes.
 *
 * @author Baron Henderson - 20077 The Indubitables
 * @version 1.0, 6/26/2025
 */
@Configurable
@TeleOp(name = "Tuning", group = "Pedro Pathing")
public class Tuning extends SelectableOpMode {
    public static Follower follower;

    @IgnoreConfigurable
    static PoseHistory poseHistory;

    @IgnoreConfigurable
    static TelemetryManager telemetryM;

    @IgnoreConfigurable
    static ArrayList<String> changes = new ArrayList<>();

    public Tuning() {
        super("Select a Tuning OpMode", s -> {
            s.folder("Localization", l -> {
                l.add("Localization Test", LocalizationTest::new);
                l.add("Forward Tuner", ForwardTuner::new);
                l.add("Lateral Tuner", LateralTuner::new);
                l.add("Turn Tuner", TurnTuner::new);
            });
            s.folder("Automatic", a -> {
                a.add("Forward Velocity Tuner", ForwardVelocityTuner::new);
                a.add("Lateral Velocity Tuner", LateralVelocityTuner::new);
                a.add("Forward Zero Power Acceleration Tuner", ForwardZeroPowerAccelerationTuner::new);
                a.add("Lateral Zero Power Acceleration Tuner", LateralZeroPowerAccelerationTuner::new);
            });
            s.folder("Manual", p -> {
                p.add("Translational Tuner", TranslationalTuner::new);
                p.add("Heading Tuner", HeadingTuner::new);
                p.add("Drive Tuner", DriveTuner::new);
                p.add("Centripetal Tuner", CentripetalTuner::new);
            });
            s.folder("Tests", p -> {
                p.add("Line", Line::new);
                p.add("Triangle", Triangle::new);
                p.add("Circle", Circle::new);
            });
        });
    }

    @Override
    public void onSelect() {
        if (follower == null) {
            follower = Constants.createFollower(hardwareMap);
            PanelsConfigurables.INSTANCE.refreshClass(this);
        } else {
            follower = Constants.createFollower(hardwareMap);
        }

        follower.setStartingPose(new Pose());

        poseHistory = follower.getPoseHistory();

        telemetryM = PanelsTelemetry.INSTANCE.getTelemetry();

        Drawing.init();
    }

    @Override
    public void onLog(List<String> lines) {}

    public static void drawOnlyCurrent() {
        try {
            Drawing.drawRobot(follower.getPose());
            Drawing.sendPacket();
        } catch (Exception e) {
            throw new RuntimeException("Drawing failed " + e);
        }
    }

    public static void draw() {
        Drawing.drawDebug(follower);
    }

    /** This creates a full stop of the robot by setting the drive motors to run at 0 power. */
    public static void stopRobot() {
        follower.startTeleopDrive(true);
        follower.setTeleOpDrive(0,0,0,true);
    }
}

/**
 * This is the LocalizationTest OpMode. This is basically just a simple mecanum drive attached to a
 * PoseUpdater. The OpMode will print out the robot's pose to telemetry as well as draw the robot.
 * You should use this to check the robot's localization.
 *
 * @author Anyi Lin - 10158 Scott's Bots
 * @author Baron Henderson - 20077 The Indubitables
 * @version 1.0, 5/6/2024
 */
class LocalizationTest extends OpMode {
    @Override
    public void init() {}

    /** This initializes the PoseUpdater, the mecanum drive motors, and the Panels telemetry. */
    @Override
    public void init_loop() {
        Tuning.telemetryM.debug("This will print your robot's position to telemetry while "
                + "allowing robot control through a basic mecanum drive on gamepad 1.");
        Tuning.telemetryM.update(telemetry);
        Tuning.follower.update();
        Tuning.drawOnlyCurrent();
    }

    @Override
    public void start() {
        Tuning.follower.startTeleopDrive();
        Tuning.follower.update();
    }

    /**
     * This updates the robot's pose estimate, the simple mecanum drive, and updates the
     * Panels telemetry with the robot's position as well as draws the robot's position.
     */
    @Override
    public void loop() {
        Tuning.follower.setTeleOpDrive(-gamepad1.left_stick_y, -gamepad1.left_stick_x, -gamepad1.right_stick_x, true);
        Tuning.follower.update();

        Tuning.telemetryM.debug("x:" + Tuning.follower.getPose().getX());
        Tuning.telemetryM.debug("y:" + Tuning.follower.getPose().getY());
        Tuning.telemetryM.debug("heading:" + Tuning.follower.getPose().getHeading());
        Tuning.telemetryM.debug("total heading:" + Tuning.follower.getTotalHeading());
        Tuning.telemetryM.update(telemetry);

        Tuning.draw();
    }
}

/**
 * This is the ForwardTuner OpMode. This tracks the forward movement of the robot and displays the
 * necessary ticks to inches multiplier. This displayed multiplier is what's necessary to scale the
 * robot's current distance in ticks to the specified distance in inches. So, to use this, run the
 * tuner, then pull/push the robot to the specified distance using a ruler on the ground. When you're
 * at the end of the distance, record the ticks to inches multiplier. Feel free to run multiple trials
 * and average the results. Then, input the multiplier into the forward ticks to inches in your
 * localizer of choice.
 *
 * @author Anyi Lin - 10158 Scott's Bots
 * @author Baron Henderson - 20077 The Indubitables
 * @version 1.0, 5/6/2024
 */
class ForwardTuner extends OpMode {
    public static double DISTANCE = 48;

    @Override
    public void init() {
        Tuning.follower.update();
        Tuning.drawOnlyCurrent();
    }

    /** This initializes the PoseUpdater as well as the Panels telemetry. */
    @Override
    public void init_loop() {
        Tuning.telemetryM.debug("Pull your robot forward " + DISTANCE + " inches. Your forward ticks to inches will be shown on the telemetry.");
        Tuning.telemetryM.update(telemetry);
        Tuning.drawOnlyCurrent();
    }

    /**
     * This updates the robot's pose estimate, and updates the Panels telemetry with the
     * calculated multiplier and draws the robot.
     */
    @Override
    public void loop() {
        Tuning.follower.update();

        Tuning.telemetryM.debug("Distance Moved: " + Tuning.follower.getPose().getX());
        Tuning.telemetryM.debug("The multiplier will display what your forward ticks to inches should be to scale your current distance to " + DISTANCE + " inches.");
        Tuning.telemetryM.debug("Multiplier: " + (DISTANCE / (Tuning.follower.getPose().getX() / Tuning.follower.getPoseTracker().getLocalizer().getForwardMultiplier())));
        Tuning.telemetryM.update(telemetry);

        Tuning.draw();
    }
}

/**
 * This is the LateralTuner OpMode. This tracks the strafe movement of the robot and displays the
 * necessary ticks to inches multiplier. This displayed multiplier is what's necessary to scale the
 * robot's current distance in ticks to the specified distance in inches. So, to use this, run the
 * tuner, then pull/push the robot to the specified distance using a ruler on the ground. When you're
 * at the end of the distance, record the ticks to inches multiplier. Feel free to run multiple trials
 * and average the results. Then, input the multiplier into the strafe ticks to inches in your
 * localizer of choice.
 *
 * @author Anyi Lin - 10158 Scott's Bots
 * @author Baron Henderson - 20077 The Indubitables
 * @version 2.0, 6/26/2025
 */
class LateralTuner extends OpMode {
    public static double DISTANCE = 48;

    @Override
    public void init() {
        Tuning.follower.update();
        Tuning.drawOnlyCurrent();
    }

    /** This initializes the PoseUpdater as well as the Panels telemetry. */
    @Override
    public void init_loop() {
        Tuning.telemetryM.debug("Pull your robot to the left " + DISTANCE + " inches. Your strafe ticks to inches will be shown on the telemetry.");
        Tuning.telemetryM.update(telemetry);
        Tuning.drawOnlyCurrent();
    }

    /**
     * This updates the robot's pose estimate, and updates the Panels telemetry with the
     * calculated multiplier and draws the robot.
     */
    @Override
    public void loop() {
        Tuning.follower.update();

        Tuning.telemetryM.debug("Distance Moved: " + Tuning.follower.getPose().getY());
        Tuning.telemetryM.debug("The multiplier will display what your strafe ticks to inches should be to scale your current distance to " + DISTANCE + " inches.");
        Tuning.telemetryM.debug("Multiplier: " + (DISTANCE / (Tuning.follower.getPose().getY() / Tuning.follower.getPoseTracker().getLocalizer().getLateralMultiplier())));
        Tuning.telemetryM.update(telemetry);

        Tuning.draw();
    }
}

/**
 * This is the TurnTuner OpMode. This tracks the turning movement of the robot and displays the
 * necessary ticks to inches multiplier. This displayed multiplier is what's necessary to scale the
 * robot's current angle in ticks to the specified angle in radians. So, to use this, run the
 * tuner, then pull/push the robot to the specified angle using a protractor or lines on the ground.
 * When you're at the end of the angle, record the ticks to inches multiplier. Feel free to run
 * multiple trials and average the results. Then, input the multiplier into the turning ticks to
 * radians in your localizer of choice.
 *
 * @author Anyi Lin - 10158 Scott's Bots
 * @author Baron Henderson - 20077 The Indubitables
 * @version 1.0, 5/6/2024
 */
class TurnTuner extends OpMode {
    public static double ANGLE = 2 * Math.PI;

    @Override
    public void init() {
        Tuning.follower.update();
        Tuning.drawOnlyCurrent();
    }

    /** This initializes the PoseUpdater as well as the Panels telemetry. */
    @Override
    public void init_loop() {
        Tuning.telemetryM.debug("Turn your robot " + ANGLE + " radians. Your turn ticks to inches will be shown on the telemetry.");
        Tuning.telemetryM.update(telemetry);

        Tuning.drawOnlyCurrent();
    }

    /**
     * This updates the robot's pose estimate, and updates the Panels telemetry with the
     * calculated multiplier and draws the robot.
     */
    @Override
    public void loop() {
        Tuning.follower.update();

        Tuning.telemetryM.debug("Total Angle: " + Tuning.follower.getTotalHeading());
        Tuning.telemetryM.debug("The multiplier will display what your turn ticks to inches should be to scale your current angle to " + ANGLE + " radians.");
        Tuning.telemetryM.debug("Multiplier: " + (ANGLE / (Tuning.follower.getTotalHeading() / Tuning.follower.getPoseTracker().getLocalizer().getTurningMultiplier())));
        Tuning.telemetryM.update(telemetry);

        Tuning.draw();
    }
}

/**
 * This is the ForwardVelocityTuner autonomous follower OpMode. This runs the robot forwards at max
 * power until it reaches some specified distance. It records the most recent velocities, and on
 * reaching the end of the distance, it averages them and prints out the velocity obtained. It is
 * recommended to run this multiple times on a full battery to get the best results. What this does
 * is, when paired with StrafeVelocityTuner, allows Constants to create a Vector that
 * empirically represents the direction your mecanum wheels actually prefer to go in, allowing for
 * more accurate following.
 *
 * @author Anyi Lin - 10158 Scott's Bots
 * @author Aaron Yang - 10158 Scott's Bots
 * @author Harrison Womack - 10158 Scott's Bots
 * @author Baron Henderson - 20077 The Indubitables
 * @version 1.0, 3/13/2024
 */
class ForwardVelocityTuner extends OpMode {
    private final ArrayList<Double> velocities = new ArrayList<>();
    public static double DISTANCE = 48;
    public static double RECORD_NUMBER = 10;

    private boolean end;

    @Override
    public void init() {}

    /** This initializes the drive motors as well as the cache of velocities and the Panels telemetry. */
    @Override
    public void init_loop() {
        Tuning.telemetryM.debug("The robot will run at 1 power until it reaches " + DISTANCE + " inches forward.");
        Tuning.telemetryM.debug("Make sure you have enough room, since the robot has inertia after cutting power.");
        Tuning.telemetryM.debug("After running the distance, the robot will cut power from the drivetrain and display the forward velocity.");
        Tuning.telemetryM.debug("Press B on game pad 1 to stop.");
        Tuning.telemetryM.debug("pose", Tuning.follower.getPose());
        Tuning.telemetryM.update(telemetry);

        Tuning.follower.update();
        Tuning.drawOnlyCurrent();
    }

    /** This starts the OpMode by setting the drive motors to run forward at full power. */
    @Override
    public void start() {
        for (int i = 0; i < RECORD_NUMBER; i++) {
            velocities.add(0.0);
        }
        Tuning.follower.startTeleopDrive(true);
        Tuning.follower.update();
        end = false;
    }

    /**
     * This runs the OpMode. At any point during the running of the OpMode, pressing B on
     * game pad 1 will stop the OpMode. This continuously records the RECORD_NUMBER most recent
     * velocities, and when the robot has run forward enough, these last velocities recorded are
     * averaged and printed.
     */
    @Override
    public void loop() {
        if (gamepad1.bWasPressed()) {
            Tuning.stopRobot();
            requestOpModeStop();
        }

        Tuning.follower.update();
        Tuning.draw();


        if (!end) {
            if (Math.abs(Tuning.follower.getPose().getX()) > DISTANCE) {
                end = true;
                Tuning.stopRobot();
            } else {
                Tuning.follower.setTeleOpDrive(1,0,0,true);
                //double currentVelocity = Math.abs(follower.getVelocity().getXComponent());
                double currentVelocity = Math.abs(Tuning.follower.poseTracker.getLocalizer().getVelocity().getX());
                velocities.add(currentVelocity);
                velocities.remove(0);
            }
        } else {
            Tuning.stopRobot();
            double average = 0;
            for (double velocity : velocities) {
                average += velocity;
            }
            average /= velocities.size();
            Tuning.telemetryM.debug("Forward Velocity: " + average);
            Tuning.telemetryM.debug("\n");
            Tuning.telemetryM.debug("Press A to set the Forward Velocity temporarily (while robot remains on).");

            for (int i = 0; i < velocities.size(); i++) {
                telemetry.addData(String.valueOf(i), velocities.get(i));
            }

            Tuning.telemetryM.update(telemetry);
            telemetry.update();

            if (gamepad1.aWasPressed()) {
                Tuning.follower.setXVelocity(average);
                String message = "XMovement: " + average;
                Tuning.changes.add(message);
            }
        }
    }
}

/**
 * This is the LateralVelocityTuner autonomous follower OpMode. This runs the robot left at max
 * power until it reaches some specified distance. It records the most recent velocities, and on
 * reaching the end of the distance, it averages them and prints out the velocity obtained. It is
 * recommended to run this multiple times on a full battery to get the best results. What this does
 * is, when paired with ForwardVelocityTuner, allows Constants to create a Vector that
 * empirically represents the direction your mecanum wheels actually prefer to go in, allowing for
 * more accurate following.
 *
 * @author Anyi Lin - 10158 Scott's Bots
 * @author Aaron Yang - 10158 Scott's Bots
 * @author Harrison Womack - 10158 Scott's Bots
 * @author Baron Henderson - 20077 The Indubitables
 * @version 1.0, 3/13/2024
 */
class LateralVelocityTuner extends OpMode {
    private final ArrayList<Double> velocities = new ArrayList<>();

    public static double DISTANCE = 48;
    public static double RECORD_NUMBER = 10;

    private boolean end;

    @Override
    public void init() {}

    /**
     * This initializes the drive motors as well as the cache of velocities and the Panels
     * telemetryM.
     */
    @Override
    public void init_loop() {
        Tuning.telemetryM.debug("The robot will run at 1 power until it reaches " + DISTANCE + " inches to the left.");
        Tuning.telemetryM.debug("Make sure you have enough room, since the robot has inertia after cutting power.");
        Tuning.telemetryM.debug("After running the distance, the robot will cut power from the drivetrain and display the strafe velocity.");
        Tuning.telemetryM.debug("Press B on Gamepad 1 to stop.");
        Tuning.telemetryM.update(telemetry);

        Tuning.follower.update();
        Tuning.drawOnlyCurrent();
    }

    /** This starts the OpMode by setting the drive motors to run left at full power. */
    @Override
    public void start() {
        for (int i = 0; i < RECORD_NUMBER; i++) {
            velocities.add(0.0);
        }
        Tuning.follower.startTeleopDrive(true);
        Tuning.follower.update();
    }

    /**
     * This runs the OpMode. At any point during the running of the OpMode, pressing B on
     * game pad1 will stop the OpMode. This continuously records the RECORD_NUMBER most recent
     * velocities, and when the robot has run sideways enough, these last velocities recorded are
     * averaged and printed.
     */
    @Override
    public void loop() {
        if (gamepad1.bWasPressed()) {
            Tuning.stopRobot();
            requestOpModeStop();
        }

        Tuning.follower.update();
        Tuning.draw();

        if (!end) {
            if (Math.abs(Tuning.follower.getPose().getY()) > DISTANCE) {
                end = true;
                Tuning.stopRobot();
            } else {
                Tuning.follower.setTeleOpDrive(0,1,0,true);
                double currentVelocity = Math.abs(Tuning.follower.getVelocity().dot(new Vector(1, Math.PI / 2)));
                velocities.add(currentVelocity);
                velocities.remove(0);
            }
        } else {
            Tuning.stopRobot();
            double average = 0;
            for (double velocity : velocities) {
                average += velocity;
            }
            average /= velocities.size();

            Tuning.telemetryM.debug("Strafe Velocity: " + average);
            Tuning.telemetryM.debug("\n");
            Tuning.telemetryM.debug("Press A to set the Lateral Velocity temporarily (while robot remains on).");
            Tuning.telemetryM.update(telemetry);

            if (gamepad1.aWasPressed()) {
                Tuning.follower.setYVelocity(average);
                String message = "YMovement: " + average;
                Tuning.changes.add(message);
            }
        }
    }
}

/**
 * This is the ForwardZeroPowerAccelerationTuner autonomous follower OpMode. This runs the robot
 * forward until a specified velocity is achieved. Then, the robot cuts power to the motors, setting
 * them to zero power. The deceleration, or negative acceleration, is then measured until the robot
 * stops. The accelerations across the entire time the robot is slowing down is then averaged and
 * that number is then printed. This is used to determine how the robot will decelerate in the
 * forward direction when power is cut, making the estimations used in the calculations for the
 * drive Vector more accurate and giving better braking at the end of Paths.
 *
 * @author Anyi Lin - 10158 Scott's Bots
 * @author Baron Henderson - 20077 The Indubitables
 * @author Aaron Yang - 10158 Scott's Bots
 * @author Harrison Womack - 10158 Scott's Bots
 * @version 1.0, 3/13/2024
 */
class ForwardZeroPowerAccelerationTuner extends OpMode {
    private final ArrayList<Double> accelerations = new ArrayList<>();
    public static double VELOCITY = 30;

    private double previousVelocity;
    private long previousTimeNano;

    private boolean stopping;
    private boolean end;

    @Override
    public void init() {}

    /** This initializes the drive motors as well as the Panels telemetryM. */
    @Override
    public void init_loop() {
        Tuning.telemetryM.debug("The robot will run forward until it reaches " + VELOCITY + " inches per second.");
        Tuning.telemetryM.debug("Then, it will cut power from the drivetrain and roll to a stop.");
        Tuning.telemetryM.debug("Make sure you have enough room.");
        Tuning.telemetryM.debug("After stopping, the forward zero power acceleration (natural deceleration) will be displayed.");
        Tuning.telemetryM.debug("Press B on Gamepad 1 to stop.");
        Tuning.telemetryM.update(telemetry);
        Tuning.follower.update();
        Tuning.drawOnlyCurrent();
    }

    /** This starts the OpMode by setting the drive motors to run forward at full power. */
    @Override
    public void start() {
        Tuning.follower.startTeleopDrive(false);
        Tuning.follower.update();
        Tuning.follower.setTeleOpDrive(1,0,0,true);
    }

    /**
     * This runs the OpMode. At any point during the running of the OpMode, pressing B on
     * game pad 1 will stop the OpMode. When the robot hits the specified velocity, the robot will
     * record its deceleration / negative acceleration until it stops. Then, it will average all the
     * recorded deceleration / negative acceleration and print that value.
     */
    @Override
    public void loop() {
        if (gamepad1.bWasPressed()) {
            Tuning.stopRobot();
            requestOpModeStop();
        }

        Tuning.follower.update();
        Tuning.draw();

        Vector heading = new Vector(1.0, Tuning.follower.getPose().getHeading());
        if (!end) {
            if (!stopping) {
                if (Tuning.follower.getVelocity().dot(heading) > VELOCITY) {
                    previousVelocity = Tuning.follower.getVelocity().dot(heading);
                    previousTimeNano = System.nanoTime();
                    stopping = true;
                    Tuning.follower.setTeleOpDrive(0,0,0,true);
                }
            } else {
                double currentVelocity = Tuning.follower.getVelocity().dot(heading);
                accelerations.add((currentVelocity - previousVelocity) / ((System.nanoTime() - previousTimeNano) / Math.pow(10.0, 9)));
                previousVelocity = currentVelocity;
                previousTimeNano = System.nanoTime();
                if (currentVelocity < Tuning.follower.getConstraints().getVelocityConstraint()) {
                    end = true;
                }
            }
        } else {
            double average = 0;
            for (double acceleration : accelerations) {
                average += acceleration;
            }
            average /= accelerations.size();

            Tuning.telemetryM.debug("Forward Zero Power Acceleration (Deceleration): " + average);
            Tuning.telemetryM.debug("\n");
            Tuning.telemetryM.debug("Press A to set the Forward Zero Power Acceleration temporarily (while robot remains on).");
            Tuning.telemetryM.update(telemetry);

            if (gamepad1.aWasPressed()) {
                Tuning.follower.getConstants().setForwardZeroPowerAcceleration(average);
                String message = "Forward Zero Power Acceleration: " + average;
                Tuning.changes.add(message);
            }
        }
    }
}

/**
 * This is the LateralZeroPowerAccelerationTuner autonomous follower OpMode. This runs the robot
 * to the left until a specified velocity is achieved. Then, the robot cuts power to the motors, setting
 * them to zero power. The deceleration, or negative acceleration, is then measured until the robot
 * stops. The accelerations across the entire time the robot is slowing down is then averaged and
 * that number is then printed. This is used to determine how the robot will decelerate in the
 * forward direction when power is cut, making the estimations used in the calculations for the
 * drive Vector more accurate and giving better braking at the end of Paths.
 *
 * @author Anyi Lin - 10158 Scott's Bots
 * @author Aaron Yang - 10158 Scott's Bots
 * @author Harrison Womack - 10158 Scott's Bots
 * @author Baron Henderson - 20077 The Indubitables
 * @version 1.0, 3/13/2024
 */
class LateralZeroPowerAccelerationTuner extends OpMode {
    private final ArrayList<Double> accelerations = new ArrayList<>();
    public static double VELOCITY = 30;
    private double previousVelocity;
    private long previousTimeNano;
    private boolean stopping;
    private boolean end;

    @Override
    public void init() {}

    /** This initializes the drive motors as well as the Panels telemetry. */
    @Override
    public void init_loop() {
        Tuning.telemetryM.debug("The robot will run to the left until it reaches " + VELOCITY + " inches per second.");
        Tuning.telemetryM.debug("Then, it will cut power from the drivetrain and roll to a stop.");
        Tuning.telemetryM.debug("Make sure you have enough room.");
        Tuning.telemetryM.debug("After stopping, the lateral zero power acceleration (natural deceleration) will be displayed.");
        Tuning.telemetryM.debug("Press B on game pad 1 to stop.");
        Tuning.telemetryM.update(telemetry);
        Tuning.follower.update();
        Tuning.drawOnlyCurrent();
    }

    /** This starts the OpMode by setting the drive motors to run forward at full power. */
    @Override
    public void start() {
        Tuning.follower.startTeleopDrive(false);
        Tuning.follower.update();
        Tuning.follower.setTeleOpDrive(0,1,0,true);
    }

    /**
     * This runs the OpMode. At any point during the running of the OpMode, pressing B on
     * game pad 1 will stop the OpMode. When the robot hits the specified velocity, the robot will
     * record its deceleration / negative acceleration until it stops. Then, it will average all the
     * recorded deceleration / negative acceleration and print that value.
     */
    @Override
    public void loop() {
        if (gamepad1.bWasPressed()) {
            Tuning.stopRobot();
            requestOpModeStop();
        }

        Tuning.follower.update();
        Tuning.draw();

        Vector heading = new Vector(1.0, Tuning.follower.getPose().getHeading() - Math.PI / 2);
        if (!end) {
            if (!stopping) {
                if (Math.abs(Tuning.follower.getVelocity().dot(heading)) > VELOCITY) {
                    previousVelocity = Math.abs(Tuning.follower.getVelocity().dot(heading));
                    previousTimeNano = System.nanoTime();
                    stopping = true;
                    Tuning.follower.setTeleOpDrive(0,0,0,true);
                }
            } else {
                double currentVelocity = Math.abs(Tuning.follower.getVelocity().dot(heading));
                accelerations.add((currentVelocity - previousVelocity) / ((System.nanoTime() - previousTimeNano) / Math.pow(10.0, 9)));
                previousVelocity = currentVelocity;
                previousTimeNano = System.nanoTime();
                if (currentVelocity < Tuning.follower.getConstraints().getVelocityConstraint()) {
                    end = true;
                }
            }
        } else {
            double average = 0;
            for (double acceleration : accelerations) {
                average += acceleration;
            }
            average /= accelerations.size();

            Tuning.telemetryM.debug("Lateral Zero Power Acceleration (Deceleration): " + average);
            Tuning.telemetryM.debug("\n");
            Tuning.telemetryM.debug("Press A to set the Lateral Zero Power Acceleration temporarily (while robot remains on).");
            Tuning.telemetryM.update(telemetry);

            if (gamepad1.aWasPressed()) {
                Tuning.follower.getConstants().setLateralZeroPowerAcceleration(average);
                String message = "Lateral Zero Power Acceleration: " + average;
                Tuning.changes.add(message);
            }
        }
    }
}

/**
 * This is the Translational PIDF Tuner OpMode. It will keep the robot in place.
 * The user should push the robot laterally to test the PIDF and adjust the PIDF values accordingly.
 *
 * @author Baron Henderson - 20077 The Indubitables
 * @author Anyi Lin - 10158 Scott's Bots
 * @author Aaron Yang - 10158 Scott's Bots
 * @author Harrison Womack - 10158 Scott's Bots
 * @version 1.0, 3/12/2024
 */
class TranslationalTuner extends OpMode {
    public static double DISTANCE = 40;
    private boolean forward = true;

    private Path forwards;
    private Path backwards;

    @Override
    public void init() {}

    /** This initializes the Follower and creates the forward and backward Paths. */
    @Override
    public void init_loop() {
        Tuning.telemetryM.debug("This will activate the translational PIDF(s)");
        Tuning.telemetryM.debug("The robot will try to stay in place while you push it laterally.");
        Tuning.telemetryM.debug("You can adjust the PIDF values to tune the robot's translational PIDF(s).");
        Tuning.telemetryM.update(telemetry);
        Tuning.follower.update();
        Tuning.drawOnlyCurrent();
    }

    @Override
    public void start() {
        Tuning.follower.deactivateAllPIDFs();
        Tuning.follower.activateTranslational();
        forwards = new Path(new BezierLine(new Pose(0,0), new Pose(DISTANCE,0)));
        forwards.setConstantHeadingInterpolation(0);
        backwards = new Path(new BezierLine(new Pose(DISTANCE,0), new Pose(0,0)));
        backwards.setConstantHeadingInterpolation(0);
        Tuning.follower.followPath(forwards);
    }

    /** This runs the OpMode, updating the Follower as well as printing out the debug statements to the Telemetry */
    @Override
    public void loop() {
        Tuning.follower.update();
        Tuning.draw();

        if (!Tuning.follower.isBusy()) {
            if (forward) {
                forward = false;
                Tuning.follower.followPath(backwards);
            } else {
                forward = true;
                Tuning.follower.followPath(forwards);
            }
        }

        Tuning.telemetryM.debug("Push the robot laterally to test the Translational PIDF(s).");
        Tuning.telemetryM.update(telemetry);
    }
}

/**
 * This is the Heading PIDF Tuner OpMode. It will keep the robot in place.
 * The user should try to turn the robot to test the PIDF and adjust the PIDF values accordingly.
 * It will try to keep the robot at a constant heading while the user tries to turn it.
 *
 * @author Baron Henderson - 20077 The Indubitables
 * @author Anyi Lin - 10158 Scott's Bots
 * @author Aaron Yang - 10158 Scott's Bots
 * @author Harrison Womack - 10158 Scott's Bots
 * @version 1.0, 3/12/2024
 */
class HeadingTuner extends OpMode {
    public static double DISTANCE = 40;
    private boolean forward = true;

    private Path forwards;
    private Path backwards;

    @Override
    public void init() {}

    /**
     * This initializes the Follower and creates the forward and backward Paths. Additionally, this
     * initializes the Panels telemetry.
     */
    @Override
    public void init_loop() {
        Tuning.telemetryM.debug("This will activate the heading PIDF(s).");
        Tuning.telemetryM.debug("The robot will try to stay at a constant heading while you try to turn it.");
        Tuning.telemetryM.debug("You can adjust the PIDF values to tune the robot's heading PIDF(s).");
        Tuning.telemetryM.update(telemetry);
        Tuning.follower.update();
        Tuning.drawOnlyCurrent();
    }

    @Override
    public void start() {
        Tuning.follower.deactivateAllPIDFs();
        Tuning.follower.activateHeading();
        forwards = new Path(new BezierLine(new Pose(0,0), new Pose(DISTANCE,0)));
        forwards.setConstantHeadingInterpolation(0);
        backwards = new Path(new BezierLine(new Pose(DISTANCE,0), new Pose(0,0)));
        backwards.setConstantHeadingInterpolation(0);
        Tuning.follower.followPath(forwards);
    }

    /**
     * This runs the OpMode, updating the Follower as well as printing out the debug statements to
     * the Telemetry, as well as the Panels.
     */
    @Override
    public void loop() {
        Tuning.follower.update();
        Tuning.draw();

        if (!Tuning.follower.isBusy()) {
            if (forward) {
                forward = false;
                Tuning.follower.followPath(backwards);
            } else {
                forward = true;
                Tuning.follower.followPath(forwards);
            }
        }

        Tuning.telemetryM.debug("Turn the robot manually to test the Heading PIDF(s).");
        Tuning.telemetryM.update(telemetry);
    }
}

/**
 * This is the Drive PIDF Tuner OpMode. It will run the robot in a straight line going forward and back.
 *
 * @author Baron Henderson - 20077 The Indubitables
 * @author Anyi Lin - 10158 Scott's Bots
 * @author Aaron Yang - 10158 Scott's Bots
 * @author Harrison Womack - 10158 Scott's Bots
 * @version 1.0, 3/12/2024
 */
class DriveTuner extends OpMode {
    public static double DISTANCE = 40;
    private boolean forward = true;

    private PathChain forwards;
    private PathChain backwards;

    @Override
    public void init() {}

    /**
     * This initializes the Follower and creates the forward and backward Paths. Additionally, this
     * initializes the Panels telemetry.
     */
    @Override
    public void init_loop() {
        Tuning.telemetryM.debug("This will run the robot in a straight line going " + DISTANCE + "inches forward.");
        Tuning.telemetryM.debug("The robot will go forward and backward continuously along the path.");
        Tuning.telemetryM.debug("Make sure you have enough room.");
        Tuning.telemetryM.update(telemetry);
        Tuning.follower.update();
        Tuning.drawOnlyCurrent();
    }

    @Override
    public void start() {
        Tuning.follower.deactivateAllPIDFs();
        Tuning.follower.activateDrive();
        
        forwards = Tuning.follower.pathBuilder()
                .setGlobalDeceleration()
                .addPath(new BezierLine(new Pose(0,0), new Pose(DISTANCE,0)))
                .setConstantHeadingInterpolation(0)
                .build();

        backwards = Tuning.follower.pathBuilder()
                .setGlobalDeceleration()
                .addPath(new BezierLine(new Pose(DISTANCE,0), new Pose(0,0)))
                .setConstantHeadingInterpolation(0)
                .build();

        Tuning.follower.followPath(forwards);
    }

    /**
     * This runs the OpMode, updating the Follower as well as printing out the debug statements to
     * the Telemetry, as well as the Panels.
     */
    @Override
    public void loop() {
        Tuning.follower.update();
        Tuning.draw();

        if (!Tuning.follower.isBusy()) {
            if (forward) {
                forward = false;
                Tuning.follower.followPath(backwards);
            } else {
                forward = true;
                Tuning.follower.followPath(forwards);
            }
        }

        Tuning.telemetryM.debug("Driving forward?: " + forward);
        Tuning.telemetryM.update(telemetry);
    }
}

/**
 * This is the Line Test Tuner OpMode. It will drive the robot forward and back
 * The user should push the robot laterally and angular to test out the drive, heading, and translational PIDFs.
 *
 * @author Baron Henderson - 20077 The Indubitables
 * @author Anyi Lin - 10158 Scott's Bots
 * @author Aaron Yang - 10158 Scott's Bots
 * @author Harrison Womack - 10158 Scott's Bots
 * @version 1.0, 3/12/2024
 */
class Line extends OpMode {
    public static double DISTANCE = 50;
    private boolean forward = true;

    private Path forwards;
    private Path backwards;

    @Override
    public void init() {}

    /** This initializes the Follower and creates the forward and backward Paths. */
    @Override
    public void init_loop() {
        Tuning.telemetryM.debug("This will activate all the PIDF(s)");
        Tuning.telemetryM.debug("The robot will go forward and backward continuously along the path while correcting.");
        Tuning.telemetryM.debug("You can adjust the PIDF values to tune the robot's drive PIDF(s).");
        Tuning.telemetryM.update(telemetry);
        Tuning.follower.update();
        Tuning.drawOnlyCurrent();
    }

    @Override
    public void start() {
        Tuning.follower.setStartingPose(new Pose(0, 0, 0));
        Tuning.follower.activateAllPIDFs();
        forwards = new Path(new BezierLine(new Pose(0,0), new Pose(DISTANCE,0)));
        forwards.setConstantHeadingInterpolation(0);
        backwards = new Path(new BezierLine(new Pose(DISTANCE,0), new Pose(0,0)));
        backwards.setConstantHeadingInterpolation(0);
        Tuning.follower.followPath(forwards);
    }

    /** This runs the OpMode, updating the Follower as well as printing out the debug statements to the Telemetry */
    @Override
    public void loop() {
        Tuning.follower.update();
        Tuning.draw();

        if (!Tuning.follower.isBusy()) {
            if (forward) {
                forward = false;
                Tuning.follower.followPath(backwards);
            } else {
                forward = true;
                Tuning.follower.followPath(forwards);
            }
        }
        Pose currentPose = Tuning.follower.getPose();
        telemetry.addData("X Position", currentPose.getX());
        telemetry.addData("Y Position", currentPose.getY());
        telemetry.addData("Is Busy?", Tuning.follower.isBusy());

        Tuning.telemetryM.debug("Driving Forward?: " + forward);
        Tuning.telemetryM.update(telemetry);
    }
}

/**
 * This is the Centripetal Tuner OpMode. It runs the robot in a specified distance
 * forward and to the left. On reaching the end of the forward Path, the robot runs the backward
 * Path the same distance back to the start. Rinse and repeat! This is good for testing a variety
 * of Vectors, like the drive Vector, the translational Vector, the heading Vector, and the
 * centripetal Vector.
 *
 * @author Baron Henderson - 20077 The Indubitables
 * @author Anyi Lin - 10158 Scott's Bots
 * @author Aaron Yang - 10158 Scott's Bots
 * @author Harrison Womack - 10158 Scott's Bots
 * @version 1.0, 3/13/2024
 */
class CentripetalTuner extends OpMode {
    public static double DISTANCE = 20;
    private boolean forward = true;

    private Path forwards;
    private Path backwards;

    @Override
    public void init() {}

    /**
     * This initializes the Follower and creates the forward and backward Paths.
     * Additionally, this initializes the Panels telemetry.
     */
    @Override
    public void init_loop() {
        Tuning.telemetryM.debug("This will run the robot in a curve going " + DISTANCE + " inches to the left and the same number of inches forward.");
        Tuning.telemetryM.debug("The robot will go continuously along the path.");
        Tuning.telemetryM.debug("Make sure you have enough room.");
        Tuning.telemetryM.update(telemetry);
        Tuning.follower.update();
        Tuning.drawOnlyCurrent();
    }

    @Override
    public void start() {
        Tuning.follower.activateAllPIDFs();
        forwards = new Path(new BezierCurve(new Pose(), new Pose(Math.abs(DISTANCE),0), new Pose(Math.abs(DISTANCE),DISTANCE)));
        backwards = new Path(new BezierCurve(new Pose(Math.abs(DISTANCE),DISTANCE), new Pose(Math.abs(DISTANCE),0), new Pose(0,0)));

        backwards.setTangentHeadingInterpolation();
        backwards.reverseHeadingInterpolation();

        Tuning.follower.followPath(forwards);
    }

    /**
     * This runs the OpMode, updating the Follower as well as printing out the debug statements to
     * the Telemetry, as well as the Panels.
     */
    @Override
    public void loop() {
        Tuning.follower.update();
        Tuning.draw();
        if (!Tuning.follower.isBusy()) {
            if (forward) {
                forward = false;
                Tuning.follower.followPath(backwards);
            } else {
                forward = true;
                Tuning.follower.followPath(forwards);
            }
        }

        Tuning.telemetryM.debug("Driving away from the origin along the curve?: " + forward);
        Tuning.telemetryM.update(telemetry);
    }
}

/**
 * This is the Triangle autonomous OpMode.
 * It runs the robot in a triangle, with the starting point being the bottom-middle point.
 *
 * @author Baron Henderson - 20077 The Indubitables
 * @author Samarth Mahapatra - 1002 CircuitRunners Robotics Surge
 * @version 1.0, 12/30/2024
 */
class Triangle extends OpMode {

    private final Pose startPose = new Pose(0, 0, Math.toRadians(0));
    private final Pose interPose = new Pose(24, -24, Math.toRadians(90));
    private final Pose endPose = new Pose(24, 24, Math.toRadians(45));

    private PathChain triangle;

    /**
     * This runs the OpMode, updating the Follower as well as printing out the debug statements to
     * the Telemetry, as well as the Panels.
     */
    @Override
    public void loop() {
        Tuning.follower.update();
        Tuning.draw();

        if (Tuning.follower.atParametricEnd()) {
            Tuning.follower.followPath(triangle, true);
        }
    }

    @Override
    public void init() {}

    @Override
    public void init_loop() {
        Tuning.telemetryM.debug("This will run in a roughly triangular shape, starting on the bottom-middle point.");
        Tuning.telemetryM.debug("So, make sure you have enough space to the left, front, and right to run the OpMode.");
        Tuning.telemetryM.update(telemetry);
        Tuning.follower.update();
        Tuning.drawOnlyCurrent();
    }

    /** Creates the PathChain for the "triangle".*/
    @Override
    public void start() {
        Tuning.follower.setStartingPose(startPose);

        triangle = Tuning.follower.pathBuilder()
                .addPath(new BezierLine(startPose, interPose))
                .setLinearHeadingInterpolation(startPose.getHeading(), interPose.getHeading())
                .addPath(new BezierLine(interPose, endPose))
                .setLinearHeadingInterpolation(interPose.getHeading(), endPose.getHeading())
                .addPath(new BezierLine(endPose, startPose))
                .setLinearHeadingInterpolation(endPose.getHeading(), startPose.getHeading())
                .build();

        Tuning.follower.followPath(triangle);
    }
}

/**
 * This is the Circle autonomous OpMode. It runs the robot in a PathChain that's actually not quite
 * a circle, but some Bezier curves that have control points set essentially in a square. However,
 * it turns enough to tune your centripetal force correction and some of your heading. Some lag in
 * heading is to be expected.
 *
 * @author Anyi Lin - 10158 Scott's Bots
 * @author Aaron Yang - 10158 Scott's Bots
 * @author Harrison Womack - 10158 Scott's Bots
 * @version 1.0, 3/12/2024
 */
class Circle extends OpMode {
    public static double RADIUS = 10;
    private PathChain circle;

    public void start() {
        circle = Tuning.follower.pathBuilder()
                .addPath(new BezierCurve(new Pose(0, 0), new Pose(RADIUS, 0), new Pose(RADIUS, RADIUS)))
                .setHeadingInterpolation(HeadingInterpolator.facingPoint(0, RADIUS))
                .addPath(new BezierCurve(new Pose(RADIUS, RADIUS), new Pose(RADIUS, 2 * RADIUS), new Pose(0, 2 * RADIUS)))
                .setHeadingInterpolation(HeadingInterpolator.facingPoint(0, RADIUS))
                .addPath(new BezierCurve(new Pose(0, 2 * RADIUS), new Pose(-RADIUS, 2 * RADIUS), new Pose(-RADIUS, RADIUS)))
                .setHeadingInterpolation(HeadingInterpolator.facingPoint(0, RADIUS))
                .addPath(new BezierCurve(new Pose(-RADIUS, RADIUS), new Pose(-RADIUS, 0), new Pose(0, 0)))
                .setHeadingInterpolation(HeadingInterpolator.facingPoint(0, RADIUS))
                .build();
        Tuning.follower.followPath(circle);
    }

    @Override
    public void init_loop() {
        Tuning.telemetryM.debug("This will run in a roughly circular shape of radius " + RADIUS + ", starting on the right-most edge. ");
        Tuning.telemetryM.debug("So, make sure you have enough space to the left, front, and back to run the OpMode.");
        Tuning.telemetryM.debug("It will also continuously face the center of the circle to test your heading and centripetal correction.");
        Tuning.telemetryM.update(telemetry);
        Tuning.follower.update();
        Tuning.drawOnlyCurrent();
    }

    @Override
    public void init() {}

    /**
     * This runs the OpMode, updating the Follower as well as printing out the debug statements to
     * the Telemetry, as well as the FTC Dashboard.
     */
    @Override
    public void loop() {
        Tuning.follower.update();
        Tuning.draw();

        if (Tuning.follower.atParametricEnd()) {
            Tuning.follower.followPath(circle);
        }
    }
}

/**
 * This is the Drawing class. It handles the drawing of stuff on Panels Dashboard, like the robot.
 *
 * @author Lazar - 19234
 * @version 1.1, 5/19/2025
 */
class Drawing {
    public static final double ROBOT_RADIUS = 9; // woah
    private static final FieldManager panelsField = PanelsField.INSTANCE.getField();

    private static final Style robotLook = new Style(
            "", "#3F51B5", 0.75
    );
    private static final Style historyLook = new Style(
            "", "#4CAF50", 0.75
    );

    /**
     * This prepares Panels Field for using Pedro Offsets
     */
    public static void init() {
        panelsField.setOffsets(PanelsField.INSTANCE.getPresets().getPEDRO_PATHING());
    }

    /**
     * This draws everything that will be used in the Follower's telemetryDebug() method. This takes
     * a Follower as an input, so an instance of the DashbaordDrawingHandler class is not needed.
     *
     * @param follower Pedro Follower instance.
     */
    public static void drawDebug(Follower follower) {
        if (follower.getCurrentPathChain() != null && follower.getCurrentPath() != null) {
            drawPath(follower.getCurrentPathChain(), robotLook);
            Pose closestPoint = follower.getPointFromPath(follower.getCurrentPath().getClosestPointTValue());
            drawRobot(new Pose(closestPoint.getX(), closestPoint.getY(), follower.getCurrentPath().getHeadingGoal(follower.getCurrentPath().getClosestPointTValue())), robotLook);
        } else if (follower.getCurrentPath() != null) {
            drawPath(follower.getCurrentPath(), robotLook);
            Pose closestPoint = follower.getPointFromPath(follower.getCurrentPath().getClosestPointTValue());
            drawRobot(new Pose(closestPoint.getX(), closestPoint.getY(), follower.getCurrentPath().getHeadingGoal(follower.getCurrentPath().getClosestPointTValue())), robotLook);
        }
        
        drawPoseHistory(follower.getPoseHistory(), historyLook);
        drawRobot(follower.getPose(), historyLook);

        sendPacket();
    }

    /**
     * This draws a robot at a specified Pose with a specified
     * look. The heading is represented as a line.
     *
     * @param pose  the Pose to draw the robot at
     * @param style the parameters used to draw the robot with
     */
    public static void drawRobot(Pose pose, Style style) {
        if (pose == null || Double.isNaN(pose.getX()) || Double.isNaN(pose.getY()) || Double.isNaN(pose.getHeading())) {
            return;
        }

        panelsField.setStyle(style);
        panelsField.moveCursor(pose.getX(), pose.getY());
        panelsField.circle(ROBOT_RADIUS);

        Vector v = pose.getHeadingAsUnitVector();
        v.setMagnitude(v.getMagnitude() * ROBOT_RADIUS);
        double x1 = pose.getX() + v.getXComponent() / 2, y1 = pose.getY() + v.getYComponent() / 2;
        double x2 = pose.getX() + v.getXComponent(), y2 = pose.getY() + v.getYComponent();

        panelsField.setStyle(style);
        panelsField.moveCursor(x1, y1);
        panelsField.line(x2, y2);
    }

    /**
     * This draws a robot at a specified Pose. The heading is represented as a line.
     *
     * @param pose the Pose to draw the robot at
     */
    public static void drawRobot(Pose pose) {
        drawRobot(pose, robotLook);
    }

    /**
     * This draws a Path with a specified look.
     *
     * @param path  the Path to draw
     * @param style the parameters used to draw the Path with
     */
    public static void drawPath(Path path, Style style) {
        double[][] points = path.getPanelsDrawingPoints();

        for (int i = 0; i < points[0].length; i++) {
            for (int j = 0; j < points.length; j++) {
                if (Double.isNaN(points[j][i])) {
                    points[j][i] = 0;
                }
            }
        }

        panelsField.setStyle(style);
        for (int i = 0; i < points[0].length - 1; i++) {
            panelsField.moveCursor(points[0][i], points[1][i]);
            panelsField.line(points[0][i + 1], points[1][i + 1]);
        }
    }

    /**
     * This draws all the Paths in a PathChain with a
     * specified look.
     *
     * @param pathChain the PathChain to draw
     * @param style     the parameters used to draw the PathChain with
     */
    public static void drawPath(PathChain pathChain, Style style) {
        for (int i = 0; i < pathChain.size(); i++) {
            drawPath(pathChain.getPath(i), style);
        }
    }

    /**
     * This draws the pose history of the robot.
     *
     * @param poseTracker the PoseHistory to get the pose history from
     * @param style       the parameters used to draw the pose history with
     */
    public static void drawPoseHistory(PoseHistory poseTracker, Style style) {
        panelsField.setStyle(style);

        int size = poseTracker.getXPositionsArray().length;
        for (int i = 0; i < size - 1; i++) {

            panelsField.moveCursor(poseTracker.getXPositionsArray()[i], poseTracker.getYPositionsArray()[i]);
            panelsField.line(poseTracker.getXPositionsArray()[i + 1], poseTracker.getYPositionsArray()[i + 1]);
        }
    }

    /**
     * This draws the pose history of the robot.
     *
     * @param poseTracker the PoseHistory to get the pose history from
     */
    public static void drawPoseHistory(PoseHistory poseTracker) {
        drawPoseHistory(poseTracker, historyLook);
    }

    /**
     * This tries to send the current packet to FTControl Panels.
     */
    public static void sendPacket() {
        panelsField.update();
    }
}
