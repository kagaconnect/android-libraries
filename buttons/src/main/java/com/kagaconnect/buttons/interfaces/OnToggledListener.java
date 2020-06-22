package com.kagaconnect.buttons.interfaces;

import com.kagaconnect.buttons.ToggleableView;

public interface OnToggledListener {
    void onSwitched(ToggleableView toggleableView, boolean isOn);
}

