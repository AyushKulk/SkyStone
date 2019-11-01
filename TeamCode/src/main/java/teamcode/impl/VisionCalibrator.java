package teamcode.impl;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.robotcore.external.tfod.Recognition;

import java.util.List;

import teamcode.common.BoundingBox2D;
import teamcode.common.TTOpMode;
import teamcode.common.TTVisionTF;

@Autonomous(name = "Vision Calibrator")
public class VisionCalibrator extends TTOpMode {

    private TTVisionTF vision;

    @Override
    protected void onInitialize() {
        vision = new TTVisionTF(hardwareMap);
        vision.enable();
    }

    @Override
    protected void onStart() {
        while (opModeIsActive()) {
            List<Recognition> recognitions = vision.getRecognitions();
            for (Recognition recognition : recognitions) {
                BoundingBox2D boundingBox = TTVisionTF.getBoundingBox(recognition);
                telemetry.addData(recognition.getLabel(), boundingBox);
            }
            telemetry.update();
        }
    }

    @Override
    protected void onStop() {
        vision.disable();
    }

}
