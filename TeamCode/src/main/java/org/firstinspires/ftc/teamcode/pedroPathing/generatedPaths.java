package org.firstinspires.ftc.teamcode.pedroPathing;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.*;

public class generatedPaths {

    public static PathChain redPath1(Follower follower) {
        PathBuilder builder = new PathBuilder(follower);
        return builder
                .addPath(new BezierLine(
                        new Pose(0, 0),
                        new Pose(-10, 0)
                ))
                .setLinearHeadingInterpolation(0, 0)
                .build();
    }

    public static PathChain redPath2(Follower follower) {
        PathBuilder builder = new PathBuilder(follower);
        return builder
                .addPath(new BezierLine(
                        new Pose(-10, 0),
                        new Pose(-46.5, -8.5)
                ))
                .setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(-45))
                .build();
    }

    public static PathChain redPath3(Follower follower) {
        PathBuilder builder = new PathBuilder(follower);
        return builder
                .addPath(new BezierLine(
                        new Pose(-46.5, -8.5),
                        new Pose(-30, -25)
                ))
                .setTangentHeadingInterpolation()
                .setVelocityConstraint(12)
                .build();
    }

    public static PathChain redPath4(Follower follower) {
        PathBuilder builder = new PathBuilder(follower);
        return builder
                .addPath(new BezierLine(
                        new Pose(-30, -25),
                        new Pose(-10, 0)
                ))
                .setLinearHeadingInterpolation(Math.toRadians(-45), Math.toRadians(0))
                .build();
    }

    public static PathChain redPath5(Follower follower) {
        PathBuilder builder = new PathBuilder(follower);
        return builder
                .addPath(new BezierLine(
                        new Pose(-10, 0),
                        new Pose(-59, -26)
                ))
                .setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(-45))
                .build();
    }

    public static PathChain redPath6(Follower follower) {
        PathBuilder builder = new PathBuilder(follower);
        return builder
                .addPath(new BezierLine(
                        new Pose(-59, -26),
                        new Pose(-45, -41)
                ))
                .setTangentHeadingInterpolation()
                .setVelocityConstraint(12)
                .build();
    }

    public static PathChain redPath7(Follower follower) {
        PathBuilder builder = new PathBuilder(follower);
        return builder
                .addPath(new BezierLine(
                        new Pose(-45, -41),
                        new Pose(-10, 0)
                ))
                .setLinearHeadingInterpolation(Math.toRadians(-45), Math.toRadians(0))
                .build();
    }
    public static PathChain redPath8(Follower follower) {
        PathBuilder builder = new PathBuilder(follower);
        return builder
                .addPath(new BezierLine(
                        new Pose(-10, 0),
                        new Pose(-76, -43.5)
                ))
                .setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(-45))
                .build();
    }
    public static PathChain redPath9(Follower follower) {
        PathBuilder builder = new PathBuilder(follower);
        return builder
                .addPath(new BezierLine(
                        new Pose(-76, -43.5),
                        new Pose(-62.5, -56.5)
                ))
                .setTangentHeadingInterpolation()
                .setVelocityConstraint(12)
                .build();
    }
    public static PathChain bluePath1(Follower follower) {
        PathBuilder builder = new PathBuilder(follower);
        return builder
                .addPath(new BezierLine(
                        new Pose(0, 0),
                        new Pose(-10, 0)
                ))
                .setLinearHeadingInterpolation(0, 0)
                .build();
    }

    public static PathChain bluePath2(Follower follower) {
        PathBuilder builder = new PathBuilder(follower);
        return builder
                .addPath(new BezierLine(
                        new Pose(-10, 0),
                        new Pose(-46.5, 8.5)
                ))
                .setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(45))
                .build();
    }

    public static PathChain bluePath3(Follower follower) {
        PathBuilder builder = new PathBuilder(follower);
        return builder
                .addPath(new BezierLine(
                        new Pose(-46.5, 8.5),
                        new Pose(-30, 25)
                ))
                .setTangentHeadingInterpolation()
                .setVelocityConstraint(12)
                .build();
    }

    public static PathChain bluePath4(Follower follower) {
        PathBuilder builder = new PathBuilder(follower);
        return builder
                .addPath(new BezierLine(
                        new Pose(-30, 25),
                        new Pose(-10, 0)
                ))
                .setLinearHeadingInterpolation(Math.toRadians(45), Math.toRadians(0))
                .build();
    }

    public static PathChain bluePath5(Follower follower) {
        PathBuilder builder = new PathBuilder(follower);
        return builder
                .addPath(new BezierLine(
                        new Pose(-10, 0),
                        new Pose(-59, 26)
                ))
                .setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(45))
                .build();
    }

    public static PathChain bluePath6(Follower follower) {
        PathBuilder builder = new PathBuilder(follower);
        return builder
                .addPath(new BezierLine(
                        new Pose(-59, 26),
                        new Pose(-45, 41)
                ))
                .setTangentHeadingInterpolation()
                .setVelocityConstraint(12)
                .build();
    }

    public static PathChain bluePath7(Follower follower) {
        PathBuilder builder = new PathBuilder(follower);
        return builder
                .addPath(new BezierLine(
                        new Pose(-45, 41),
                        new Pose(-10, 0)
                ))
                .setLinearHeadingInterpolation(Math.toRadians(45), Math.toRadians(0))
                .build();
    }
    public static PathChain bluePath8(Follower follower) {
        PathBuilder builder = new PathBuilder(follower);
        return builder
                .addPath(new BezierLine(
                        new Pose(-10, 0),
                        new Pose(-76, 43.5)
                ))
                .setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(45))
                .build();
    }
    public static PathChain bluePath9(Follower follower) {
        PathBuilder builder = new PathBuilder(follower);
        return builder
                .addPath(new BezierLine(
                        new Pose(-76, 43.5),
                        new Pose(-62.5, 56.5)
                ))
                .setTangentHeadingInterpolation()
                .setVelocityConstraint(12)
                .build();
    }
}