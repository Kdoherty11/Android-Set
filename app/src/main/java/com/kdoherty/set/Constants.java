package com.kdoherty.set;

/**
 * Created by kdoherty on 8/17/14.
 */
public final class Constants {

    public static final String TAG = "com.kdoherty.set.LogTag";

    public static final class Keys {
        public static final String SPF_LOGIN = "prefsloginExtra";
        public static final String SPF_HIGHSCORE = "prefsHighScoresExtra";
        public static final String SPF_USERNAME = "prefsUsernameExtra";
        public static final String SPF_GAME_STATE = "prefsGameStateExtra";

        public static final String USERNAME = "usernameExtra";
        public static final String PASSWORD= "passwordExtra";
        public static final String TIME = "timeExtra";
        public static final String CPU_DIFFICULTY = "cpuDifficultyExtra";
        public static final String ACTIVE_CARDS = "activeCardsExtra";
        public static final String SET = "setExtra";
        public static final String USER_SCORE = "userScoreExtra";
        public static final String USER_WRONG = "userWrongExtra";
        public static final String CPU_SCORE = "cpuScoreExtra";
        public static final String TARGET = "targetSetExtra";
        public static final String PLAYERS = "multiplayerPlayersExtra";

        public static final String PRACTICE_HIGH_SCORE = "practiceHighScoreExtra";
        public static final String RACE_HIGH_SCORE = "raceHighScoreExtra";
        public static final String ELAPSED_TIME_RACE = "raceElapsedTimeExtra";
        public static final String GAME_MODE = "gameModeExtra";
    }

    public static final class Actions {
        public static final String CPU_PLAYER = "com.kdoherty.services.action.CPU_PLAYER";
        public static final String BROADCAST = "com.kdoherty.services.FIND_CPU_SET";
    }

    public static final class Cpu {
        public static final int DEFAULT = 150;
        public static final int EASY = 250;
        public static final int MEDIUM = 200;
        public static final int HARD = 150;
        public static final int INSANE = 75;
    }

    public static final class Modes {
        public static final String PRACTICE = "practiceMode";
        public static final String RACE = "raceMode";
    }
}
