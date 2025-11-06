package org.firstinspires.ftc.teamcode.systems;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple.Direction;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import java.util.Objects;

@Config
public class Operator {

    public final DcMotorEx shooterMotor;
    public final DcMotorEx beltMotor;
    public final DcMotorEx collectorMotor;
    public final Servo stopper;
    private static double shooterTargetVelocity;
    private static double autoFeedRange;

    public Operator(HardwareMap hardwareMap) {
        // Set motors and Servos
        shooterMotor = hardwareMap.get(DcMotorEx.class, "shoot");
        beltMotor = hardwareMap.get(DcMotorEx.class, "belt");
        collectorMotor = hardwareMap.get(DcMotorEx.class, "collect");
        stopper = hardwareMap.get(Servo.class, "stopper");

        beltMotor.setDirection(Direction.REVERSE);


        // Set important constants & variables
        shooterTargetVelocity = 1400;
        autoFeedRange = 100;
    }

    public void Operate(Gamepad driverGamepad, Gamepad operatorGamepad) {

        // Spin shooter wheel if the trigger is being held
        if (operatorGamepad.right_trigger > 0) {
            runShooter();
        }
        // Backwards button for when the artifacts are in the way of the shooter
        else if (packing(operatorGamepad)) {
            pack();
        } else {
            stop(shooterMotor);
        }

        // Run the belt & collector if the trigger is being held
        if (operatorGamepad.left_trigger > 0 && !packing(operatorGamepad)) {
            runIntake();
        }
        // Auto run the belt if it is at max speed
        else if (canAutoFeed(operatorGamepad)) {
            autoFeed();
        }
        else if (!packing(operatorGamepad)) {
            stop(beltMotor, collectorMotor);
        }

        if (operatorGamepad.dpad_down) {
            stopperPosition("open");
        } else if (operatorGamepad.dpad_up) {
            stopperPosition("closed");
        }
    }

    public void pack() {
        shooterMotor.setVelocity(-1000); // -50% of the max velocity (2000)
        beltMotor.setPower(-1.0);
        collectorMotor.setPower(0.2); // Power is positive to stop the artifacts from rolling out the back
    }
    public boolean packing(Gamepad operatorGamepad) {
        return operatorGamepad.square && operatorGamepad.x;
    }
    public void runShooter() {
        shooterMotor.setVelocity(shooterTargetVelocity);
    }
    public void runIntake() {
        beltMotor.setPower(1.0);
        collectorMotor.setPower(0.8);
    }
    public void autoFeed() {
        beltMotor.setPower(1.0);
        collectorMotor.setPower(0.6);
    }
    public boolean canAutoFeed(Gamepad operatorGamepad) {
        return Math.abs(shooterTargetVelocity - shooterMotor.getVelocity()) < autoFeedRange
                && !operatorGamepad.circle && !operatorGamepad.b && operatorGamepad.right_trigger > 0.0
                && !packing(operatorGamepad);
    }
    public void stopperPosition(String stopperPosition) {
        if (Objects.equals(stopperPosition, "open")) {
            stopper.setPosition(1.0);
        }
        else if (Objects.equals(stopperPosition, "closed")) {
            stopper.setPosition(0.5);
        }
        else {
            return;
        }
    }
    public void stop(DcMotorEx... motors) {
        for (DcMotorEx motor : motors) {
            motor.setPower(0.0);
        }
    }
}