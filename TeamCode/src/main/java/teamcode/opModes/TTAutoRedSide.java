package teamcode.opModes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import java.util.Timer;
import java.util.TimerTask;

import teamcode.common.AbstractOpMode;
import teamcode.common.BoundingBox2D;
import teamcode.common.Debug;
import teamcode.common.SkyStoneConfiguration;
import teamcode.common.Vector2D;
import teamcode.common.Vector3D;
import teamcode.robotComponents.TTArmSystem;
import teamcode.robotComponents.TTDriveSystem;
import teamcode.robotComponents.TTVision;

@Autonomous(name = "TTAutoRedSide")
public class TTAutoRedSide extends AbstractOpMode {

    private static final BoundingBox2D MIDDLE_STONE_BOUNDS = new BoundingBox2D(-10, 0, 110, 0);
    private static final BoundingBox2D RIGHT_STONE_BOUNDS = new BoundingBox2D(120, 0, 500, 0);

    private TTArmSystem arm;
    private TTDriveSystem driveSystem;
    private TTVision vision;
    SkyStoneConfiguration config = null;

    @Override
    protected void onInitialize() {
        arm = new TTArmSystem(this);
        driveSystem = new TTDriveSystem(hardwareMap);
        vision = new TTVision(hardwareMap, TTVision.CameraType.PHONE);
    }

    @Override
    protected void onStart() {
        moveToScanningPosition();
        Vector3D pos;
        boolean flag = true;
        while (flag) {
            Debug.log("Line 1");
            pos = vision.getSkystonePosition();
            if (pos == null) {
                continue;
            } else {
                flag = false;
            }
            Debug.log("Line 2");
            double horizontalDistanceFromRobot = pos.getY();
            Debug.log("Line 3");
            config = determineSkystoneConfig(horizontalDistanceFromRobot);
            Debug.log(config);
        }
        if (config.equals(SkyStoneConfiguration.THREE_SIX)) {
            suckSkystone(6);
        }else if(config.equals(SkyStoneConfiguration.TWO_FIVE)){
            suckSkystone(5);
        }else{
            suckSkystone(4);
        }
    }

    private SkyStoneConfiguration determineSkystoneConfig(double horizontalDistanceFromRobot) {
        Vector2D visionPos = new Vector2D(horizontalDistanceFromRobot, 0);
        if (MIDDLE_STONE_BOUNDS.contains(visionPos)) {
            return SkyStoneConfiguration.TWO_FIVE;
        } else if (RIGHT_STONE_BOUNDS.contains(visionPos)) {
            return SkyStoneConfiguration.ONE_FOUR;
        } else {
            return SkyStoneConfiguration.THREE_SIX;
        }
    }

    private void moveToScanningPosition() {
        foundationGrabber(true);
        driveSystem.lateral(12, 0.6);
    }

    private void suckSkystone(int skystoneNum) {
        driveSystem.turn(-90, 0.6);
        timedIntake();
        driveSystem.lateral(42 - skystoneNum * 8, 0.6);
        driveSystem.vertical(-20, 0.6);
        driveSystem.vertical(-8, 0.2);
        if (arm.intakeIsFull()) {
            Debug.log("Intake full");
            arm.intake(0);
            driveSystem.vertical(24, 0.6);
            arm.setClawPosition(true);
            driveSystem.turn(-90, 0.6);
            moveToFoundation(6);
        } else {
            timedIntake();
        }
    }

    private void moveToFoundation(int skystoneNum) {
        arm.grabFoundation(false);
        driveSystem.vertical(123 - skystoneNum * 8, 0.6);
        driveSystem.turn(-90, 0.6);
        driveSystem.vertical(20, 0.6);
        arm.grabFoundation(true);
        driveSystem.frontArc(true, 0.6, -90);
        scoreStoneInFoundation();

    }

    private void scoreStoneInFoundation() {
        Timer wrist = getNewTimer();

        TimerTask wristTask = new TimerTask(){
            @Override
            public void run() {
                arm.setWristPosition(true);
            }
        };
        
        wrist.schedule(wristTask, 100);
    }

    private void timedIntake() {
        TimerTask startIntake = new TimerTask() {
            @Override
            public void run() {
                arm.intake(1.0);
            }
        };
        getNewTimer().schedule(startIntake, 0);
    }

    private void foundationGrabber(final boolean open){
        TimerTask activateGrabber = new TimerTask(){
            @Override
            public void run(){
                arm.grabFoundation(open);
            }
        };
        getNewTimer().schedule(activateGrabber, 0);
    }

    @Override
    protected void onStop() {

    }
}
