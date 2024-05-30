package platinpython.vfxgenerator.client.gui.widget;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;

public abstract class UpdateableWidget extends AbstractWidget {
    public UpdateableWidget(int x, int y, int width, int height) {
        super(x, y, width, height, Component.empty());
    }

    public abstract void updateValue();

    protected abstract void updateMessage();
}
