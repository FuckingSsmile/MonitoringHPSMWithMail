package ru.hpsm.email;

public class TelegramBotState {
    private BotState state;
    private long userIdForState;

    public TelegramBotState(){
        setActivityBot(BotState.FreeState);
    }

    public void setActivityBot(BotState state) {
        this.state = state;
    }

    public void changeActivityState(String s) {
        if (s.equalsIgnoreCase("/addphone")) {
            setActivityBot(BotState.WaitingNewPhone);
        }
        if (s.equalsIgnoreCase("/removephone")) {
            setActivityBot(BotState.WaitingRemovePhone);
        }
    }

    public void stateFree(){
        setActivityBot(BotState.FreeState);
    }

    public BotState getState() {
        return state;
    }

    public long getUserIdForState() {
        return userIdForState;
    }

    public void setUserIdForState(long userIdForState) {
        this.userIdForState = userIdForState;
    }

    enum BotState{
        FreeState,
        WaitingNewPhone,
        WaitingRemovePhone
    }
}
