package teamcode.test;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import teamcode.common.AbstractOpMode;
import teamcode.league3.MoonshotArmSystem;


@Autonomous(name="StoneProce ss")
public class StoneProcessorTest extends AbstractOpMode {


    private MoonshotArmSystem arm;

    @Override
    protected void onInitialize() {
        arm = new MoonshotArmSystem(hardwareMap);
    }

    @Override
    protected void onStart() {
        arm.intakeSequence();
        while(opModeIsActive());
    }

    @Override
    protected void onStop() {

    }
}
