package com.consolemaster;

import java.util.List;

public interface Composable {
    List<Canvas> getChildren();

    public int getChildCount();
}
