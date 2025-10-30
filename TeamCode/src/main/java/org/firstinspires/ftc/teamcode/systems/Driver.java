package org.firstinspires.ftc.teamcode.systems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import java.util.List;

public class Driver {
    public final DcMotor frontLeftDrive;
    public final DcMotor frontRightDrive;
    public final DcMotor backLeftDrive;
    public final DcMotor backRightDrive;

    public Driver(HardwareMap hardwareMap) {
        // Initialize the hardware variables. Note that the strings used here as parameters
        // to 'get' must correspond to the names assigned during the robot configuration
        // step (using the FTC Robot Controller app on the phone).
        frontLeftDrive = hardwareMap.get(DcMotor.class, "drive_a");
        frontRightDrive = hardwareMap.get(DcMotor.class, "drive_b");
        backLeftDrive = hardwareMap.get(DcMotor.class, "drive_c");
        backRightDrive = hardwareMap.get(DcMotor.class, "drive_d");


        // To drive forward, most robots need the motor on one side to be reversed, because the axles point in opposite directions.
        // Pushing the left stick forward MUST make robot go forward. So adjust these two lines based on your first test drive.
        // Note: The settings here assume direct drive on left and right wheels.  Gear Reduction or 90 Deg drives may require direction flips
        frontLeftDrive.setDirection(DcMotor.Direction.FORWARD);
        frontRightDrive.setDirection(DcMotor.Direction.REVERSE);
        backLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        backRightDrive.setDirection(DcMotor.Direction.FORWARD);
    }

    public void Drive(Gamepad gamepad1, Gamepad gamepad2) {

        // Get input from the gamepad
        double forward = -gamepad1.left_stick_y;  // Forward is negative Y
        double strafe = gamepad1.left_stick_x;   // Left/Right strafe
        double turn = gamepad1.right_stick_x;    // Turn left/right

        // Calculate power for each wheel
        double frontLeftPower  = forward + strafe + turn;
        double frontRightPower = forward - strafe - turn;
        double backLeftPower   = forward - strafe + turn;
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
        frontLeftDrive.setPower(frontLeftPower);
        frontRightDrive.setPower(frontRightPower);
        backLeftDrive.setPower(backLeftPower);
        backRightDrive.setPower(backRightPower);


    }
    public static Object getDriveFromPort(int portNumber) {
        List<String> Motors = List.of("drive_a","drive_b","drive_c","drive_d");

        return Motors.get(portNumber);
    }
}
