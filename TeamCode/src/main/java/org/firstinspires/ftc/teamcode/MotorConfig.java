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

import static org.firstinspires.ftc.teamcode.systems.Driver.getDriveFromPort;

import org.firstinspires.ftc.teamcode.systems.Driver;


/*
 * This file contains a way of configuring the drive wheels.
 */

@TeleOp(name="Config: Wheels", group="Config")
public class MotorConfig extends LinearOpMode {

    // Declare MainTeleOp members.
    private ElapsedTime runtime = new ElapsedTime();

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        Driver drive = new Driver(hardwareMap, gamepad1);

        // Wait for the game to start (driver presses START)
        waitForStart();
        runtime.reset();

        // run until the end of the match (driver presses STOP)
        DcMotor lastActiveDrive = null;
        while (opModeIsActive()) {

            final DcMotor targetMotor = getMotorToSpin(drive);

            // If the targetMotor is the same as last time, then don't do anything else.
            if (targetMotor == lastActiveDrive) {
                continue;
            }

            // If the last motor was a real motor, then stop its spinning
            if (lastActiveDrive != null) {
                lastActiveDrive.setPower(0.0);
            }
            // Remember the target motor for next time. It will be the "last" motor on the next loop
            lastActiveDrive = targetMotor;

            // If the new target motor isn't null, make it spin. Also, print out
            // the name of the motor so we know what to fix.
            if (targetMotor != null) {
                telemetry.addData("Current Wheel", getDriveFromPort(targetMotor.getPortNumber()));
                targetMotor.setPower(0.3);
            }

            telemetry.update();
        }
    }

    DcMotor getMotorToSpin(Driver drive) {

        // Pick which wheel to target based on which button is pressed.
        // The first match wins. Pressing multiple buttons will only match one.
        if (gamepad1.cross) {
            return drive.frontRightDrive;
        }

        if (gamepad1.circle) {
            return drive.frontLeftDrive;
        }

        if (gamepad1.triangle) {
            return drive.backRightDrive;
        }

        if (gamepad1.square) {
            return drive.backLeftDrive;
        }

        // No motor should spin.
        return null;
    }
}
