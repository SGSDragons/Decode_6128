package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.systems.Driver;
import org.firstinspires.ftc.teamcode.systems.OperationSubsystem;
import org.firstinspires.ftc.teamcode.systems.Operator;

@TeleOp(name="Singleplayer OpMode", group="OpMode")
@Config
public class SingleplayerOpMode extends LinearOpMode {
    private ElapsedTime runtime = new ElapsedTime();

    @Override
    public void runOpMode() {


        telemetry.addData("Status", "Initialized");
        telemetry.update();

        final Driver drive = new Driver(hardwareMap);
        final Operator operator = new Operator(hardwareMap);

        // Wait for the game to start (driver presses START)
        waitForStart();
        runtime.reset();

        telemetry.addData("Status", "Running");
        telemetry.update();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {

            drive.Drive(gamepad1, gamepad1);
            operator.Operate(gamepad1, gamepad1);
        }
    }
}