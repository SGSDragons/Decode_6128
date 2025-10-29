package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.systems.PhysicalDriver;
import org.firstinspires.ftc.teamcode.systems.PhysicalOperator;

@TeleOp(name="6128 DECODE OpMode", group="OpMode")
@Config
public class MainOpMode extends LinearOpMode{

    private ElapsedTime runtime = new ElapsedTime();

    @Override
    public void runOpMode() {

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        final PhysicalDriver driver = new PhysicalDriver(hardwareMap);
        final PhysicalOperator operator = new PhysicalOperator(hardwareMap);

        // Wait for the game to start (driver presses START)
        waitForStart();
        runtime.reset();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            telemetry.addData("Status", "Initialized");
            telemetry.update();

            driver.Drive(gamepad1, gamepad2);
            operator.Operate(gamepad1, gamepad2);
        }
    }
}