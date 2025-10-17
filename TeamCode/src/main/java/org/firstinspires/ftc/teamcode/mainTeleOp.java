    package org.firstinspires.ftc.teamcode;

    import com.qualcomm.robotcore.eventloop.opmode.OpMode;
    import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
    import com.qualcomm.robotcore.util.ElapsedTime;

    @TeleOp(name="teleOp", group = "testing")
    public class mainTeleOp extends OpMode {
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
        boolean prevTriangle = false;

        ElapsedTime sendTimer = new ElapsedTime();
        boolean sendingAll = false;
        int sendStage = 0;


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
            boolean trianglePressed = gamepad1.triangle;

            if (trianglePressed && !prevTriangle && !sendingAll) {
                startSendAll();
            }

            if (sendingAll) {
                updateSendAll();
            }

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
            prevTriangle = trianglePressed;

            supply.update();
            telemetry.update();
        }
        public void startSendAll() {
            sendingAll = true;
            sendStage = 0;
            sendTimer.reset();

            hwManager.intake.setVelocity(0);
            supply.send();
        }

        public void updateSendAll() {
            switch (sendStage) {
                case 0:
                    if (sendTimer.milliseconds() > 620) {
                        supply.update();
                        intake.toggle();
                        sendTimer.reset();
                        sendStage++;
                    }
                    break;

                case 1:
                    if (sendTimer.milliseconds() > 800) {
                        supply.send();
                        sendTimer.reset();
                        sendStage++;
                    }
                    break;

                case 2:
                    if (sendTimer.milliseconds() > 620) {
                        supply.update();
                        sendTimer.reset();
                        sendStage++;
                    }
                    break;

                case 3:
                    if (sendTimer.milliseconds() > 800) {
                        supply.send();
                        intake.toggle();
                        sendTimer.reset();
                        sendStage++;
                    }
                    break;

                case 4:
                    if (sendTimer.milliseconds() > 620) {
                        supply.update();
                        sendingAll = false;
                    }
                    break;
            }
        }

    }
