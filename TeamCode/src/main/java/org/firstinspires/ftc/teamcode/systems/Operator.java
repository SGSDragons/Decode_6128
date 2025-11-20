package org.firstinspires.ftc.teamcode.systems;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple.Direction;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

import java.util.Objects;

@Config
public class Operator {

    public final DcMotorEx flywheelMotor;
    public final DcMotorEx beltMotor;
    public final DcMotorEx intakeMotor;
    public final Servo stopper;
    public static double shooterTargetVelocity = 1450;
    public static double autoFeedRange = 100;

    // Default
    public Operator(HardwareMap hardwareMap) {
        // Set motors and Servos
        flywheelMotor = hardwareMap.get(DcMotorEx.class, "shoot");
        beltMotor = hardwareMap.get(DcMotorEx.class, "belt");
        intakeMotor = hardwareMap.get(DcMotorEx.class, "collect");
        stopper = hardwareMap.get(Servo.class, "stopper");

        beltMotor.setDirection(Direction.REVERSE);

        stopperPosition("closed");
    }

    public void Operate(Gamepad gamepad) {

        runFlywheel();

        if (gamepad.right_trigger > 0.0) {
            shoot(false);
        } else if (gamepad.right_bumper) {
            // Right bumper runs shoot() without stop for emergencies
            shoot(true);
        } else if (gamepad.left_trigger > 0.0) {
            intake();
        } else {
            stopBC();
        }

        TelemetryPacket p = new TelemetryPacket();
        p.put("Flywheel Speed", flywheelMotor.getVelocity());
        p.put("Target Speed", shooterTargetVelocity);
        p.put("Belt Speed", beltMotor.getVelocity());
        p.put("Intake Speed", intakeMotor.getVelocity());
        p.put("Flywheel Current", flywheelMotor.getCurrent(CurrentUnit.MILLIAMPS));
        FtcDashboard.getInstance().sendTelemetryPacket(p);
    }

    public boolean canAutoFeed() {
        return Math.abs(shooterTargetVelocity - flywheelMotor.getVelocity()) < autoFeedRange;
    }

    public void stopperPosition(String stopperPosition) {
        if (Objects.equals(stopperPosition, "open")) {
            stopper.setPosition(0.6);
        }
        else if (Objects.equals(stopperPosition, "closed")) {
            stopper.setPosition(0.1);
        }
        else {
            return;
        }
    }

    public void intake() {
        stopperPosition("closed");
        intakeMotor.setPower(1.0);
        beltMotor.setPower(1.0);
    }

    public void shoot(boolean override) {
        if (!override) {
            // Default behavior, only feed if flywheel is at correct speed
            stopperPosition("open");
            intakeMotor.setPower(0.3);
            beltMotor.setPower(canAutoFeed() ? 1.0 : 0.0);
        } else {
            // Override behavior for when there is a problem with flywheel speed
            stopperPosition("open");
            intakeMotor.setPower(0.3);
            beltMotor.setPower(1.0);
        }
    }

    public void stopBC() {
        intakeMotor.setPower(0.0);
        beltMotor.setPower(0.0);
    }

    public void runFlywheel() {
        flywheelMotor.setVelocity(shooterTargetVelocity);
    }
}