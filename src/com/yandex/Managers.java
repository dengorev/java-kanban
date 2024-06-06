package com.yandex;

import com.yandex.service.HistoryManager;
import com.yandex.service.TaskManager;
import com.yandex.service.InMemoryHistoryManager;
import com.yandex.service.InMemoryTaskManager;

public class Managers {

    private Managers() {
    }

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
