package com.vanity.iqbal.helper;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.vanity.iqbal.factory.ListPoemGenerator;
import com.vanity.iqbal.objects.ListPoem;
import com.vanity.iqbal.objects.Section;
import com.vanity.iqbal.services.NotifyService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import static android.content.Context.ALARM_SERVICE;
import static com.vanity.iqbal.factory.ListPoemGenerator.CreateListPoem;

/**
 * Created by aghumman on 5/1/2018.
 */

public class PoemOfTheDay {

    // Poem of the Day Timing - 10:00pm
    private static int POEM_OF_THE_DAY_HOUR = 22;
    private static int POEM_OF_THE_DAY_MINUTE = 0;

    public static String selectPoemOfTheDay(Context context) {

        // Select a random poem from Editor's Pick
        ListPoem listEditorPick = CreateListPoem(context, ListPoemGenerator.ListPoemTypes.LIST_EDITORS_PICK);

        // Generate list of poemIds from Editor's Pick PoemList
        List<String> listOfPoems = new ArrayList<>();
        ArrayList<Section> sections = listEditorPick.getSections();
        for (int sectionIndex = 0; sectionIndex < sections.size(); sectionIndex++) {
            Section currentSection = sections.get(sectionIndex);
            for (int sectionItemIndex = 0; sectionItemIndex < currentSection.getPoems().size(); sectionItemIndex++) {
                listOfPoems.add(currentSection.getPoems().get(sectionItemIndex).getId());
            }
        }

        // Randomly select one of the poems
        int totalPoems = listOfPoems.size();
        Random random = new Random();
        int randomPoemIndex = random.nextInt(totalPoems);
        return listOfPoems.get(randomPoemIndex);
    }

    public static void createAlarmForPoemOfTheDayNotification(Activity activity) {

        // Try setting an Alarm for Poem of the Day - Can't take risk of crashing for an extra feature
        try {
            // Cancel Previous Repeating Alarm
            Intent intent = new Intent(activity, NotifyService.class);
            PendingIntent sender = PendingIntent.getService(activity, 0, intent, 0);
            AlarmManager alarmManager = (AlarmManager) activity.getSystemService(ALARM_SERVICE);
            alarmManager.cancel(sender);

            // Setting the Alarm - Again
            Intent myIntent = new Intent(activity, NotifyService.class);  //NotifyService
            alarmManager = (AlarmManager) activity.getSystemService(ALARM_SERVICE);
            PendingIntent pendingIntent = PendingIntent.getService(activity, 0, myIntent, 0);

            Calendar calendar = Calendar.getInstance();

            if (POEM_OF_THE_DAY_HOUR > calendar.get(Calendar.HOUR_OF_DAY) || (POEM_OF_THE_DAY_HOUR == (calendar.get(Calendar.HOUR_OF_DAY)) && POEM_OF_THE_DAY_MINUTE > calendar.get(Calendar.MINUTE))) {
                calendar.set(Calendar.HOUR, POEM_OF_THE_DAY_HOUR);
                calendar.set(Calendar.MINUTE, POEM_OF_THE_DAY_MINUTE);
            } else {
                calendar.set(Calendar.HOUR, POEM_OF_THE_DAY_HOUR);
                calendar.set(Calendar.MINUTE, POEM_OF_THE_DAY_MINUTE);
                int next_day = calendar.get(Calendar.DAY_OF_YEAR) + 1;
                calendar.set(Calendar.DAY_OF_YEAR, next_day);
            }
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

        } catch (Exception ex) {
            Toast.makeText(activity.getApplicationContext(), "Unable to set the \'Poem of the Day\' Feature!", Toast.LENGTH_SHORT).show();
        }
    }
}
