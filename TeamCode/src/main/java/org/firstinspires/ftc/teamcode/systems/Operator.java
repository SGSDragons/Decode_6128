package org.firstinspires.ftc.teamcode.systems;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

@Config
public class Operator {

    private final DcMotorEx shooterMotor;
    private final DcMotorEx bCMotor;
    public static double shooterTargetVelocity;
    public static double autoFeedRange;

    public Operator(HardwareMap hardwareMap) {
        // Set shooter wheel and belt-collector wheel motors
        shooterMotor = hardwareMap.get(DcMotorEx.class, "shoot");
        bCMotor = hardwareMap.get(DcMotorEx.class, "collect");

        // Set important constants & variables
        shooterTargetVelocity = 1400;
        autoFeedRange = 100;
    }

    public void Operate(Gamepad driverGamepad, Gamepad operatorGamepad) {
        // Backwards button for when the artifacts are in the way of the shooter
        if (operatorGamepad.square || operatorGamepad.x) {
            shooterMotor.setVelocity(-1000); // -50% of the max velocity (2000)
            bCMotor.setPower(-0.5); // -50% of the max power (1.0)
        }

        // Spin shooter wheel if the trigger is being held
        if (operatorGamepad.right_trigger > 0) {
            shooterMotor.setVelocity(shooterTargetVelocity);
        } else if (operatorGamepad.square || !operatorGamepad.x) {
            shooterMotor.setVelocity(0.0);
        }

        // Spin belt-collector wheel if the trigger is being held
        if (operatorGamepad.left_trigger > 0) {
            bCMotor.setPower(1.0);
        }
        // Auto run the belt if it is at max speed  >>> TEMPORARILY DISABLED <<<
        /*
        else if (Math.abs(shooterTargetVelocitySetting - shooterMotor.getVelocity()) < autoFeedRange && !operatorGamepad.circle && !operatorGamepad.b && operatorGamepad.right_trigger > 0.0) {
            bCMotor.setPower(1.0);
        }
        */
        else if (!operatorGamepad.square || !operatorGamepad.x) {
            bCMotor.setPower(0.0);
        }

        // Changeable shooter speed
        if (operatorGamepad.dpad_up) {
            shooterTargetVelocity = 1500;
        } else if (operatorGamepad.dpad_left || operatorGamepad.dpad_right) {
            shooterTargetVelocity = 1400;
        } else if (operatorGamepad.dpad_down) {
            shooterTargetVelocity = 1300;
        }


        // Communication when needed
        if (driverGamepad.triangle || driverGamepad.y) {
            operatorGamepad.
                    rumble(100);
            operatorGamepad.setLedColor(55, 255, 55, 200);
        }
        if (operatorGamepad.triangle || operatorGamepad.y) {
            driverGamepad.rumble(100);
            driverGamepad.setLedColor(55, 255, 55, 200);
        }
    }
}