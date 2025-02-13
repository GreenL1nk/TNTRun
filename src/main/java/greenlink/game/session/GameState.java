package greenlink.game.session;

import lombok.Getter;

@Getter
public enum GameState {
    WAITING("Ожидание игроков"),
    PRE_RUN("Старт игры через &e<time>"),
    RUNNING("Идёт игра"),
    FINISHED("Ожидание игроков");

    final String message;

    GameState(String message) {
        this.message = message;
    }

    public String getWithArg(int time) {
        return message.replace("<time>", String.valueOf(time));
    }
}