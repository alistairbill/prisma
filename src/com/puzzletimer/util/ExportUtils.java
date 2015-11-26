package com.puzzletimer.util;

import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.io.PrintWriter;

import javax.swing.JOptionPane;

import static com.puzzletimer.Internationalization._;
import com.puzzletimer.models.Solution;
import com.puzzletimer.state.SolutionManager;
import com.puzzletimer.statistics.Average;

/**
 * Created by Wes on 12/6/2014.
 */

public class ExportUtils
{
    private static ArrayList<Solution> currentFive;
    private static ArrayList<Solution> currentTwelve;
    private static ArrayList<Solution> currentHundred;

    public static void ExportToFile(SolutionManager solutionManager)
    {
        // clear these out each export so we're not carrying data from previous export
        currentFive = new ArrayList<Solution>();
        currentTwelve = new ArrayList<Solution>();
        currentHundred = new ArrayList<Solution>();

        String fileDate = new SimpleDateFormat("dd-MM-yyyy hhmm a").format(new Date());
        String fileName = fileDate + " " + _("tools.export_filename");

        // we want the solutions to be in reverse order (earliest to latest)
        // so calculated averages are correct (i.e. Ao12 is of the current solve
        // and the 11 previous solves)
        ArrayList<Solution> solutions = new ArrayList<Solution>();
        for (Solution solution : solutionManager.getSolutions()) {
            solutions.add(0, solution);
        }

        try
        {
            String[] columnHeaders = {"Solution Date & Time", "Scramble", "Time Elapsed (seconds)", "Current Ao5",
                    "Current Ao12", "Current Ao100"};

            PrintWriter writer = new PrintWriter(fileName);
            writer.write(StringUtils.join(",", columnHeaders) + "\n");

            for (Solution solution : solutions)
            {
                String date = solution.getTiming().getStart().toString();
                String timeElapsed = SolutionUtils.formatSeconds(solution.getTiming().getElapsedTime(),"MILLISECONDS",false);
                String scramble = solution.getScramble().getRawSequence();

                String ao5 = calculateAverageOfFive(solution);
                String ao12 = calculateAverageOfTwelve(solution);
                String ao100 = calculateAverageOfHundred(solution);

                String[] rowValues = {date, scramble, timeElapsed, ao5, ao12, ao100};
                writer.write(StringUtils.join(",", rowValues) + "\n");
            }

            writer.close();

        } catch (Exception e)
        {
            JOptionPane.showMessageDialog(null, "Unable to export solutions: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String message = solutions.size() + " solutions successfully exported to " + fileName;
        JOptionPane.showMessageDialog(null, message, "Complete!", JOptionPane.PLAIN_MESSAGE);
    }

    private static String calculateAverageOfFive(Solution solution)
    {
        currentFive.add(solution);
        if (currentFive.size() > 5) {
            currentFive.remove(0);
        }
        if (currentFive.size() == 5)
        {
            Average aoFive = new Average(0, 1);
            aoFive.setSolutions(currentFive.toArray(new Solution[currentFive.size()]),false);
            return "" + SolutionUtils.formatSeconds(aoFive.getValue(),"MILLISECONDS",false);
        }
        else {
            return "";
        }
    }

    private static String calculateAverageOfTwelve(Solution solution)
    {
        currentTwelve.add(solution);
        if (currentTwelve.size() > 12) {
            currentTwelve.remove(0);
        }
        if (currentTwelve.size() == 12)
        {
            Average aoTwelve = new Average(0, 1);
            aoTwelve.setSolutions(currentTwelve.toArray(new Solution[currentTwelve.size()]),false);
            return "" + SolutionUtils.formatSeconds(aoTwelve.getValue(),"MILLISECONDS",false);
        }
        else {
            return "";
        }
    }

    private static String calculateAverageOfHundred(Solution solution)
    {
        currentHundred.add(solution);
        if (currentHundred.size() > 100) {
            currentHundred.remove(0);
        }
        if (currentHundred.size() == 100)
        {
            Average aoHundred = new Average(0, 1);
            aoHundred.setSolutions(currentHundred.toArray(new Solution[currentHundred.size()]),false);
            return "" + SolutionUtils.formatSeconds(aoHundred.getValue(),"MILLISECONDS",false);
        }
        else {
            return "";
        }
    }
}
