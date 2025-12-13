package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.systems.Operator;

import java.util.Collections;

@TeleOp(name = "Flywheel Tuner")
public class FlywheelTuner extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {

        Operator op = new Operator(Collections.emptyList(), hardwareMap);

        waitForStart();
        boolean enabled = false;
        while (opModeIsActive()) {
            if (gamepad1.a) {
                op.flywheelMotor.setPower(0.0);
                enabled = false;
            }

            if (gamepad1.b) {
                //op.retune();
                enabled = true;
            }

            if (enabled) {
                op.Operate(gamepad1);
            }
        }
    }
}
