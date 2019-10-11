package teamcode.impl;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Hardware;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import teamcode.common.HardwareComponentNames;
import teamcode.common.TTArm;
import teamcode.common.TTDriveSystem;
import teamcode.common.TTOpMode;
import teamcode.common.Vector2;

@TeleOp(name = "TT TeleOp")
public class TTTeleOp extends TTOpMode {

    private static final double TURN_SPEED_MODIFIER = 0.6;
    private static final double REDUCED_DRIVE_SPEED = 0.6;

    //private TTDriveSystem driveSystem;
    private TTArm arm;

    @Override
    protected void onInitialize() {
    }

    @Override
    protected void onStart() {
        DcMotor armLift = hardwareMap.get(DcMotor.class, HardwareComponentNames.ARM_LIFT);
        armLift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        armLift.setTargetPosition(1545);
        armLift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        armLift.setPower(1.0);
        while(armLift.isBusy());
        //driveSystem = new TTDriveSystem(hardwareMap);
       // arm = new TTArm(hardwareMap);

        //while (opModeIsActive()) {
          //  update();
        //}
    }

    private void update() {
        //driveUpdate();
        armUpdate();
    }

    protected void onStop() {
    }

    private void driveUpdate() {
        double vertical = gamepad1.right_stick_y;
        double horizontal = gamepad1.right_stick_x;
        double turn = gamepad1.left_stick_x * TURN_SPEED_MODIFIER;
        Vector2 velocity = new Vector2(vertical, horizontal);
        if (!gamepad1.right_bumper) {
            velocity = velocity.multiply(REDUCED_DRIVE_SPEED);
        }
        //driveSystem.continuous(velocity, turn);
    }

    private void armUpdate(){
        if(gamepad2.y) {
            arm.armLift();
        }
        if(gamepad2.a){
            arm.armLower();
        }
        if(gamepad2.x && arm.getClawPos() == 1){
            arm.rotateClaw(0);
        }else if (gamepad2.x && arm.getClawPos() == 0){
            arm.rotateClaw(1);
        }
        if (gamepad2.b && arm.getWristPos() <= 0.8) {
            arm.rotateWrist(0);
        }else if (gamepad2.b && arm.getWristPos() < 0.1){
            arm.rotateWrist(0.7);
        }
    }

}
