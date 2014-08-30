package com.kdoherty.set.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.kdoherty.set.Constants;
import com.kdoherty.set.model.Card;
import com.kdoherty.set.model.Set;
import com.kdoherty.set.model.SetSolver;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * An {@link android.app.IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class CpuPlayerService extends IntentService {

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see android.app.IntentService
     */
    public static void startActionCpuPlayer(Context context, int cpuDifficulty, ArrayList<Card> cards) {
        Intent intent = new Intent(context, CpuPlayerService.class);
        intent.setAction(Constants.Actions.CPU_PLAYER);
        intent.putExtra(Constants.Keys.CPU_DIFFICULTY, cpuDifficulty);
        intent.putParcelableArrayListExtra(Constants.Keys.ACTIVE_CARDS, cards);
        context.startService(intent);
    }

    public CpuPlayerService() {
        super("CpuPlayerService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (action.equals(Constants.Actions.CPU_PLAYER)) {
                final int cpuDifficulty = intent.getIntExtra(Constants.Keys.CPU_DIFFICULTY, Constants.Cpu.DEFAULT);
                final ArrayList cards =  intent.getParcelableArrayListExtra(Constants.Keys.ACTIVE_CARDS);
                handleActionCpuPlayer(cpuDifficulty, cards);
            }
        }
    }

    /**
     * Handle action CpuPlayer in a background thread
     */
    private void handleActionCpuPlayer(int cpuDifficulty, List<Card> cards) {
        Random random = new Random();
        int num = random.nextInt(cpuDifficulty);

        while (num != 1) {
            num = random.nextInt(cpuDifficulty);

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Set set = SetSolver.findSet(cards);

        Intent broadcast = new Intent(Constants.Actions.CPU_PLAYER);
        broadcast.setAction(Constants.Actions.BROADCAST);
        broadcast.addCategory(Intent.CATEGORY_DEFAULT);
        broadcast.putExtra(Constants.Keys.SET, set);
        sendBroadcast(broadcast);
    }
}
