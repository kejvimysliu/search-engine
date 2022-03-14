package org.lucene.project;

import org.lucene.project.service.IServiceExecutor;
import org.lucene.project.service.ServiceExecutorFactory;
import org.lucene.project.utils.AppHelper;
import org.lucene.project.utils.ConsoleAnimation;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        ConsoleAnimation.printAnimation();
        System.out.print("Enter command: ");

        Scanner in = new Scanner(System.in);
        String command = in.nextLine();
        while (!command.equals("close")) {
            String commandToBeExecuted = AppHelper.translateCommand(command);
            IServiceExecutor serviceExecutor = ServiceExecutorFactory.getInstance().getService(commandToBeExecuted);
            serviceExecutor.executeCommand(command);
            command = in.nextLine();
        }

    }
}
