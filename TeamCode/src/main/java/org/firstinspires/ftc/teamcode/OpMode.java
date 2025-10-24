package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.teamcode.BasicOpMode.*;

@TeleOp(name="6128 DECODE OpMode", group="OpMode")
public class OpMode extends LinearOpMode{

    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        final DriveTrain drive = new DriveTrain(hardwareMap);

        // Wait for the game to start (driver presses START)
        waitForStart();
        runtime.reset();

        // Set shooter wheel and belt-collector wheel motors
        final DcMotor shooterMotor, bCMotor;
        shooterMotor  = hardwareMap.get(DcMotor.class, "shoot");
        bCMotor       = hardwareMap.get(DcMotor.class, "collect");
        shooterMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            double deltaTime = runtime.seconds();

            // Get input from the gamepad
            double forward = -gamepad1.left_stick_y;  // Forward is negative Y
            double strafe = -gamepad1.left_stick_x;   // Left/Right strafe
            double turn = -gamepad1.right_stick_x;    // Turn left/right

            drive.drive(forward, strafe, turn);

            // Spin shooter wheel if the trigger is being held
            if (gamepad2.right_trigger > 0) {
                shooterMotor.setPower(1.0);
            } else {
                shooterMotor.setPower(0.0);
            }

            // Spin belt-collector wheel if the trigger is being held
            if (gamepad2.left_trigger > 0) {
                bCMotor.setPower(1.0);
            }
            // Backwards button for when the artifacts are in the way of the shooter
             else if (gamepad2.left_bumper) {
                bCMotor.setPower(-0.5);
            } else {
                bCMotor.setPower(0.0);
            }
        }
    }
}
