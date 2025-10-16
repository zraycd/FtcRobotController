package org.firstinspires.ftc.teamcode.pedroPathing;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.*;

public class generatedPaths {

    public static PathChain buildPath1(Follower follower) {
        PathBuilder builder = new PathBuilder(follower);
        return builder
                .addPath(new BezierLine(
                        new Pose(0.0, 0.0),
                        new Pose(50.000, 0.0)
                ))
                .setLinearHeadingInterpolation(0, 0)
                .build();
    }

    public static PathChain buildPath2(Follower follower) {
        PathBuilder builder = new PathBuilder(follower);
        return builder
                .addPath(new BezierLine(
                        new Pose(100.000, 100.000),
                        new Pose(659.000, 989.000)
                ))
                .setLinearHeadingInterpolation(Math.toRadians(45), Math.toRadians(0))
                .build();
    }

    public static PathChain buildPath3(Follower follower) {
        PathBuilder builder = new PathBuilder(follower);
        return builder
                .addPath(new BezierLine(
                        new Pose(96.000, 83.000),
                        new Pose(118.000, 83.000)
                ))
                .setTangentHeadingInterpolation()
                .build();
    }

    public static PathChain buildPath4(Follower follower) {
        PathBuilder builder = new PathBuilder(follower);
        return builder
                .addPath(new BezierLine(
                        new Pose(118.000, 83.000),
                        new Pose(96.000, 100.000)
                ))
                .setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(45))
                .build();
    }

    public static PathChain buildPath5(Follower follower) {
        PathBuilder builder = new PathBuilder(follower);
        return builder
                .addPath(new BezierLine(
                        new Pose(96.000, 100.000),
                        new Pose(96.000, 59.000)
                ))
                .setLinearHeadingInterpolation(Math.toRadians(45), Math.toRadians(0))
                .build();
    }

    public static PathChain buildPath6(Follower follower) {
        PathBuilder builder = new PathBuilder(follower);
        return builder
                .addPath(new BezierLine(
                        new Pose(96.000, 59.000),
                        new Pose(118.000, 59.000)
                ))
                .setTangentHeadingInterpolation()
                .build();
    }

    public static PathChain buildPath7(Follower follower) {
        PathBuilder builder = new PathBuilder(follower);
        return builder
                .addPath(new BezierLine(
                        new Pose(118.000, 59.000),
                        new Pose(96.000, 100.000)
                ))
                .setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(45))
                .build();
    }
}