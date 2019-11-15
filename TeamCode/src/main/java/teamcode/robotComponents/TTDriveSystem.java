package teamcode.robotComponents;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDCoefficients;

import teamcode.common.Vector2D;

public class TTDriveSystem {

    // correct ticks = current ticks * correct distance / current distance
    //Old Values
    private static final double INCHES_TO_TICKS_VERTICAL = -42.64;
    private static final double INCHES_TO_TICKS_LATERAL = 47.06;
    private static final double INCHES_TO_TICKS_DIAGONAL = -64.29;
    private static final double DEGREES_TO_TICKS = -8.547404708;
    //Meta values
    private static  double INCHES_TO_TICKS_VERTICAL_META;
    private static  double INCHES_TO_TICKS_LATERAL_META;
    //private static double INCHES_TO_TICKS_DIAGONAL_META;
    private static double DEGREES_TO_TICKS_META;
    private static double DRIVE_SPEED_MODIFIER = 0.7;

    //6000 rpm base
    //approx 28 ticks per revolution
    //28 * 60


    /**
     * Maximum number of ticks a motor's current position must be away from it's target for it to
     * be considered near its target.
     */
    private static final double TICK_ERROR_TOLERANCE = 25.0;
    /**
     * Proportional.
     */
    private static final double P = 2.5;
    /**
     * Integral.
     */
    private static final double I = 0.1;
    /**
     * Derivative.
     */
    private static final double D = 0.0;

    private final DcMotor frontLeft, frontRight, backLeft, backRight;
    private final DcMotor[] motors;
    private final DriveSystem version;

    public TTDriveSystem(HardwareMap hardwareMap) {
        frontLeft = hardwareMap.get(DcMotor.class, TTHardwareComponentNames.FRONT_LEFT_DRIVE);
        frontRight = hardwareMap.get(DcMotor.class, TTHardwareComponentNames.FRONT_RIGHT_DRIVE);
        backLeft = hardwareMap.get(DcMotor.class, TTHardwareComponentNames.BACK_LEFT_DRIVE);
        backRight = hardwareMap.get(DcMotor.class, TTHardwareComponentNames.BACK_RIGHT_DRIVE);
        motors = new DcMotor[]{frontLeft, frontRight, backLeft, backRight};
        version = DriveSystem.old;
        correctDirections();
        setPID();
    }

    //Meta Constructor
    public TTDriveSystem(HardwareMap hardwareMap, double gearRatio){
        frontLeft = hardwareMap.get(DcMotor.class, TTHardwareComponentNames.FRONT_LEFT_DRIVE);
        frontRight = hardwareMap.get(DcMotor.class, TTHardwareComponentNames.FRONT_RIGHT_DRIVE);
        backLeft = hardwareMap.get(DcMotor.class, TTHardwareComponentNames.BACK_LEFT_DRIVE);
        backRight = hardwareMap.get(DcMotor.class, TTHardwareComponentNames.BACK_RIGHT_DRIVE);
        motors = new DcMotor[]{frontLeft, frontRight, backLeft, backRight};
        version = DriveSystem.meta;
        setGearRatios(gearRatio);
        correctDirections();
        setPID();
    }

    private void setGearRatios(double gearRatio) {
        INCHES_TO_TICKS_VERTICAL_META = (28 * gearRatio) / (2.95276 * Math.PI);
        INCHES_TO_TICKS_LATERAL_META = (28 * gearRatio) / (2.95276 * Math.PI);
        DEGREES_TO_TICKS_META =  1;
        //TODO Arbetrary value
    }

    private void correctDirections() {
        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeft.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    private void setPID() {
        PIDCoefficients coefficients = new PIDCoefficients();
        coefficients.i = I;
        coefficients.p = P;
        coefficients.d = D;
        for (DcMotor motor : motors) {
            DcMotorEx ex = (DcMotorEx) motor;
            ex.setPIDCoefficients(DcMotor.RunMode.RUN_TO_POSITION, coefficients);
        }
    }

    public void continuous(Vector2D velocity, double turnSpeed) {
        setRunMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        double direction = velocity.getDirection();

        double maxPow = Math.sin(Math.PI / 4);
        double power = velocity.magnitude() / maxPow;

        double angle = direction - Math.PI / 4;
        double sin = Math.sin(angle);
        double cos = Math.cos(angle);

        double frontLeftPow = power * sin - turnSpeed;
        double frontRightPow = power * cos + turnSpeed;
        double backLeftPow = power * cos - turnSpeed;
        double backRightPow = power * sin + turnSpeed;
        if(isSprint){
            frontLeft.setPower(frontLeftPow);
            frontRight.setPower(frontRightPow);
            backLeft.setPower(backLeftPow);
            backRight.setPower(backRightPow);
        }else {
            frontLeft.setPower(frontLeftPow * DRIVE_SPEED_MODIFIER);
            frontRight.setPower(frontRightPow * DRIVE_SPEED_MODIFIER);
            backLeft.setPower(backLeftPow * DRIVE_SPEED_MODIFIER);
            backRight.setPower(backRightPow * DRIVE_SPEED_MODIFIER);
        }
    }
    public DcMotor[] getMotors(){
        return motors;
    }

    public void vertical(double inches, double speed) {
        setRunMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        int ticks;
        if(version.equals(DriveSystem.old)) {
            ticks = (int) (inches * INCHES_TO_TICKS_VERTICAL);
        }else{
             ticks = (int)(inches * INCHES_TO_TICKS_VERTICAL_META);
        }

        for (DcMotor motor : motors) {
            motor.setTargetPosition(ticks);
        }
        setRunMode(DcMotor.RunMode.RUN_TO_POSITION);

        for (DcMotor motor : motors) {
            motor.setPower(speed);
        }

        while (!nearTarget()) ;
        brake();
    }

    public void lateral(double inches, double speed) {
        setRunMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        int ticks;
        if(version.equals(DriveSystem.old)) {
            ticks = (int) (inches * INCHES_TO_TICKS_LATERAL);
        }else{
            ticks = (int)(inches * INCHES_TO_TICKS_LATERAL_META);
        }

        frontLeft.setTargetPosition(-ticks);
        frontRight.setTargetPosition(ticks);
        backLeft.setTargetPosition(ticks);
        backRight.setTargetPosition(-ticks);
        setRunMode(DcMotor.RunMode.RUN_TO_POSITION);

        for (DcMotor motor : motors) {
            motor.setPower(speed);
        }

        while (!nearTarget()) ;
        brake();
    }

    /**
     * Drives at an angle whose reference angle is 45 degrees and lies in the specified quadrant.
     *
     * @param quadrant 0, 1, 2, or 3 corresponds to I, II, III, or IV respectively
     * @param inches   the inches to be travelled
     * @param speed    [0.0, 1.0]
     */
    public void diagonal(int quadrant, double inches, double speed) {
        setRunMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        int ticks = (int) (inches * INCHES_TO_TICKS_DIAGONAL);
        int[] targets = new int[4];
        double[] powers = new double[4];

        switch (quadrant) {
            case 0:
                // forward right
                targets[0] = ticks;
                targets[3] = ticks;

                powers[0] = speed;
                powers[3] = speed;
                break;
            case 1:
                // forward left
                targets[1] = ticks;
                targets[2] = ticks;

                powers[1] = speed;
                powers[2] = speed;
                break;
            case 2:
                // backward left
                targets[0] = -ticks;
                targets[3] = -ticks;

                powers[0] = speed;
                powers[3] = speed;
                break;
            case 3:
                // backward right
                targets[1] = -ticks;
                targets[2] = -ticks;

                powers[1] = speed;
                powers[2] = speed;
                break;
            default:
                throw new IllegalArgumentException("quadrant must be 0, 1, 2, or 3");
        }

        for (int i = 0; i < 4; i++) {
            DcMotor motor = motors[i];
            motor.setTargetPosition(targets[i]);
        }
        setRunMode(DcMotor.RunMode.RUN_TO_POSITION);

        for (int i = 0; i < 4; i++) {
            DcMotor motor = motors[i];
            motor.setPower(powers[i]);
        }

        while (!nearTarget()) ;
        brake();
    }

    /**
     * @param degrees degrees to turn clockwise
     * @param speed   [0.0, 1.0]
     */
    public void turn(double degrees, double speed) {
        setRunMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        int ticks = (int) (degrees * DEGREES_TO_TICKS);
        frontLeft.setTargetPosition(ticks);
        frontRight.setTargetPosition(-ticks);
        backLeft.setTargetPosition(ticks);
        backRight.setTargetPosition(-ticks);
        setRunMode(DcMotor.RunMode.RUN_TO_POSITION);

        for (DcMotor motor : motors) {
            motor.setPower(speed);
        }

        while (!nearTarget()) ;
        //brake();
        setRunMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    public void brake() {
        for (DcMotor motor : motors) {
            motor.setPower(0.0);
        }
    }

    private boolean nearTarget() {
        for (DcMotor motor : motors) {
            int targetPosition = motor.getTargetPosition();
            int currentPosition = motor.getCurrentPosition();
            double ticksFromTarget = Math.abs(targetPosition - currentPosition);
            if (ticksFromTarget >= TICK_ERROR_TOLERANCE) {
                return false;
            }
        }
        return true;
    }

    private void setRunMode(DcMotor.RunMode mode) {
        for (DcMotor motor : motors) {
            motor.setMode(mode);
        }
    }

    private enum DriveSystem{
        old,meta
    }

}
