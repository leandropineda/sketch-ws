package com.lpineda.dsketch.data;

import com.lpineda.dsketch.api.Mapping;

public interface MessageReceiver {
    Mapping onMessage(String evt);
}
