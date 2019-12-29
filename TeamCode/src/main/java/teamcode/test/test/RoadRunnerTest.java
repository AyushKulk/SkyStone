package teamcode.test.test;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.path.Path;
import com.acmerobotics.roadrunner.path.PathBuilder;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.acmerobotics.roadrunner.trajectory.TrajectoryGenerator;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import teamcode.test.test.mecanum.SampleMecanumDriveBase;
import teamcode.test.test.mecanum.SampleMecanumDriveREVOptimized;

public class RoadRunnerTest extends LinearOpMode {
    SampleMecanumDriveBase drive;
    Path path;


    @Override
    public void runOpMode() throws InterruptedException {
        drive = new SampleMecanumDriveREVOptimized(hardwareMap);
        waitForStart();
        path = new PathBuilder(new Pose2d(0,0)).splineTo(new Pose2d(72,0)).build();
        Trajectory trajectory = TrajectoryGenerator.INSTANCE.generateTrajectory(path, DriveConstants.BASE_CONSTRAINTS);
        drive.followTrajectorySync(trajectory);
        drive.turnSync(Math.toRadians(90));
    }
}
