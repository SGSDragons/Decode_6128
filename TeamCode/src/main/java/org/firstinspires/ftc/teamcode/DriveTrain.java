package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import java.util.List;

public class DriveTrain {
    public final DcMotor leftDriveFront;
    public final DcMotor leftDriveBack;
    public final DcMotor rightDriveFront;
    public final DcMotor rightDriveBack;

    public DriveTrain(HardwareMap hardwareMap) {
        // Initialize the hardware variables. Note that the strings used here as parameters
        // to 'get' must correspond to the names assigned during the robot configuration
        // step (using the FTC Robot Controller app on the phone).
        leftDriveFront  = hardwareMap.get(DcMotor.class, "drive_d");
        leftDriveBack  = hardwareMap.get(DcMotor.class, "drive_b");
        rightDriveFront  = hardwareMap.get(DcMotor.class, "drive_c");
        rightDriveBack = hardwareMap.get(DcMotor.class, "drive_a");


        // To drive forward, most robots need the motor on one side to be reversed, because the axles point in opposite directions.
        // Pushing the left stick forward MUST make robot go forward. So adjust these two lines based on your first test drive.
        // Note: The settings here assume direct drive on left and right wheels.  Gear Reduction or 90 Deg drives may require direction flips
        leftDriveFront.setDirection(DcMotor.Direction.REVERSE);
        leftDriveBack.setDirection(DcMotor.Direction.REVERSE);
        rightDriveFront.setDirection(DcMotor.Direction.FORWARD);
        rightDriveBack.setDirection(DcMotor.Direction.FORWARD);
    }

    public void drive(double forward, double strafe, double turn) {

        // Calculate power for each wheel
        double frontLeftPower  = forward + strafe + turn;
        double backLeftPower   = forward - strafe + turn;
        double frontRightPower = forward - strafe - turn;
        double backRightPower  = forward + strafe - turn;

        // Normalize the values if any motor power exceeds the range [-1.0, 1.0]
        double max = Math.max(Math.abs(frontLeftPower), Math.max(Math.abs(backLeftPower),
                Math.max(Math.abs(frontRightPower), Math.abs(backRightPower))));

        if (max > 1.0) {
            frontLeftPower  /= max;
            backLeftPower   /= max;
            frontRightPower /= max;
            backRightPower  /= max;
        }

        // Send calculated power to wheels
        leftDriveFront.setPower(frontLeftPower);
        leftDriveBack.setPower(backLeftPower);
        rightDriveFront.setPower(frontRightPower);
        rightDriveBack.setPower(backRightPower);
    }
    public static Object getMotorFromPort(int portNumber) {
        List<String> Motors = List.of("drive_a","drive_b","drive_c","drive_d");

        return Motors.get(portNumber);
    }
}
