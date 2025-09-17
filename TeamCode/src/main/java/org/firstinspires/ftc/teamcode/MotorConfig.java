/* Copyright (c) 2017 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;


/*
 * This file contains an extremely inefficient way of configuring the drive wheels.
 */

@TeleOp(name="Config: Wheels", group="Config")
public class MotorConfig extends LinearOpMode {

    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor leftDriveFront = null;
    private DcMotor leftDriveBack = null;
    private DcMotor rightDriveFront = null;
    private DcMotor rightDriveBack = null;

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        // Initialize the hardware variables. Note that the strings used here as parameters
        // to 'get' must correspond to the names assigned during the robot configuration
        // step (using the FTC Robot Controller app on the phone).
        leftDriveFront  = hardwareMap.get(DcMotor.class, "drive_a");
        leftDriveBack  = hardwareMap.get(DcMotor.class, "drive_b");
        rightDriveFront  = hardwareMap.get(DcMotor.class, "drive_c");
        rightDriveBack = hardwareMap.get(DcMotor.class, "drive_d");


        // To drive forward, most robots need the motor on one side to be reversed, because the axles point in opposite directions.
        // Pushing the left stick forward MUST make robot go forward. So adjust these two lines based on your first test drive.
        // Note: The settings here assume direct drive on left and right wheels.  Gear Reduction or 90 Deg drives may require direction flips
        leftDriveFront.setDirection(DcMotor.Direction.FORWARD);
        leftDriveBack.setDirection(DcMotor.Direction.REVERSE);
        rightDriveFront.setDirection(DcMotor.Direction.FORWARD);
        rightDriveBack.setDirection(DcMotor.Direction.REVERSE);

        // Wait for the game to start (driver presses START)
        waitForStart();
        runtime.reset();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {

            // Set other variables
            int wheelToActivate = 1;
            double frontLeftPower = 0.0;
            double backLeftPower = 0.0;
            double frontRightPower = 0.0;
            double backRightPower = 0.0;
            String currentWheel = "drive_a";

            telemetry.addData("Current Wheel", currentWheel);
            telemetry.addData("Press X to set as leftFront Wheel, Circle for leftBack, Triangle for rightFront, and Square for rightBack", "");
            if (gamepad1.right_bumper) {
                if (wheelToActivate == 1) {
                    frontLeftPower = 1.0;
                    currentWheel = "drive_a";
                } else if (wheelToActivate == 2) {
                    backLeftPower = 1.0;
                    currentWheel = "drive_b";
                } else if (wheelToActivate == 3) {
                    frontRightPower = 1.0;
                    currentWheel = "drive_c";
                } else if (wheelToActivate == 4) {
                    backRightPower = 1.0;
                    currentWheel = "drive_d";
                }
            }
            else if (gamepad1.xWasPressed()) {
                if (wheelToActivate == 1) {
                    telemetry.addLine();
                    telemetry.addData("drive_a set as", "leftFront");
                    wheelToActivate = 2;
                } else if (wheelToActivate == 2) {
                    telemetry.addData("drive_b set as", "leftFront");
                    wheelToActivate = 3;
                } else if (wheelToActivate == 3) {
                    telemetry.addData("drive_c set as", "leftFront");
                    wheelToActivate = 4;
                } else if (wheelToActivate == 4) {
                    telemetry.addData("drive_d set as", "leftFront");
                    requestOpModeStop();
                }
            } else if (gamepad1.circleWasPressed()) {
                if (wheelToActivate == 1) {
                    telemetry.addLine();
                    telemetry.addData("drive_a set as", "leftBack");
                    wheelToActivate = 2;
                } else if (wheelToActivate == 2) {
                    telemetry.addData("drive_b set as", "leftBack");
                    wheelToActivate = 3;
                } else if (wheelToActivate == 3) {
                    telemetry.addData("drive_c set as", "leftBack");
                    wheelToActivate = 4;
                } else if (wheelToActivate == 4) {
                    telemetry.addData("drive_d set as", "leftBack");
                    requestOpModeStop();
                }
            } else if (gamepad1.triangleWasPressed()) {
                if (wheelToActivate == 1) {
                    telemetry.addLine();
                    telemetry.addData("drive_a set as", "rightFront");
                    wheelToActivate = 2;
                } else if (wheelToActivate == 2) {
                    telemetry.addData("drive_b set as", "rightFront");
                    wheelToActivate = 3;
                } else if (wheelToActivate == 3) {
                    telemetry.addData("drive_c set as", "rightFront");
                    wheelToActivate = 4;
                } else if (wheelToActivate == 4) {
                    telemetry.addData("drive_d set as", "rightFront");
                    requestOpModeStop();
                }
            } else if (gamepad1.squareWasPressed()) {
                if (wheelToActivate == 1) {
                    telemetry.addLine();
                    telemetry.addData("drive_a set as", "rightBack");
                    wheelToActivate = 2;
                } else if (wheelToActivate == 2) {
                    telemetry.addData("drive_b set as", "rightBack");
                    wheelToActivate = 3;
                } else if (wheelToActivate == 3) {
                    telemetry.addData("drive_c set as", "rightBack");
                    wheelToActivate = 4;
                } else if (wheelToActivate == 4) {
                    telemetry.addData("drive_d set as", "rightBack");
                    requestOpModeStop();
                }
            }
            // Send calculated power to wheels
            leftDriveFront.setPower(frontLeftPower);
            leftDriveBack.setPower(backLeftPower);
            rightDriveFront.setPower(frontRightPower);
            rightDriveBack.setPower(backRightPower);

            telemetry.update();
        }
    }
}
