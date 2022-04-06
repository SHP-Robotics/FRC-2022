package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.ParallelDeadlineGroup;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

import frc.robot.subsystems.Drive;
import frc.robot.subsystems.Flywheel;
import frc.robot.subsystems.Indexer;
import frc.robot.subsystems.Vision;

public class SimpleAuto extends SequentialCommandGroup {
        public SimpleAuto(Drive drive, Indexer indexer, Flywheel flywheel, Vision vision) {
            addCommands(
                new ParallelDeadlineGroup(
                        new ForwardTimeBased(drive),
                        new RunCommand(() -> {
                                indexer.setGuide(0.8);
                                indexer.intake();
                        }, indexer)
                ),
                new CycleBall(flywheel, vision, indexer)
            );
        }
    }