package teamcode.impl;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.tfod.Recognition;

import java.util.List;

import teamcode.common.BoundingBox2D;
import teamcode.common.League1TTArm;
import teamcode.common.TTDriveSystem;
import teamcode.common.TTOpMode;
import teamcode.common.TTVision;
import teamcode.common.Vector2;

public class TTAutoRed extends TTOpMode {
    private static final BoundingBox2D SKYSTONE_POS_1 = new BoundingBox2D(0, 0, 0, 0);
    private static final BoundingBox2D SKYSTONE_POS_2 = new BoundingBox2D(0, 0, 0, 0);
    private static final BoundingBox2D SKYSTONE_POS_3 = new BoundingBox2D(0, 0, 0, 0);

    private TTDriveSystem driveSystem;
    private League1TTArm arm;
    private TTVision vision;
    private int skystonePos;

    @Override
    protected void onInitialize() {
        driveSystem = new TTDriveSystem(hardwareMap);
        arm = new League1TTArm(hardwareMap);
        vision = new TTVision(hardwareMap);
        vision.enable();
    }

    @Override
    protected void onStart() {
        skystonePos = scanStones();
        setArmStartPos();
        grabBlock4();
    }

    @Override
    protected void onStop() {
    }

    /**
     * Returns the position of the skystones. Returns 3 if the stones are in the first and fourth
     * slots. Returns 2 if the stones are in the second and fifth slots. Returns 1 if the stones
     * are in the third and sixth slots.
     */
    private int scanStones() {
        List<Recognition> recognitions = vision.getRecognitions();
        for (Recognition recognition : recognitions) {
            if (recognition.getLabel().equals(TTVision.LABEL_SKYSTONE)) {
                Vector2 center = TTVision.getCenter(recognition);
                if (SKYSTONE_POS_1.contains(center)) {
                    return 3;
                } else if (SKYSTONE_POS_2.contains(center)) {
                    return 2;
                } else if (SKYSTONE_POS_3.contains(center)) {
                    return 1;
                }
            }
        }
        return 3; // assume left position if image recognition fails.
    }

    public void grabBlock4() {
        driveSystem.lateral(-10, 0.25);
        driveSystem.vertical(31.5, 0.25);
        arm.closeClaw();
        sleep(250);
        arm.timedLift(0.75, 0.5);
        sleep(250);
        driveSystem.vertical(-10, 0.25);
        driveSystem.turn(90, 0.25);
        driveSystem.vertical(83.5, 0.5);
        arm.timedLift(1, 0.5);
        sleep(250);
        driveSystem.turn(-90, 0.25);
        driveSystem.vertical(15.75, 0.25);
        sleep(250);
        arm.lower(0.5);
        driveSystem.turn(-90, 0.25);
        driveSystem.lateral(25, 0.25);
        driveSystem.vertical(4, 0.25);
        arm.openClaw();
        sleep(250);
        arm.timedLift(1, 0.5);
        driveSystem.vertical(-10, 0.5);
    }

    public void grabBlock5() {
        driveSystem.lateral(-2, 0.25);
        driveSystem.vertical(31.5, 0.25);
        arm.closeClaw();
        sleep(250);
        arm.timedLift(0.75, 0.5);
        sleep(250);
        driveSystem.vertical(-10, 0.25);
        driveSystem.turn(90, 0.25);
        driveSystem.vertical(83.5, 0.5);
        arm.timedLift(1, 0.5);
        sleep(250);
        driveSystem.turn(-90, 0.25);
        driveSystem.vertical(15.75, 0.25);
        sleep(250);
        arm.lower(0.5);
        driveSystem.turn(-90, 0.25);
        driveSystem.lateral(25, 0.25);
        driveSystem.vertical(4, 0.25);
        arm.openClaw();
        sleep(250);
        arm.timedLift(1, 0.5);
        driveSystem.vertical(-10, 0.5);
    }
    //Opens claw and lowers arm for starting pos
    private void setArmStartPos(){
        arm.openClaw();
        arm.lower(0.5);
    }
}
