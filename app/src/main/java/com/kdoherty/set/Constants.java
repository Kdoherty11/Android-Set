package com.kdoherty.set;

/**
 * Created by kdoherty on 8/17/14.
 */
public final class Constants {

    public static final class Keys {
        public static final String SPF_NAME = "vidsloginExtra";
        public static final String USERNAME = "usernameExtra";
        public static final String PASSWORD= "passwordExtra";
        public static final String TIME = "timeExtra";
        public static final String CPU_DIFFICULTY = "cpuDifficultyExtra";
        public static final String ACTIVE_CARDS = "activeCardsExtra";
        public static final String SET = "setExtra";
        public static final String USER_SCORE = "userScoreExtra";
        public static final String USER_WRONG = "userWrongExtra";
        public static final String CPU_SCORE = "cpuScoreKey";

    }

    public static final class Actions {
        public static final String CPU_PLAYER = "com.kdoherty.services.action.CPU_PLAYER";
        public static final String BROADCAST = "com.kdoherty.services.FIND_CPU_SET";
    }

    public static final class Cpu {
        public static final int DEFAULT = 150;
        public static final int EASY = 175;
        public static final int MEDIUM = 150;
        public static final int HARD = 125;
        public static final int INSANE = 100;
    }



}
