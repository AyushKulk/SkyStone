package teamcode.test.odometry;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import teamcode.common.AbstractOpMode;
import teamcode.common.Debug;
import teamcode.common.Point;
import teamcode.common.Vector2D;
import teamcode.league3.DriveSystem;
import teamcode.league3.GPS;


@Autonomous(name="PurePursuit")
public class PurePursuitOpMode extends AbstractOpMode {
    PurePursuitMovement movement;
    OdometryWheelsFinal wheels;
    DriveSystem driveSystem;
    Thread odometryUpdate;
    ArrayList<CurvePoint> path = new ArrayList<>();


    @Override
    protected void onInitialize() {
        GPS gps = new GPS(this.hardwareMap, new Vector2D(24, 24), 0);
        driveSystem = new DriveSystem(hardwareMap, wheels);
        movement = new PurePursuitMovement(gps);
    }

    @Override
    protected void onStart() {
        Thread  driveUpdate = new Thread(){
            @Override
            public void run(){
                while(opModeIsActive()) {

                    Vector2D velocity = new Vector2D(MovementVars.xSpeed, MovementVars.ySpeed);
                    driveSystem.continuous(velocity, MovementVars.turnSpeed);
                }
            }
        };
        driveUpdate.start();

        //ArrayList<CurvePoint> path = new ArrayList();
        //path.add(new CurvePoint(50, 50, 0.5, 0.3, 40.0,  1.0,1.0));
        //path.add(new CurvePoint(180.0, 180.0, 0.5, 1.0, 5.0, 90.0, 1.0));
        //path.add(new CurvePoint(220, 180, 0.5, 0.3, 50.0, 90.0, 1.0,1.0));
        //path.add(new CurvePoint(280,50, 0.5, 0.3, 50.0, 90.0, 1.0,1.0));
        //path.add(new CurvePoint(180, 50, 0.3, 0.3, 50.0, 90.0, 1.0,1.0));
        //path.add(new CurvePoint(50.0, 50.0, 0.3, 0.3, 50.0, 90.0, 1.0,1.0));
        //path.add(new CurvePoint(50.0, 200.0, 0.3, 0.3, 5.0, 90.0,1.0));
        //path.add(new CurvePoint(100, 100, 0.3, 0.3, 50.0, 90.0, 1.0,1.0));
        //path.add(new CurvePoint(274, 40, 0.3, 0.3, 5.0, 90.0, 1.0,1.0));

        ArrayList<CurvePoint> followPath = new ArrayList<>();
        followPath.add(new CurvePoint(120, 24, 0.5, 0.5, 5.0, Math.toRadians(50), 1.0));
        followPath.add(new CurvePoint(125, 120, 0.5, 0.5, 5.0, Math.toRadians(50), 1.0));
        followPath.add(new CurvePoint(14, 120, 0.5, 0.5, 5.0, Math.toRadians(50), 1.0));


        //followPath.add(new CurvePoint(0.0,0.0,0.5,1.0,20.0,Math.toRadians(50), 1.0));
//        followPath.add(new CurvePoint(180.0,180.0,1.0,1.0,50.0,Math.toRadians(50), 1.0));
//        followPath.add(new CurvePoint(220.0,180.0,1.0,1.0,50.0,Math.toRadians(50), 1.0));
//        followPath.add(new CurvePoint(280.0,50.0,1.0,1.0,50.0,Math.toRadians(50), 1.0));
//        //followPath.add(new CurvePoint(180.0,0.0,1.0,1.0,10.0,Math.toRadians(50), 1.0));
//        followPath.add(new CurvePoint(320.0,0.0,1.0,1.0,50.0,Math.toRadians(50), 1.0));
//        followPath.add(new CurvePoint(350,150,0.5,1.0,20.0,Math.toRadians(50), 1.0));
//        //followPath.add(new CurvePoint(240,200.0,1.0,1.0,50.0,Math.toRadians(50), 1.0));
//        followPath.add(new CurvePoint(320,240,1.0,1.0,15.0,Math.toRadians(50), 1.0));
        //followPath.add(new CurvePoint(280.0,50.0,1.0,1.0,50.0,Math.toRadians(50), 1.0));
        //followPath.add(new CurvePoint(180.0,0.0,1.0,1.0,50.0,Math.toRadians(50), 1.0));

        while(opModeIsActive()){
            try {
                movement.followCurve(followPath, 90);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }


        }

    }

    @Override
    protected void onStop() {

    }
}
